package org.zerock.b01.domain.operation.tablehead;

import lombok.Getter;

@Getter
public enum ProductTableHead {
    PROD_ID("상품코드"),
    PROD_NAME("상품명"),
    PROD_MEASURE("규격"),
    PROD_UNIT("단위"),
    PROD_EXPLAIN("설명")
    ;

    private final String label;

    ProductTableHead(String label) {
        this.label = label;
    }
}
