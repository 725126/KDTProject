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
public class DeliveryPartnerItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long deliveryPartnerItemId;

  @ManyToOne
  @JoinColumn(name = "delivery_partner_id", nullable = false)
  private DeliveryPartner deliveryPartner;

  @Column(nullable = false)
  private int deliveryPartnerItemQty;

  @Column(nullable = false)
  private LocalDateTime deliveryPartnerItemDate;


}
