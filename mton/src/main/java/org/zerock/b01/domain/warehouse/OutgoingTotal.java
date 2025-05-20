package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.ProductionPlan;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutgoingTotal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long outgoingTotalId;

  @ManyToOne
  @JoinColumn(name = "mat_id", nullable = false)
  private Material material;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "prdplan_id", nullable = false)
  private ProductionPlan productionPlan;

  private LocalDateTime outgoingFirstDate;

  private LocalDateTime outgoingCompletedAt;

  @Column(nullable = false)
  private int estimatedOutgoingQty;

  @Column(nullable = false)
  private int outgoingTotalQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OutgoingStatus outgoingStatus;
}
