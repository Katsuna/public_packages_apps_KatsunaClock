package com.katsuna.clock.alarms;

import com.katsuna.clock.data.Alarm;

public interface AlarmItemListener {

    void onAlarmEdit(Alarm alarm);

    void onAlarmTurnOff(Alarm alarm);

    void onAlarmDelete(Alarm alarm);
}