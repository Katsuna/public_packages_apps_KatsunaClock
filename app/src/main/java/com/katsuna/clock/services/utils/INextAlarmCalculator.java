package com.katsuna.clock.services.utils;

import com.katsuna.clock.data.Alarm;

import org.threeten.bp.LocalDateTime;

interface INextAlarmCalculator {

    LocalDateTime getTriggerDateTime(LocalDateTime now, Alarm alarm);
}
