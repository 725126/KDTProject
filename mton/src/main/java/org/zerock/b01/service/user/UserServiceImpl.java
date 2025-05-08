package org.zerock.b01.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public User getUserByEmail(String email) {
        return userRepository.findByuEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
    }

    // 비밀번호 변경 > 비밀번호 일치 여부 확인
    @Override
    public boolean checkPasswordMatch(String currentPassword, String userPassword) {
        return passwordEncoder.matches(currentPassword, userPassword);
    }

    // 비밀번호 변경
    @Override
    public void changePassword(User user, String newPassword) {
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    // 회원정보 수정 (기본정보)
    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 회원정보 수정
        user.updateBasicInfo(dto.getName(), dto.getEmail(), dto.getPhone());

        // 협력업체 회원의 경우 추가 회원정보 수정
        if (dto.getUserRole().equals(UserRole.PARTNER)) {
            Partner partner = partnerRepository.findByUser(user)
                    .orElseThrow(() -> new IllegalArgumentException("협력업체 정보 없음"));

            partner.updateCompanyInfo(dto.getCompanyName(), dto.getAddress());
        }

        // 인증 정보 갱신
        CustomUserDetails updatedUserDetails = new CustomUserDetails(user);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                null,
                updatedUserDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    // 회원 탈퇴
    @Override
    @Transactional
    public void deactivateUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        user.setuIsActive(UserStatus.INACTIVE);

        userLogRepository.save(UserLog.builder()
                .user(user)
                .sActionType("WITHDRAW")
                .sActionContent("탈퇴 사유: " + reason)
                .build());
    }

    // 회원가입 미승인 회원 목록 반환
    @Override
    public List<User> findByStatus(UserStatus status) {
        return userRepository.findByuIsActive(UserStatus.PENDING);
    }

    @Override
    public Page<User> getPendingUsers(Pageable pageable) {
        return userRepository.findByuIsActive(UserStatus.PENDING, pageable);
    }

    @Override
    @Transactional
    public void activateUser(String uEmail) {
        User user = userRepository.findByuEmail(uEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        user.setuIsActive(UserStatus.ACTIVE);
    }
}
