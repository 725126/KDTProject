package org.zerock.b01.domain.operation.tablehead;

import lombok.Getter;

@Getter
public enum OrderingTableHead {
    ORDER_ID("발주코드"),
    PPLAN_ID("조달계획코드"),
    CMT_ID("계약자재코드"),
    ORDER_QTY("소요량"),
    ORDER_DATE("발주일자"),
    ORDER_END("납기일"),
    ORDER_STAT("상태")
    ;

    private final String label;

    OrderingTableHead(String label) {
        this.label = label;
    }
}
