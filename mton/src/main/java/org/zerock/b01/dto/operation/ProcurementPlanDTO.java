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
public class ProcurementPlanDTO {
    private String pplanId;
    private String prdplanId;
    private String materialId;
    private String materialName;
    int ppmatQty;
    LocalDate pplanDate;
    LocalDate pplanEnd;
    private String pplanStat;
}
