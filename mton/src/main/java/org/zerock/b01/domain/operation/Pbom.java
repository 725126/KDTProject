package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Pbom {
    @Id
    private String pbomId;

    @ManyToOne
    @JoinColumn(name = "mat_id", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "prod_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int pbomQty;
}
