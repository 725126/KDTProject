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
public class Inspection {
    @Id
    private String insId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Date insStart;

    @Column(nullable = false)
    private Date insEnd;

    @Column(nullable = false)
    private int insQty;
}
