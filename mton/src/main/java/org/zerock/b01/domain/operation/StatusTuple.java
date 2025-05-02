package org.zerock.b01.domain.operation;

import lombok.Getter;

@Getter
public class StatusTuple {
    private boolean success;
    private String message;

    public StatusTuple(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
