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

  @Column(nullable = false)
  private String outgoingCode;

  private LocalDateTime outgoingCompletedAt;

}
