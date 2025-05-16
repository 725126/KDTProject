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
public class IncomingItemDTO {

  private Long incomingItemId;

  private String incomingCode;

  private LocalDateTime modifyDate;

  private String pCompany;

  private String matId;

  private String matName;

  private int incomingQty;

  private int incomingReturnQty;

  private int incomingMissingQty;

  private String incomingItemStatus;


}
