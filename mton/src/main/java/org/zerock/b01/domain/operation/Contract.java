package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.user.Partner;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contract {
    @Id
    private String conId; // [기본키] 계약코드

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner; // [외래키] 협력업체 ID

    @Column(nullable = false)
    private LocalDate conDate; // 계약일

    @Column(nullable = false)
    private LocalDate conEnd; // 만료일
}
