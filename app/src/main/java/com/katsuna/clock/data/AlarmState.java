package com.katsuna.clock.data;

public enum AlarmState {
    ACTIVATED(0);

    private final int code;

    AlarmState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}