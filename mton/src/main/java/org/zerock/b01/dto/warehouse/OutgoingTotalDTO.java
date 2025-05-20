package org.zerock.b01.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingTotalDTO {

  private Long outgoingTotalId;

  private LocalDateTime outgoingFirstDate;

  private LocalDateTime outgoingCompletedAt;

  private String prdplanId;

  private LocalDate prdplanEnd;

  private String matId;

  private String matName;

  private int estimatedOutgoingQty;

  private int outgoingTotalQty;

  private String outgoingStatus;
}
