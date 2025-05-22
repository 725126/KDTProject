package org.zerock.b01.dto.warehouse;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTotalDTO {

  private Long inventoryTotalId;

  private String matId;

  private String matType;

  private String matName;

  private int allTotalQty;

  private int allIncomingEffectiveQty;

  private int allOutgoingTotalQty;

  private int partnerQty;

  private Long allTotalPrice;
}
