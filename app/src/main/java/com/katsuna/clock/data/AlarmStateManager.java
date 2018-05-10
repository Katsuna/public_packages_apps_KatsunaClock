package com.katsuna.clock.data;

import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlarmStateManager {

    private static final String TAG = "AlarmStateManager";

    private static final AlarmStateManager instance = new AlarmStateManager();
    private final LinkedHashMap<Long, AlarmState> map = new LinkedHashMap<>();

    private AlarmStateManager() {
    }

    public static synchronized AlarmStateManager getInstance() {
        return instance;
    }

    public synchronized boolean alarmActive() {
        for(Map.Entry o: map.entrySet()) {
            if (o.getValue() == AlarmState.ACTIVATED) {
                return true;
            }
        }
        return false;
    }

    public synchronized AlarmState getAlarmState(Alarm alarm) {
        return map.get(alarm.getAlarmId());
    }

    public synchronized void setAlarmState(Alarm alarm, AlarmState state) {
        Log.e(TAG, "setAlarmState: " + alarm);
        map.put(alarm.getAlarmId(), state);
    }

    public synchronized void removeAlarm(Alarm alarm) {
        Log.e(TAG, "removeAlarm: " + alarm);
        map.remove(alarm.getAlarmId());
    }
}
