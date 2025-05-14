package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Incoming {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long incomingId;

  @ManyToOne
  @JoinColumn(name = "incoming_total_id", nullable = false)
  private IncomingTotal incomingTotal;

  @ManyToOne
  @JoinColumn(name = "delivery_partner_item_id", nullable = false)
  private DeliveryPartnerItem deliveryPartnerItem;

  @Column(nullable = false)
  private String incomingCode;

  @Column(nullable = false)
  private int incomingQty = 0;

  @Column(nullable = false)
  private int incomingReturnQty = 0;

  @Column(nullable = false)
  private int incomingMissingQty  = 0;

  public Incoming updateIncomingQtys(int qty) {
    this.incomingQty = qty;
    this.incomingMissingQty = this.deliveryPartnerItem.getDeliveryPartnerItemQty() - qty;
    return this;
  }

  public Incoming updateIncomingQtyFull(int qty) {
    this.incomingQty = qty;
    this.incomingMissingQty = 0;
    return this;
  }

  public int getRemainingQty() {
    int ordered = this.getDeliveryPartnerItem().getDeliveryPartnerItemQty();
    int received  = this.getIncomingQty() - this.getIncomingMissingQty();
    return ordered - received;
  }

  public void updateIncomingReturnQty(int incomingReturnQty) {
    this.incomingReturnQty = incomingReturnQty;
  }
}
