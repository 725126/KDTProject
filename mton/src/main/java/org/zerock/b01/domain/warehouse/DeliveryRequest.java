package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Ordering;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long drId;

  @ManyToOne
  @JoinColumn(name = "order_id",nullable = false)
  private Ordering ordering;

  @Column(nullable = false)
  private int drTotalQty;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeliveryStatus drStatus;

  public void updateTotalQty(int totalQty) {
    this.drTotalQty = totalQty;
  }

  // 상태를 발주량과 납입 지시 총 수량 비교 후 업데이트하는 메서드
  public void updateStatus() {
    if (this.drTotalQty == this.ordering.getOrderQty()) {
      this.drStatus = DeliveryStatus.완료;
    } else {
      this.drStatus = DeliveryStatus.진행중;
    }
  }

}
