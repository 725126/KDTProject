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
  @JoinColumn(name = "incoming_total_id", nullable = false)
  private IncomingTotal incomingTotal;

  @ManyToOne
  @JoinColumn(name = "delivery_partner_item_id", nullable = false)
  private DeliveryPartnerItem deliveryPartnerItem;

  @Column(nullable = false)
  private String incomingCode;

}
