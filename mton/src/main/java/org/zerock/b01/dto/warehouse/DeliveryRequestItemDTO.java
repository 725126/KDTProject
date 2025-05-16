package org.zerock.b01.dto.warehouse;

import com.fasterxml.jackson.annotation.JsonFormat;
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

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime creDate;

  private LocalDateTime modDate;

  private String orderId;

  private String matName;

  private LocalDate orderEnd;

  private int orderQty;

}
