package com.katsuna.clock.services.utils;

import com.katsuna.clock.data.Alarm;

public interface INextAlarmCalculator {

    long getTriggerTime(Alarm alarm);

}
