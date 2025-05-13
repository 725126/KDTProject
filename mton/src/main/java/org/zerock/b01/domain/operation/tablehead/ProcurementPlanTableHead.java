package org.zerock.b01.domain.operation.tablehead;

import lombok.Getter;

@Getter
public enum ProcurementPlanTableHead {
    PPLAN_ID("조달코드"),
    PRDPLAN_ID("계획코드"),
    MAT_ID("자재코드"),
    MAT_NAME("자재명"),
    PPMAT_QTY("소요량"),
    PPLAN_DATE("계획일자"),
    PPLAN_END("납기일"),
    PPLAN_STAT("상태")
    ;

    private final String label;

    ProcurementPlanTableHead(String label) {
        this.label = label;
    }
}
