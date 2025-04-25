package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Material;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inventory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int inventoryId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private CompanyStorage companyStorage;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Material material;

  @Column(nullable = false)
  private int totalPrice;

  @Column(nullable = false)
  private int totalQty;

  @Column(nullable = false)
  private int availableQty;
}
