package org.zerock.b01.dto.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractMaterialDTO {
    private String cmtId;
    private String conId;
    private String matId;
    private int cmtPrice;
    private int cmtReq;
}
