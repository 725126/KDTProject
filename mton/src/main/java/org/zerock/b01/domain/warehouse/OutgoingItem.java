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
  private Long outgoingItemId;

  @ManyToOne
  @JoinColumn(name = "outgoing_id", nullable = false)
  private Outgoing outgoing;

  @ManyToOne
  @JoinColumn(name = "mat_id", nullable = false)
  private Material material;

  @ManyToOne
  @JoinColumn(name = "prdplan_id", nullable = false)
  private ProductionPlan productionPlan;

  @ManyToOne
  @JoinColumn(name = "cstorage_id", nullable = false)
  private CompanyStorage companyStorage;

  @Column(nullable = false)
  private int outgoingQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OutgoingStatus outgoingStatus;
}
