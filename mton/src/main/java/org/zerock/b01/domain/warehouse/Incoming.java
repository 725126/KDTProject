package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

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
  @JoinColumn(name = "delivery_partner_item_id", nullable = false)
  private DeliveryPartnerItem deliveryPartnerItem;

  @Column(nullable = false)
  private String incomingCode;

  @Column(nullable = true)
  private LocalDateTime incomingCompletedAt;

  @Column(nullable = false)
  private int incomingQty = 0;

  @Column(nullable = false)
  private int incomingReturnQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingStatus incomingStatus;

  @Column(nullable = true)
  private LocalDateTime incomingDate;

}
