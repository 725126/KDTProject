package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;
import org.zerock.b01.domain.operation.Material;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inventory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long inventoryId;

  @ManyToOne
  @JoinColumn(name = "inventory_total_id", nullable = false)
  private InventoryTotal inventoryTotal;

  @ManyToOne
  @JoinColumn(name = "cstorage_id", nullable = false)
  private CompanyStorage companyStorage;

  @ManyToOne
  @JoinColumn(name = "mat_id", nullable = false)
  private Material material;

  @Column(nullable = false)
  private Long totalPrice;

  @Column(nullable = false)
  private int totalQty;

  public void addQty(int qty, long price) {
    this.totalQty += qty;
    this.totalPrice += price;
  }

  public void subtractQty(int qty, long price) {
    if (this.totalQty < qty || this.totalPrice < price) {
      throw new IllegalArgumentException("출고 수량 또는 금액이 재고보다 많습니다.");
    }
    this.totalQty -= qty;
    this.totalPrice -= price;
  }
}
