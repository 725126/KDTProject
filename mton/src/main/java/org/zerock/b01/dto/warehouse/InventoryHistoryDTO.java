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
public class InventoryHistoryDTO {

  private Long inventoryHistoryId;

  private String matId;

  private String matType;

  private String matName;

  private LocalDateTime updateDate;

  private String updateReason;

  private int changeQty;

  private Long changePrice;

  private int allTotalQty;

  private Long allTotalPrice;

}
