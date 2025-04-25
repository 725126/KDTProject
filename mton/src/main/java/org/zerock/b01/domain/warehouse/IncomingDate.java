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
public class IncomingDate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int incomingDateId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private IncomingItem incomingItem;

  private LocalDateTime incomingDate;

  @Column(nullable = false)
  private int incomingDateQty = 0;

  @Column(nullable = false)
  private int incomingDateReturnQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IncomingDateStatus incomingDateStatus;
}
