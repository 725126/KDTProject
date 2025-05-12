package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryRequestItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long drItemId;

  @ManyToOne
  @JoinColumn(name = "dr_id", nullable = false)
  private DeliveryRequest deliveryRequest;

  @ManyToOne
  @JoinColumn(nullable = false)
  private CompanyStorage companyStorage;

  @Column(nullable = false)
  private String drItemCode;

  @Column(nullable = false)
  private int drItemQty;

  @Column(nullable = false)
  private LocalDate drItemDueDate;

  public void updateDeliveryRequestItem(int drItemQty, LocalDate drItemDueDate, CompanyStorage companyStorage) {
    this.drItemQty = drItemQty;
    this.drItemDueDate = drItemDueDate;
    this.companyStorage = companyStorage;
  }

}
