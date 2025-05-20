package org.zerock.b01.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutgoingDTO {

  private Long outgoingId;

  private Long outgoingTotalId;

  private LocalDateTime outgoingFirstDate;

  private LocalDateTime outgoingCompletedAt;

  private Long prdplanId;

  private String matId;

  private String matName;

  private int prdplanQty;

  private int outgoingTotalQty;

  private String outgoingStatus;

  private Long inventoryId;

  private String cstorageId;

  private int availableQty;

  private String outgoingCode;

  private LocalDateTime ougoingDate;

  private int outgoingQty;

}
