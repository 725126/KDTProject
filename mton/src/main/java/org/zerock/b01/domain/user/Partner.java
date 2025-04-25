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
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    private String pCompany;

    private String pAddr;

    private String pBusinessNo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // (외래키) 회원 ID
}
