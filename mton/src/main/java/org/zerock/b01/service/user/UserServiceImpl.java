package org.zerock.b01.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zerock.b01.domain.user.*;
import org.zerock.b01.dto.user.*;
import org.zerock.b01.repository.user.PartnerRepository;
import org.zerock.b01.repository.user.UserLogRepository;
import org.zerock.b01.repository.user.UserRepository;
import org.zerock.b01.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final UserLogRepository userLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public boolean createUser(UserCreateDTO request) {
        // 아이디(이메일) 중복 확인
        if (isExistedUEmail(request.getUEmail())) { // 아이디 중복 ○
            return false;
        }

        // 비밀번호 & 비밀번호 확인 일치 여부 검증
        if (!request.getUPassword().equals(request.getUPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 기본 회원 정보 저장
        User user = User.builder()
                .uEmail(request.getUEmail())
                .uPassword(passwordEncoder.encode(request.getUPassword()))
                .uName(request.getUName())
                .uPhone(request.getUPhone())
                .userRole(UserRole.valueOf(request.getUserRole().toString()))
                .uIsActive(UserStatus.PENDING)
                .build();

        userRepository.save(user);

        // 협력업체 회원가입의 경우 > 추가 정보 저장
        if (request.getUserRole().equals(UserRole.PARTNER)) {
            PartnerCreateDTO partnerCreateDTO = request.getPartnerCreateDTO();

            Partner partner = Partner.builder()
                    .pCompany(partnerCreateDTO.getPCompany())
                    .pBusinessNo(partnerCreateDTO.getPBusinessNo())
                    .pAddr(partnerCreateDTO.getPAddr())
                    .user(user)
                    .build();

            partnerRepository.save(partner);
        }

        // 다시 저장 후 확인도 uEmail로
        return isExistedUEmail(user.getUEmail());
    }

    // 이메일(회원 아이디) 중복 여부 확인
    @Override
    public boolean isExistedUEmail(String email) {
        return userRepository.existsByuEmail(email);
    }

    // 비밀번호 재설정 메일 발송
    @Override
    public void sendResetPasswordEmail(String email) {
        User user = userRepository.findByuEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("등록된 이메일이 없습니다."));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetLink = "http://localhost:8080/reset/pw?token=" + token;

        emailService.sendEmail(user.getUEmail(), "[자재ON] 비밀번호 재설정 요청",
                "비밀번호를 재설정하려면 아래 링크를 클릭하세요.\n" + resetLink);
    }

    // 비밀번호 재설정 > 토큰 유효성 확인
    @Override
    public boolean checkResetTokenValid(String token) {
        return userRepository.findByResetToken(token).isPresent();
    }

    // 비밀번호 재설정 > 비밀번호 변경 
    @Override
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new UsernameNotFoundException("유효하지 않은 토큰입니다."));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    // 사업자등록번호 중복 확인
    @Override
    public boolean isBusinessNoAvailable(String businessNo) {
        return !partnerRepository.existsBypBusinessNo(businessNo);
    }

    // 이메일(회원 아이디 찾기)
    @Override
    public Optional<String> findEmailByFindIdDTO(FindIdDTO dto) {
        UserRole roleEnum = UserRole.valueOf(dto.getUserRole());
        Optional<User> userOptional = userRepository.findUserByNamePhoneAndRole(dto.getUName(), dto.getUPhone(), roleEnum);

        if (userOptional.isPresent()) {
            // 회원종류가 협력업체인 경우 사업자등록번호 검사
            if (roleEnum.equals(UserRole.PARTNER)) {
              Optional<Partner> partnerOptional = partnerRepository.findBypBusinessNo(dto.getPBusinessNo());
              if (partnerOptional.isEmpty()) {
                  return Optional.empty();
              }
            }
        }
        return userOptional.map(User::getUEmail);
    }

    // (이메일로) 회원 정보 불러오기
    @Override
    public UserResponseDTO getUserInfoByEmail(String email) {
        User user = userRepository.findByuEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .userId(user.getUserId())
                .uEmail(user.getUEmail())
                .uName(user.getUName())
                .uPhone(user.getUPhone())
                .userRole(user.getUserRole())
                .uIsActive(user.getUIsActive())
                .build();

        // 협력업체 회원인 경우 > 추가 회원 정보 조회 후 저장
        if(user.getUserRole().equals(UserRole.PARTNER)) {
            Partner partner = partnerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("협력업체 회원 정보를 찾을 수 없습니다."));

            PartnerResponseDTO partnerResponseDTO = PartnerResponseDTO.builder()
                    .partnerId(partner.getPartnerId())
                    .pCompany(partner.getPCompany())
                    .pBusinessNo(partner.getPBusinessNo())
                    .pAddr(partner.getPAddr())
                    .build();

            userResponseDTO.setPartnerResponse(partnerResponseDTO);
        }

        return userResponseDTO;
    }

    // (회원 ID(일련번호)로) 회원 정보 불러오기
    @Override
    public UserResponseDTO getUserInfoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                .userId(user.getUserId())
                .uEmail(user.getUEmail())
                .uName(user.getUName())
                .uPhone(user.getUPhone())
                .userRole(user.getUserRole())
                .uIsActive(user.getUIsActive())
                .build();

        // 협력업체 회원인 경우 > 추가 회원 정보 조회 후 저장
        if(user.getUserRole().equals(UserRole.PARTNER)) {
            Partner partner = partnerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("협력업체 회원 정보를 찾을 수 없습니다."));

            PartnerResponseDTO partnerResponseDTO = PartnerResponseDTO.builder()
                    .partnerId(partner.getPartnerId())
                    .pCompany(partner.getPCompany())
                    .pBusinessNo(partner.getPBusinessNo())
                    .pAddr(partner.getPAddr())
                    .build();

            userResponseDTO.setPartnerResponse(partnerResponseDTO);
        }

        return userResponseDTO;
    }

    @Override
    public Partner findByPartner(User user) {
        return partnerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("협력업체 회원 정보를 찾을 수 없습니다."));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByuEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

    /**
     * [공통] 현재 비밀번호 일치 여부 확인
     * - 사용자 입력값(현재 비밀번호)과 저장된 암호화된 비밀번호 비교
     *
     * @param currentPassword 사용자가 입력한 현재 비밀번호 (평문)
     * @param userPassword    DB에 저장된 암호화된 비밀번호
     * @return 비밀번호가 일치하면 true, 일치하지 않으면 false
     */
    @Override
    public boolean checkPasswordMatch(String currentPassword, String userPassword) {
        return passwordEncoder.matches(currentPassword, userPassword);
    }

    /**
     * [공통] 비밀번호 변경 처리
     * - 새 비밀번호를 암호화하여 저장
     *
     * @param user        대상 사용자 객체
     * @param newPassword 새 비밀번호 (평문)
     */
    @Override
    public void changePassword(User user, String newPassword) {
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    /**
     * [공통] 회원 기본정보 수정
     * - 이름, 이메일, 연락처, 회원 역할 등 수정
     * - 협력업체(PARTNER) 회원은 회사 정보도 함께 수정
     * - 현재 로그인된 사용자 본인일 경우, 인증 정보도 갱신
     *
     * @param userId 수정 대상 회원의 ID
     * @param dto    수정할 회원 정보 DTO
     */
    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 회원정보 수정
        user.updateBasicInfo(dto.getName(), dto.getEmail(), dto.getPhone(), dto.getUserRole());

        // 협력업체 회원의 경우 추가 회원정보 수정
        if (dto.getUserRole().equals(UserRole.PARTNER)) {
            Partner partner = partnerRepository.findByUser(user)
                    .orElseThrow(() -> new IllegalArgumentException("협력업체 정보 없음"));

            partner.updateCompanyInfo(dto.getCompanyName(), dto.getAddress());
        }

        // 인증 정보 갱신
        // 인증 정보 갱신은 본인일 때만 수행
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null && currentAuth.getPrincipal() instanceof CustomUserDetails currentUserDetails) {
            // 현재 로그인한 사용자의 ID가 수정 대상과 같을 때만 인증 정보 갱신
            if (currentUserDetails.getUserId().equals(user.getUserId())) {
                CustomUserDetails updatedUserDetails = new CustomUserDetails(user);
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        updatedUserDetails,
                        null,
                        updatedUserDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }

    }

    /**
     * [공통] 회원 탈퇴 처리
     * - 회원 상태를 INACTIVE(비활성화)로 변경
     * - 탈퇴 사유를 회원 로그(UserLog)에 기록
     *
     * @param userId 탈퇴할 회원의 ID
     * @param reason 탈퇴 사유 (예: 자발적 탈퇴, 미사용 등)
     */
    @Override
    @Transactional
    public void deactivateUser(Long userId, String reason) {
        // [1] 사용자 조회 (없으면 예외 발생)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        // [2] 상태 변경: ACTIVE → INACTIVE (비활성화 처리)
        user.setuIsActive(UserStatus.INACTIVE);

        // [3] 탈퇴 사유 로그 기록 (DELETE 타입)
        userLogRepository.save(UserLog.builder()
                .user(user)
                .sActionType("DELETE")
                .sActionContent("[탈퇴] 탈퇴 사유: " + reason)
                .build());
    }

    // 회원가입 미승인 회원 목록 반환
    @Override
    public List<User> findByStatus(UserStatus status) {
        return userRepository.findByuIsActive(UserStatus.PENDING);
    }

    /**
     * [관리자] 가입 승인 대기 사용자 목록 조회
     * - 상태값이 PENDING인 사용자만 조회
     * - 페이징 적용
     *
     * @param pageable 페이징 정보
     * @return 가입 대기 상태의 회원 목록
     */
    @Override
    public Page<User> getPendingUsers(Pageable pageable) {
        return userRepository.findByuIsActive(UserStatus.PENDING, pageable);
    }

    /**
     * [관리자] 가입 승인 처리
     * - 이메일 기준으로 사용자 조회
     * - 상태값을 ACTIVE로 변경
     *
     * @param uEmail 승인 대상 사용자의 이메일
     */
    @Override
    @Transactional
    public void activateUser(String uEmail) {
        User user = userRepository.findByuEmail(uEmail)
                .orElseThrow(() -> new IllegalArgumentException("승인 대상 유저 없음."));

        user.setuIsActive(UserStatus.ACTIVE);
    }

    /**
     * [관리자] 가입 승인 대기 사용자 목록 조회
     * - 상태: 고정값(PENDING)
     * - 조건: 이름(keyword), 회원 종류(role)
     */
    @Override
    public Page<User> getPendingUsersWithFilter(String keyword, String role, Pageable pageable) {
        // [1] 이름 + 회원 종류 둘 다 있을 경우
        if (StringUtils.hasText(keyword) && StringUtils.hasText(role)) {
            try {
                // [2] 역할 문자열을 enum으로 변환
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                // [3] 상태: PENDING + 역할 + 이름 조건으로 검색
                return userRepository.searchByStatusAndRoleAndName(UserStatus.PENDING, userRole, keyword, pageable);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 회원 종류 파라미터: {}", role);
                // 잘못된 enum이면 빈 페이지 반환
                return Page.empty(pageable);
            }
        // [4] 이름만 있을 경우
        } else if (StringUtils.hasText(keyword)) {
            return userRepository.searchByStatusAndName(
                    UserStatus.PENDING, keyword, pageable);
        // [5] 회원 종류만 있을 경우
        } else if (StringUtils.hasText(role)) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                return userRepository.searchByStatusAndRole(UserStatus.PENDING, userRole, pageable);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 회원 종류 파라미터: {}", role);
                return Page.empty(pageable);
            }
            // [6] 아무 필터도 없으면 상태값 PENDING 전체 조회
        } else {
            return userRepository.findByuIsActive(UserStatus.PENDING, pageable);
        }
    }

    /**
     * [관리자] 회원 상태 필터 없이 이름 + 역할 기반 검색
     * - 상태 필터 없음 (e.g. ACTIVE/INACTIVE 구분 안함)
     * - 조건: 이름(keyword), 회원 종류(role)
     */
    @Override
    public Page<User> findUsersByKeywordAndRole(String keyword, String role, Pageable pageable) {
        // [1] 회원 종류가 있으면 필터링 수행
        if (StringUtils.hasText(role)) {
            try {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                // [2] 회원 종류 + 이름(있으면)으로 검색
                return userRepository.searchAllUsers(
                        StringUtils.hasText(keyword) ? keyword : null,
                        userRole,
                        pageable);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 회원 역할: {}", role);
                // 회원 종류 값이 enum으로 변환 안되면 빈 결과 반환
                return Page.empty(pageable);
            }
        // [3] 회원 종류 없이 이름만 있을 경우
        } else {
            return userRepository.searchAllUsers(
                    StringUtils.hasText(keyword) ? keyword : null,
                    null, // 회원 종류 없음
                    pageable);
        }
    }

    /**
     * [관리자] 회원 목록 페이지 필터링 검색
     * - 전체 회원 대상 (가입 승인 완료된 사용자 포함)
     * - 조건: 계정 상태(status), 이름(keyword), 회원 역할(role)
     */
    @Override
    public Page<User> findUsersByFilters(String keyword, String role, String status, Pageable pageable) {
        // [1] 변수 초기화: 역할과 상태는 enum으로 변환할 예정
        UserRole userRole = null;
        UserStatus userStatus = null;

        // [2] 회원 종류가 넘어온 경우 처리 (예: "PARTNER" → UserRole.PARTNER)
        if (StringUtils.hasText(role)) {
            try {
                // enum으로 안전 변환
                userRole = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 잘못된 값일 경우 경고 로그 출력
                log.warn("잘못된 회원 역할: {}", role);
            }
        }

        // [3] 계정 상태가 넘어온 경우 처리 (예: "ACTIVE" → UserStatus.ACTIVE)
        if (StringUtils.hasText(status)) {
            try {
                userStatus = UserStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 계정 상태: {}", status);
            }
        } else {
            // [4] status 파라미터 없으면 기본 ACTIVE만 조회
            userStatus = UserStatus.ACTIVE;
        }

        return userRepository.searchUsers(userStatus, userRole,
                StringUtils.hasText(keyword) ? keyword : null, pageable);
    }

//    @Override
//    public List<UserLog> getAllLogsSortedByDateDesc() {
//        List<UserLog> userLogs = userLogRepository.findAll(Sort.by(Sort.Direction.DESC, "creDate"));
//
//        return userLogs;
//    }

    @Override
    public Page<UserLog> getPagedLogsSortedByDateDesc(Pageable pageable) {
        return userLogRepository.findAll(PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "creDate")
        ));
    }

    public void saveUserLog(User actor, String actionType, String content) {
        UserLog log = UserLog.builder()
                .user(actor)
                .sActionType(actionType)
                .sActionContent(content)
                .build();

        userLogRepository.save(log);
    }

}
