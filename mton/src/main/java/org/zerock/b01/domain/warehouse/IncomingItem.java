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
public class IncomingItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long incomingItemId;

  @ManyToOne
  @JoinColumn(name = "incoming_id",nullable = false)
  private Incoming incoming;

  @Column(nullable = false)
  private LocalDateTime modifyDate;

  @Column(nullable = false)
  private int incomingQty;

  @Column(nullable = false)
  private int incomingReturnQty;

  @Column(nullable = false)
  private int incomingMissingQty;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingItemStatus incomingItemStatus;

}
