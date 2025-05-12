package org.zerock.b01.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {
    private String matId;
    private String matName;
    private String matType;
    private String matMeasure;
    private String matUnit;
    private String matExplain;
}
