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
public class TransactionViewDTO {

  private String orderId;

  private String matId;

  private String matName;

  private LocalDate incomingLastCompletedAt;

  private int titemPrice;

  private int titemQty;

  private int amount;

  private String pCompany;

}
