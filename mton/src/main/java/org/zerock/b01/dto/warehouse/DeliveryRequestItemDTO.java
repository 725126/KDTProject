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
public class DeliveryRequestItemDTO {

  private Long drItemId;

  private Long drId;

  private String drItemCode;

  private int drItemQty;

  private LocalDate drItemDueDate;

  private String cstorageId;

  private LocalDateTime creDate;

  private LocalDateTime modDate;

  private String orderId;

  private String matName;

  private LocalDate orderEnd;

  private int orderQty;

}
