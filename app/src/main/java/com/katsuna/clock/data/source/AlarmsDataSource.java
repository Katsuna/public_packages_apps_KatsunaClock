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
