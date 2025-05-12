package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.Ordering;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IncomingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long incomingItemId;

  @ManyToOne
  @JoinColumn(name = "incoming_id",nullable = false)
  private Incoming incoming;

  @ManyToOne
  @JoinColumn(name = "cstorage_item_id", nullable = false)
  private CompanyStorageItem companyStorageItem;

  @Column(nullable = false)
  private LocalDate incomingItemDate;

  @Column(nullable = false)
  private int incomingItemQty = 0;

  @Column(nullable = false)
  private int incomingItemReturnQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingItemStatus incomingItemStatus;
}
