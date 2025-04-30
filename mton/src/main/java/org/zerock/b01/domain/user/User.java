package org.zerock.b01.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userRole", "uPassword"})
public class User extends BaseEntity { // 회원 (기본 회원 정보)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 회원 ID (일련번호)

    private String uEmail; // 이메일 (회원 아이디로 사용)

    private String uPassword; // 비밀번호

    private String uName; // 이름

    private String uPhone; // 연락처

    @Enumerated(EnumType.STRING)
    private UserStatus uIsActive; // 계정 활성 상태 (활성화, 비활성화)

    @Enumerated(EnumType.STRING)
    private UserRole userRole; // 역할 (생산부서(내부), 구매부서(내부), 협력 업체(외부), 관리자(내부))

    private String resetToken;

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setPassword(String uPassword) {
        this.uPassword = uPassword;
    }
}
