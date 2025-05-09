package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.domain.user.UserStatus;
import org.zerock.b01.repository.user.PartnerRepository;
import org.zerock.b01.repository.user.UserRepository;

@Configuration
@RequiredArgsConstructor
public class DummyDataLoader {

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner loadDummyUsers() {
        return args -> {
            // ✅ prod1 계정이 이미 있으면 더미 생성 스킵
            if (userRepository.findByuEmail("prod1@example.com").isPresent()) {
                System.out.println("ℹ️ 더미 유저 이미 존재함. 생성 스킵됨.");
                return;
            }

            // 1. 생산부서 10명
            for (int i = 1; i <= 10; i++) {
                userRepository.save(User.builder()
                        .uEmail("prod" + i + "@example.com")
                        .uPassword(passwordEncoder.encode("prod1234"))
                        .uName("생산직원" + i)
                        .uPhone("010-1000-" + String.format("%04d", i))
                        .uIsActive(UserStatus.PENDING)
                        .userRole(UserRole.PRODUCTION)
                        .build());
            }

            // 2. 구매부서 10명
            for (int i = 1; i <= 10; i++) {
                userRepository.save(User.builder()
                        .uEmail("purch" + i + "@example.com")
                        .uPassword(passwordEncoder.encode("purch1234"))
                        .uName("구매직원" + i)
                        .uPhone("010-2000-" + String.format("%04d", i))
                        .uIsActive(UserStatus.PENDING)
                        .userRole(UserRole.PURCHASING)
                        .build());
            }

            // 3. 협력업체 10명 + Partner 테이블 연결
            for (int i = 1; i <= 10; i++) {
                User partnerUser = userRepository.save(User.builder()
                        .uEmail("partner" + i + "@example.com")
                        .uPassword(passwordEncoder.encode("partner1234"))
                        .uName("협력사담당" + i)
                        .uPhone("010-3000-" + String.format("%04d", i))
                        .uIsActive(UserStatus.PENDING)
                        .userRole(UserRole.PARTNER)
                        .build());

                partnerRepository.save(Partner.builder()
                        .pCompany("협력업체" + i + "주식회사")
                        .pAddr("서울시 중구 협력로 " + i + "길")
                        .pBusinessNo("110-12-" + String.format("%05d", i))
                        .user(partnerUser)
                        .build());
            }

            System.out.println("✅ 더미 유저 30명 생성 완료 (관리자 제외)");
        };
    }
}


