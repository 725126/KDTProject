package org.zerock.b01.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionPlanDTO {
    private String prdplanId;
    private String prodId;
    private String prodName;
    private int prdplanQty;
    private LocalDate prdplanDate;
    private LocalDate prdplanEnd;
    private String prdplanStatus;
}
