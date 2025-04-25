package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionItem {
    @Id
    private String titemId;

    @ManyToOne
    @JoinColumn(name = "tran_id", nullable = false)
    private Transaction tran;

    @ManyToOne
    @JoinColumn(name = "mat_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private int titemPrice;

    @Column(nullable = false)
    private int titemQty;

    @Column(nullable = false)
    private String titemStore;
}
