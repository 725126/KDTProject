package org.zerock.b01.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partner { // 협력업체 (추가 회원 정보)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId; // 협력업체 ID

    private String pCompany; // 회사명

    private String pAddr; // 회사 주소

    private String pBusinessNo; // 사업자 번호

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // (외래키) 회원 ID
}
