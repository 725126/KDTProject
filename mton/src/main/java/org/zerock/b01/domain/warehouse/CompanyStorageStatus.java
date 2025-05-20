package org.zerock.b01.domain.warehouse;

import lombok.Getter;

@Getter
public enum CompanyStorageStatus {
    INCOMING("입고"),
    OUTGOING("출고"),
    OTHER("기타");

    private final String displayName;

    CompanyStorageStatus(String displayName) {
        this.displayName = displayName;
    }

}
