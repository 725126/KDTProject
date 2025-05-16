package org.zerock.b01.domain.operation.tablehead;

import lombok.Getter;

@Getter
public enum ProductionPlanTableHead {
    PRDPLAN_ID("생산코드"),
    PROD_ID("제품코드"),
    PROD_NAME("제품명"),
    PRDPLAN_QTY("생산량"),
    PRDPLAN_DATE("계획일자"),
    PRDPLAN_END("생산일"),
    PRDPLAN_STAT("상태")
    ;

    private final String label;

    ProductionPlanTableHead(final String label) {
        this.label = label;
    }
}
