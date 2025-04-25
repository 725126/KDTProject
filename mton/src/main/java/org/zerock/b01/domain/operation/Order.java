package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    @Id
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "cmt_id", nullable = false)
    private ContractMaterial contractMaterial;

    @ManyToOne
    @JoinColumn(name = "pplan_id", nullable = false)
    private ProcurementPlan procurementPlan;

    @Column(nullable = false)
    private Date orderDate;

    @Column(nullable = false)
    private Date orderEnd;

    @Column(nullable = false)
    private int orderQty;

    @Column(name = "order_stat", nullable = false)
    @Check(constraints = "order_stat IN ('진행중', '완료')")
    private String orderStat;
}
