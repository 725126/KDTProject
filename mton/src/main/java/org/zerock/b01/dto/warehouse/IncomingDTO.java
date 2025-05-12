package org.zerock.b01.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomingDTO {

  private Long incomingId;

  private LocalDate deliveryPartnerItemDate;

  private String incomingCode;

  private String pCompany;

  private String matId;

  private String matName;

  private int deliveryPartnerItemQty;

  private String cstorageId;
}
