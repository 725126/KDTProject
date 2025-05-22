package org.zerock.b01.dto.warehouse;

import lombok.*;

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

  private Long titemQty;

  private Long amount;

  private String pCompany;

  private Long partnerId;

  public TransactionViewDTO(String orderId, String pCompany, Long partnerId,
                            String matId, String matName,
                            int titemPrice, Long titemQty, Long amount) {
    this.orderId = orderId;
    this.pCompany = pCompany;
    this.partnerId = partnerId;
    this.matId = matId;
    this.matName = matName;
    this.titemPrice = titemPrice;
    this.titemQty = titemQty;
    this.amount = amount;
  }

  // 🔹 입고 완료일 추가 setter (stream map에서 넣어줄 용도)
  public void setIncomingLastCompletedAt(LocalDate incomingLastCompletedAt) {
    this.incomingLastCompletedAt = incomingLastCompletedAt;
  }

}
