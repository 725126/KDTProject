package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.warehouse.IncomingTotal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long titemId;

    @ManyToOne
    @JoinColumn(name = "tran_id", nullable = false)
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name ="order_id" , nullable = false)
    private Ordering ordering;

    @Column(nullable = false)
    private String matId;

    @Column(nullable = false)
    private String matName;

    @Column(nullable = false)
    private int titemPrice;

    @Column(nullable = false)
    private int titemQty;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String remark;
}
