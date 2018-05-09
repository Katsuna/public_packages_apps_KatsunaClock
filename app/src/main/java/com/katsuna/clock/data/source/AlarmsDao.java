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
