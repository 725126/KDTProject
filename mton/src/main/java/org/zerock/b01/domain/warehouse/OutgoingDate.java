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
public class OutgoingDate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int outgoingDateId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private OutgoingItem outgoingItem;

  private LocalDateTime outgoingDate;

  @Column(nullable = false)
  private int outgoingDateQty = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OutgoingDateStatus outgoingDateStatus;
}
