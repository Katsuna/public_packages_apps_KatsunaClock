package com.katsuna.clock.data.source.converters;

import android.arch.persistence.room.TypeConverter;

import com.katsuna.clock.data.AlarmType;

public class AlarmTypeConverter {

    @TypeConverter
    public static AlarmType toType(int type) {
        if (type == AlarmType.ALARM.getCode()) {
            return AlarmType.ALARM;
        } else if (type == AlarmType.REMINDER.getCode()) {
            return AlarmType.REMINDER;
        } else {
            throw new IllegalArgumentException("Could not recognize type");
        }
    }

    @TypeConverter
    public static int toInteger(AlarmType type) {
        return type.getCode();
    }
}