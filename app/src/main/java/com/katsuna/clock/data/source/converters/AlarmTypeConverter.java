/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
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