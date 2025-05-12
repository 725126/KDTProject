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

  private int drItemQty;

  private int deliveryPartnerItemQty;

  private int incomingQty;

  private int incomingReturnQty;

  private LocalDateTime incomingDate;

  private LocalDateTime incomingCompletedAt;

  private String incomingStatus;

  private String cstorageId;





  private LocalDateTime creDate;


}
