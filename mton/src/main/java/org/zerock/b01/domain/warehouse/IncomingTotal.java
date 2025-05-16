package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncomingTotal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long incomingTotalId;

  @ManyToOne
  @JoinColumn(name = "dr_item_id", nullable = false)
  private DeliveryRequestItem deliveryRequestItem;

  private LocalDateTime incomingFirstDate;

  private LocalDateTime incomingCompletedAt;

  @Column(nullable = false)
  private int incomingEffectiveQty = 0 ;

  @Column(nullable = false)
  private int incomingTotalQty = 0 ;

  @Column(nullable = false)
  private int incomingReturnTotalQty = 0 ;

  @Column(nullable = false)
  private int incomingMissingTotalQty = 0 ;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingStatus incomingStatus;

  // 상태를 업데이트하는 메서드 추가
  public void updateIncomingStatus(IncomingStatus newStatus) {
    this.incomingStatus = newStatus;
  }

  public void markAsCompleted() {
    this.incomingCompletedAt = LocalDateTime.now();
    this.incomingStatus = IncomingStatus.입고마감;
  }

  public void markFirstIncoming() {
    if (this.incomingFirstDate == null) {
      this.incomingFirstDate = LocalDateTime.now();
    }
  }

  public void updateEffectiveQty() {
    this.incomingEffectiveQty = this.incomingTotalQty
            - this.incomingReturnTotalQty
            - this.incomingMissingTotalQty;
  }


  public void addToTotalAndMissingTotalQty(int totalQty, int missingQty) {
    this.incomingTotalQty += totalQty;
    this.incomingMissingTotalQty += missingQty;
    updateEffectiveQty();
  }

  public void addToReturnTotalQty(int returnQty) {
    this.incomingReturnTotalQty += returnQty;
    updateEffectiveQty();
  }

}
