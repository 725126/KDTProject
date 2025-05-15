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
public class CalcPbomDTO {
    private String matId;
    private String prdplanId;
    private int pbomQty;
    private int pbomMaxQty;
    private LocalDate prdplanEnd;
}
