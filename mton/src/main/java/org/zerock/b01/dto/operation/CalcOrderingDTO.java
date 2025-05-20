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
public class CalcOrderingDTO {
    private String pplanId;
    private String cmtId;
    private int ppmatQty;
    private int cmtReq;
    private LocalDate pplanEnd;
}
