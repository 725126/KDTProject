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
public class IncomingTotalDTO {

  private Long incomingTotalId;

  private LocalDateTime incomingCompletedAt;

  private String pCompany;

  private String matId;

  private String matName;

  private int incomingEffectiveQty;

  private String drItemId;

  private String drItemCode;

  private LocalDate drItemDueDate;

  private int drItemQty;



}
