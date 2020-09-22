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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.katsuna.clock.data.Alarm;

import java.util.List;

@Dao
public interface AlarmsDao {

    /**
     * Select all alarms from the alarms table.
     *
     * @return all alarms.
     */
    @Query("SELECT * FROM alarms")
    List<Alarm> getAlarms();

    /**
     * Select an alarm by id.
     *
     * @param alarmId the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM alarms WHERE alarmId = :alarmId")
    Alarm getAlarmById(long alarmId);

    /**
     * Insert an alarm in the database. If the alarm already exists, replace it.
     *
     * @param alarm the alarm to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAlarm(Alarm alarm);

    /**
     * Update an alarm.
     *
     * @param alarm alarm to be updated
     */
    @Update
    void updateAlarm(Alarm alarm);

    /**
     * Delete an alarm by id.
     */
    @Query("DELETE FROM alarms WHERE alarmId = :alarmId")
    void deleteAlarmById(long alarmId);

}
