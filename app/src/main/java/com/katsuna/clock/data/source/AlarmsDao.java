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
    @Query("SELECT * FROM alarms WHERE entryid = :alarmId")
    Alarm getAlarmById(String alarmId);

    /**
     * Insert an alarm in the database. If the alarm already exists, replace it.
     *
     * @param alarm the alarm to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlarm(Alarm alarm);

    /**
     * Update a task.
     *
     * @param alarm alarm to be updated
     * @return the number of alarms updated. This should always be 1.
     */
    @Update
    int updateAlarm(Alarm alarm);

    /**
     * Delete a task by id.
     *
     * @return the number of alarms deleted. This should always be 1.
     */
    @Query("DELETE FROM alarms WHERE entryid = :alarmId")
    int deleteAlarmById(String alarmId);

}
