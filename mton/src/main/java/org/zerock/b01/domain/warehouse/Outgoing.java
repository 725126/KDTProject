package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Outgoing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long outgoingId;

  @ManyToOne
  @JoinColumn(name = "outgoing_total_id", nullable = false)
  private OutgoingTotal outgoingTotal;

  @ManyToOne
  @JoinColumn(name = "inventory_id", nullable = false)
  private Inventory inventory;

  @Column(nullable = false)
  @Builder.Default
  private int outgoingQty = 0;

  @Column(nullable = false)
  private String outgoingCode;

  @Column(nullable = false)
  private LocalDateTime outgoingDate;

}
