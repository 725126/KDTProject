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
public class IncomingInspectionDTO {

  private Long incomingId;

  private LocalDate deliveryPartnerItemDate;

  private String incomingCode;

  private String pCompany;

  private String matId;

  private String matName;

  private int deliveryPartnerItemQty;

  private int incomingQty;

  private int incomingReturnQty;

  private int incomingMissingQty;

  private LocalDateTime incomingFirstDate;

  private String cstorageId;

  private String incomingStatus;

}
