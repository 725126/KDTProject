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

  @ManyToOne
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

  // 상태를 업데이트하는 메서드 추가
  public void updateOutgoingStatus(OutgoingStatus newStatus) {
    this.outgoingStatus = newStatus;
  }

  public void markAsCompleted() {
    this.outgoingCompletedAt = LocalDateTime.now();
    this.outgoingStatus = OutgoingStatus.출고마감;
  }

  public void markFirstOutgoing() {
    if (this.outgoingFirstDate == null) {
      this.outgoingFirstDate = LocalDateTime.now();
    }
  }

  public void addToTotalQty(int totalQty) {
    this.outgoingTotalQty += totalQty;
  }
}
