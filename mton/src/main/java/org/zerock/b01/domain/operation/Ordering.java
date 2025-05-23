package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Ordering {
    @Id
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "cmt_id", nullable = false)
    private ContractMaterial contractMaterial;

    @ManyToOne
    @JoinColumn(name = "pplan_id", nullable = false)
    private ProcurementPlan procurementPlan;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private LocalDate orderEnd;

    @Column(nullable = false)
    private int orderQty;

    @Column(nullable = false)
    private boolean transIssued; // 거래명세 발행 여부

    @Column(name = "order_stat", nullable = false)
    @Check(constraints = "order_stat IN ('진행중', '완료', '취소', '발주중', '취소대기', '완료요청')")
    private String orderStat;


    public void markAsCompleted() {
        this.orderStat = "완료";
    }

    public void changeOrderStat(String stat) {
        this.orderStat = stat;
    }

    public void setTransIssued(boolean transIssued) {
        this.transIssued = transIssued;
    }
}
