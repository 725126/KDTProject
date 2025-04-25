package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.Order;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncomingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int incomingItemId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Incoming incoming;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Material material;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Order order;

  @ManyToOne
  @JoinColumn(nullable = false)
  private DeliveryRequest deliveryRequest;

  @Column(nullable = false)
  private int incomingQty;

  @Column(nullable = false)
  private int incomingReturnQty;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingStatus incomingStatus;
}
