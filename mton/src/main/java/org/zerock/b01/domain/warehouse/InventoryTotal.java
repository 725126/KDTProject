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
public class InventoryTotal extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long inventoryTotalId;

  @ManyToOne
  @JoinColumn(name = "mat_Id",nullable = false)
  private Material material;

  @Column(nullable = false)
  private int allTotalQty;

  @Column(nullable = false)
  private Long allTotalPrice;

  public void addQty(int qty, Long price) {
    this.allTotalQty += qty;
    this.allTotalPrice += price;
  }

  public void subtractQty(int qty, Long price) {
    this.allTotalQty -= qty;
    this.allTotalPrice -= price;
  }
}
