package com.katsuna.clock.data.source.converters;

import android.arch.persistence.room.TypeConverter;

import com.katsuna.clock.data.AlarmStatus;

public class AlarmStatusConverter {

    @TypeConverter
    public static AlarmStatus toStatus(int status) {
        if (status == AlarmStatus.ACTIVE.getCode()) {
            return AlarmStatus.ACTIVE;
        } else if (status == AlarmStatus.INACTIVE.getCode()) {
            return AlarmStatus.INACTIVE;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static int toInteger(AlarmStatus status) {
        return status.getCode();
    }
}