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
public class Inspection {
    @Id
    private String insId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Ordering ordering;

    @Column(nullable = false)
    private LocalDate insStart;

    @Column(nullable = false)
    private LocalDate insEnd;

    @Column(nullable = false)
    private int insQty;
}
