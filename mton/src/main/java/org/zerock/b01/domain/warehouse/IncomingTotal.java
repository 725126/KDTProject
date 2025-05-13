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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingStatus incomingStatus;

  // 상태를 업데이트하는 메서드 추가
  public void updateIncomingStatus(IncomingStatus newStatus) {
    this.incomingStatus = newStatus;
  }

  // 상태를 기준으로 업데이트할 수 있는 빌더 메서드 추가
  public IncomingTotal updateStatusAndReturn(IncomingStatus newStatus) {
    return IncomingTotal.builder()
            .incomingTotalId(this.incomingTotalId)
            .deliveryRequestItem(this.deliveryRequestItem)
            .incomingCompletedAt(this.incomingCompletedAt)
            .incomingTotalQty(this.incomingTotalQty)
            .incomingReturnTotalQty(this.incomingReturnTotalQty)
            .incomingStatus(newStatus)  // 새로운 상태 설정
            .build();
  }
}
