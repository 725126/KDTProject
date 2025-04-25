package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @Column(nullable = false)
    private Date prdplanDate;

    @Column(nullable = false)
    private Date prdplanEnd;
}
