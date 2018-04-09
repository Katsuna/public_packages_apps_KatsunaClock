package com.katsuna.clock.data;

public enum AlarmStatus {
    ACTIVE(0),
    INACTIVE(1);

    private final int code;

    AlarmStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}