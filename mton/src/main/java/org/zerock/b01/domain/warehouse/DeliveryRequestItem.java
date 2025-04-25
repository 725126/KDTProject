package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryRequestItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int drItemId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private DeliveryRequest deliveryRequest;

  @ManyToOne
  @JoinColumn(nullable = false)
  private CompanyStorage companyStorage;

  @Column(nullable = false)
  private int drItemQty;

  @Column(nullable = false)
  private int drItemReceivedQty = 0;

  @Column(nullable = false)
  private Date drItemDueDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeliveryItemStatus drItemStatus;

}
