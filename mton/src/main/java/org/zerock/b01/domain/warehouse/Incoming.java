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
public class Incoming {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int incomingId;

  @Column(nullable = false)
  private String incomingCode;

  private LocalDateTime incomingCompletedAt;

  @ManyToOne
  @JoinColumn(nullable = false)
  private CompanyStorage companyStorage;

}
