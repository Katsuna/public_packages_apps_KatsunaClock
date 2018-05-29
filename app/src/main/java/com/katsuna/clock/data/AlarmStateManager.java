package com.katsuna.clock.data;

import com.katsuna.clock.LogUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlarmStateManager {

    private static final String TAG = AlarmStateManager.class.getSimpleName();

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
        LogUtils.i("%s setAlarmState:  %s", TAG, alarm);
        map.put(alarm.getAlarmId(), state);
    }

    public synchronized void removeAlarm(Alarm alarm) {
        LogUtils.i("%s removeAlarm: %s", TAG, alarm);
        map.remove(alarm.getAlarmId());
    }
}
