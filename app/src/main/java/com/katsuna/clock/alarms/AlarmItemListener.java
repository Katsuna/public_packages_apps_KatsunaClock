package com.katsuna.clock.alarms;

import android.support.annotation.NonNull;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;

public interface AlarmItemListener {

    void onAlarmFocus(@NonNull Alarm alarm, boolean focus);

    void onAlarmEdit(@NonNull Alarm alarm);

    void onAlarmStatusUpdate(@NonNull Alarm alarm, @NonNull AlarmStatus alarmStatus);

    void onAlarmDelete(@NonNull Alarm alarm);
}