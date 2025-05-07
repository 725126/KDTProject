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
public class Pbom {
    @Id
    private String pbomId;

    @ManyToOne
    @JoinColumn(name = "mat_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "prod_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @Column(nullable = false)
    private int pbomQty;
}
