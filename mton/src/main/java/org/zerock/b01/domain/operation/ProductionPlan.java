package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductionPlan {
    @Id
    private String prdplanId;

    @ManyToOne
    @JoinColumn(name = "prod_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int prdplanQty;

    @Column(nullable = false, updatable = false)
    private LocalDate prdplanDate;

    @Column(nullable = false)
    private LocalDate prdplanEnd;

    @Column(nullable = false)
    @Builder.Default
    private String prdplanStat = "진행중";

    public void changeStat(String stat) {
        this.prdplanStat = stat;
    }
}
