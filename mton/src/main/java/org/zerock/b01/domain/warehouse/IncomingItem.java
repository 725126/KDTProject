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
  private int incomingQty = 0; // 에러 발생했던 구간

  @Column(nullable = false)
  private int incomingReturnQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingStatus incomingStatus;
}
