package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;
import org.zerock.b01.domain.operation.Order;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryRequest extends BaseEntity {

  @Id
  private String drId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Order order;

  @Column(nullable = false)
  private Date drDate;

  @Column(nullable = false)
  private Date drDueDate;

  @Column(nullable = false)
  private int drTotalQty;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DeliveryStatus drStatus;

}
