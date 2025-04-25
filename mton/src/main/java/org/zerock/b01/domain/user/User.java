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
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 일련번호

    private String uEmail; // 회원 아이디

    private String uPassword;

    private String uName;

    private String uPhone;

    private String uIsActive;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;
}
