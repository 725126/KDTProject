package org.zerock.b01.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.domain.user.UserStatus;
import org.zerock.b01.dto.user.PartnerCreateDTO;
import org.zerock.b01.dto.user.PartnerResponseDTO;
import org.zerock.b01.dto.user.UserCreateDTO;
import org.zerock.b01.dto.user.UserResponseDTO;
import org.zerock.b01.repository.user.PartnerRepository;
import org.zerock.b01.repository.user.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final BCryptPasswordEncoder passwordEncoder;
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
                .uIsActive(UserStatus.ACTIVE)
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
    public Optional<String> findEmailByNameAndPhone(String name, String phone) {
        return userRepository.findByNameAndPhone(name, phone)
                .map(User::getUEmail); // 이메일만 Optional로 감싸서 리턴
    }

    // 회원 정보 불러오기
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



}
