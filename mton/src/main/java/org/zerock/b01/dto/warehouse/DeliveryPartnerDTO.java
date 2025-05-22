package org.zerock.b01.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPartnerDTO {

  private Long deliveryPartnerId;

  private int deliveryPartnerQty;

  private String deliveryPartnerStatus;

  private String drItemCode;

  private String orderId;

  private String matName;

  private int drItemQty;

  private LocalDate drItemDueDate;

  private int incomingReturnTotalQty;

  private int incomingMissingTotalQty;

  private Long partnerId;

  public int getRemainingQty() {
    return drItemQty - deliveryPartnerQty + incomingReturnTotalQty + incomingMissingTotalQty;
  }
}
