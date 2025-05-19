package org.zerock.b01.domain.operation.tablehead;

import lombok.Getter;

@Getter
public enum InspectionTableHead {
    INS_ID("검수코드"),
    ORD_ID("발주코드"),
    ;

    private final String label;

    InspectionTableHead(String label) {
        this.label = label;
    }
}
