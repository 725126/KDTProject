package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.domain.user.UserStatus;
import org.zerock.b01.repository.user.UserRepository;

@Configuration
@RequiredArgsConstructor
public class InitAdminUser { // 관리자 로그인
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            String adminEmail = "sjw725126@gmail.com";
            if (userRepository.findByuEmail(adminEmail).isEmpty()) {
                User admin = User.builder()
                        .uEmail(adminEmail)
                        .uPassword(passwordEncoder.encode("1234"))  // 비밀번호 반드시 암호화
                        .uName("관리자")
                        .uPhone("010-1234-5678")
                        .uIsActive(UserStatus.ACTIVE)
                        .userRole(UserRole.ADMIN)
                        .build();

                userRepository.save(admin);
                System.out.println("✅ 초기 관리자 계정 생성 완료");
            }
        };
    }
}
