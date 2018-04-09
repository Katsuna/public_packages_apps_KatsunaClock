package com.katsuna.clock.data;

public enum AlarmType {
    ALARM(0),
    REMINDER(1);

    private final int code;

    AlarmType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}