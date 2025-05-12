package org.zerock.b01.domain.operation.tablehead;

import lombok.Getter;

@Getter
public enum MaterialTableHead {
    MAT_ID("자재코드"),
    MAT_NAME("자재명"),
    MAT_TYPE("종별"),
    MAT_MEASURE("규격"),
    MAT_UNIT("단위"),
    MAT_EXPLAIN("설명")
    ;

    private final String label;

    MaterialTableHead(String label) {
        this.label = label;
    }
}
