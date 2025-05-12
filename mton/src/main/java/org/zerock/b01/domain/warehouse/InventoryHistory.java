package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Material;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long inventoryHistoryId;

  @ManyToOne
  @JoinColumn(name = "inventory_id", nullable = false)
  private Inventory inventory;

  @ManyToOne
  @JoinColumn(name = "cstorage_id", nullable = false)
  private CompanyStorage companyStorage;

  @ManyToOne
  @JoinColumn(name = "mat_id", nullable = false)
  private Material material;

  @Column(nullable = false)
  private int changeQty;

  @Column(nullable = false)
  private Long changePrice;

  @Column(nullable = false)
  private LocalDateTime updateDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InventoryUpdateReason updateReason;
}
