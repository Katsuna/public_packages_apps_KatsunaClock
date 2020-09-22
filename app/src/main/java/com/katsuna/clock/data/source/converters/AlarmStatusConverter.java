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