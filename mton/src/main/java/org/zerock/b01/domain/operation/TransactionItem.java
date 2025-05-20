package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"transaction", "material"})
public class TransactionItem {
    @Id
    private String titemId; // 거래명세 항목 ID (UUID 등)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tran_id", nullable = false)
    private Transaction transaction; // 거래명세서

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mat_id", nullable = false)
    private Material material; // 자제 정보

    @Column(nullable = false)
    private int titemPrice; // 수량

    @Column(nullable = false)
    private int titemQty; // 단가

    @Column(nullable = false)
    private String titemStore; // 입고 창고 ID

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
