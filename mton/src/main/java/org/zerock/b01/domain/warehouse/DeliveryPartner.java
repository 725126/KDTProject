package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "incomingItem")
public class DeliveryPartner extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long deliveryPartnerId;

  @ManyToOne
  @JoinColumn(name = "dr_item_id", nullable = false)
  private DeliveryRequestItem deliveryRequestItem;

  @ManyToOne
  @JoinColumn(name = "incoming_id", nullable = false)
  private Incoming incoming;

  @Column(nullable = false)
  private int deliveryPartnerQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeliveryPartnerStatus deliveryPartnerStatus;

  // 출고 수량 업데이트 메서드
  public void updateDeliveryPartnerQty(int qtyToAdd) {
    // 발주된 총 수량
    int drItemQty = deliveryRequestItem.getDrItemQty();
    // 반품 수량 (incomingItem이 없으면 0)
    int incomingReturnQty = (incoming != null)
            ? incoming.getIncomingReturnQty()
            : 0;
    // 남은 수량 = 발주수량 - 이미 입고된 수량 + 반품 수량
    int remainingQty = drItemQty - this.deliveryPartnerQty + incomingReturnQty;

    if (qtyToAdd > remainingQty) {
      throw new IllegalArgumentException(
              String.format("남은 수량(%d)을 초과할 수 없습니다. 요청 수량: %d", remainingQty, qtyToAdd)
      );
    }

    this.deliveryPartnerQty += qtyToAdd;
  }

  // 출고 상태 업데이트 메서드
  public void updateDeliveryPartnerStatus(DeliveryPartnerStatus status) {
    this.deliveryPartnerStatus = status; // 상태 변경
  }

  public void updateDeliveryRequestItem(DeliveryRequestItem deliveryRequestItem) {
    this.deliveryRequestItem = deliveryRequestItem;
  }
}
