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

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class DummyDataLoader { // 회원 계정 생성 (400개)

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final PasswordEncoder passwordEncoder;

    private final Set<String> usedNames = new HashSet<>();
    private final Random random = new Random();

    private static final List<String> LAST_NAMES = Arrays.asList(
            "김", "이", "박", "최", "정", "강", "조", "윤", "장", "임",
            "한", "오", "서", "신", "권", "황", "안", "송", "류", "홍"
    );

    private static final List<String> FIRST_NAMES = Arrays.asList(
            "지우", "서준", "하린", "예준", "하윤", "민재", "서연", "지호", "예린", "민석",
            "도윤", "지아", "유진", "시후", "은우", "수빈", "태윤", "유하", "도경", "다윤",
            "지안", "채원", "윤재", "세윤", "지후", "서연", "이안", "하린", "이담", "지민",
            "태연", "도현", "서아", "하준", "도연", "하은", "주원", "민지", "세현", "다연",
            "서진", "태영", "지유", "시현", "다희", "가온", "하윤", "예진", "이준", "이서"
    );

    @Bean
    public CommandLineRunner loadDummyUsers() {
        return args -> {
            if (userRepository.findByuEmail("prod_pending1@example.com").isPresent()) {
                System.out.println("ℹ️ 더미 유저 이미 존재함. 생성 스킵됨.");
                return;
            }

            // 총 400명 생성
            createUsers("prod_pending", UserRole.PRODUCTION, UserStatus.PENDING, 50, false);
            createUsers("prod_active", UserRole.PRODUCTION, UserStatus.ACTIVE, 50, false);
            createUsers("purch_pending", UserRole.PURCHASING, UserStatus.PENDING, 50, false);
            createUsers("purch_active", UserRole.PURCHASING, UserStatus.ACTIVE, 50, false);
            createUsers("partner_pending", UserRole.PARTNER, UserStatus.PENDING, 100, true);
            createUsers("partner_active", UserRole.PARTNER, UserStatus.ACTIVE, 100, true);

            System.out.println("✅ 더미 유저 400명 생성 완료 (관리자 제외)");
        };
    }

    private void createUsers(String emailPrefix, UserRole role, UserStatus status, int count, boolean isPartner) {
        for (int i = 1; i <= count; i++) {
            String email = emailPrefix + i + "@example.com";
            if (userRepository.findByuEmail(email).isPresent()) continue;

            String phonePrefix = (role == UserRole.PRODUCTION) ? "010-1000-" :
                    (role == UserRole.PURCHASING) ? "010-2000-" : "010-3000-";

            String name = generateUniqueKoreanName();

            User user = userRepository.save(User.builder()
                    .uEmail(email)
                    .uPassword(passwordEncoder.encode("1234rr"))
                    .uName(name)
                    .uPhone(phonePrefix + String.format("%04d", i))
                    .uIsActive(status)
                    .userRole(role)
                    .build());

            if (isPartner) {
                partnerRepository.save(Partner.builder()
                        .pCompany("협력주식회사")
                        .pAddr("서울시 중구 협력로 " + i + "길")
                        .pBusinessNo("110-12-" + String.format("%05d", i))
                        .user(user)
                        .build());
            }
        }
    }

    private String generateUniqueKoreanName() {
        String name;
        do {
            String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
            String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
            name = lastName + firstName;
        } while (usedNames.contains(name));
        usedNames.add(name);
        return name;
    }
}



