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
package com.katsuna.clock.data.source;

import android.support.annotation.NonNull;

import com.katsuna.clock.data.Alarm;

import java.util.List;

/**
 * Main entry point for accessing alarms data.
 * <p>
 * For simplicity, only getAlarms() and getAlarm() have callbacks. Consider adding callbacks to other
 * methods to inform the user of database errors or successful operations.
 */
public interface AlarmsDataSource {

    void getAlarms(@NonNull LoadAlarmsCallback callback);

    void getAlarm(long alarmId, @NonNull GetAlarmCallback callback);

    Alarm getAlarm(long alarmId);

    void saveAlarm(@NonNull Alarm alarm);

    void deleteAlarm(long alarmId);

    interface LoadAlarmsCallback {

        void onAlarmsLoaded(List<Alarm> alarms);

        void onDataNotAvailable();
    }

    interface GetAlarmCallback {

        void onAlarmLoaded(Alarm alarm);

        void onDataNotAvailable();
    }
}
