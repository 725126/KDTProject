package org.zerock.b01.domain.operation.tablehead;

import lombok.Getter;

@Getter
public enum PbomTableHead {
    PBOM_ID("품목등록코드"),
    MAT_ID("자재코드"),
    PROD_ID("상품코드"),
    PBOM_QTY("소요량")
    ;

    private final String label;

    PbomTableHead(String label) {
        this.label = label;
    }
}
