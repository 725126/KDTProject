package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContractMaterial {
    @Id
    private String cmtId; // [기본키] 계약자재코드

    @ManyToOne
    @JoinColumn(name = "con_id", nullable = false)
    private Contract contract; // [외래키] 계약코드

    @ManyToOne
    @JoinColumn(name = "mat_id", nullable = true) // nullable = false > nullable = true
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Material material; // [외래키] 자재코드

    @Column(nullable = false)
    private int cmtPrice; // 단가(원)

    @Column(nullable = false)
    private int cmtReq; // 소요일(일)

    @Column(nullable = false)
    private int cmtQty; // 수량

    @Column(length = 100, nullable = false)
    private String cmtExplains; // 합의내용
}
