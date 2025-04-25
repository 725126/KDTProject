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
  private int inventoryHistoryId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Inventory inventory;

  @ManyToOne
  @JoinColumn(nullable = false)
  private CompanyStorage companyStorage;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Material material;

  @Column(nullable = false)
  private int changeQty;

  @Column(nullable = false)
  private int changePrice;

  @Column(nullable = false)
  private LocalDateTime updateDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InventoryUpdateReason updateReason;
}
