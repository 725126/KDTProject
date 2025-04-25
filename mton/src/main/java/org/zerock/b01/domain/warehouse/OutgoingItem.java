package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.ProductionPlan;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OutgoingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int outgoingItemId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Outgoing outgoing;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Material material;

  @ManyToOne
  @JoinColumn(nullable = false)
  private ProductionPlan productionPlan;

  @Column(nullable = false)
  private int outgoingQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OutgoingStatus outgoingStatus;
}
