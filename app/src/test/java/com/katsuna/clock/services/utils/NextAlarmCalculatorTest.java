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
package com.katsuna.clock.services.utils;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class NextAlarmCalculatorTest {


    private NextAlarmCalculator mNextAlarmCalculator;

    private LocalDateTime now;

    @Before
    public void start() {
        mNextAlarmCalculator = new NextAlarmCalculator();
        // configure now as 2018-May-2 12:30:00  (Wednesday)
        now = LocalDateTime.of(2018, 5, 2, 12, 30);
    }

    @After
    public void stop() {
        mNextAlarmCalculator = null;
        now = null;
    }

    @Test
    public void nonRecurringAlarmAfterPresentTime_getScheduledInTheSameDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 30, null, false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);
        LocalDateTime expectedTriggerTime = now.plusHours(2);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertEquals(triggerTime, expectedTriggerTime);
    }

    @Test
    public void nonRecurringAlarmBeforePresentTime_getScheduledNextDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 10, 30, null, false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);
        LocalDateTime expectedTriggerTime = now.plusDays(1).minusHours(2);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertEquals(triggerTime, expectedTriggerTime);
    }

    @Test
    public void nonRecurringAlarmAtTheSamePresentTime_getScheduledNextDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 12, 30, null, false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);
        LocalDateTime expectedTriggerTime = now.plusDays(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertEquals(triggerTime, expectedTriggerTime);
    }

    @Test
    public void recurringAlarmAfterPresentTime_getScheduledInTheSameDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 30, null, false, false, true, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);
        LocalDateTime expectedTriggerTime = now.plusHours(2);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertEquals(triggerTime, expectedTriggerTime);
    }

    @Test
    public void recurringAlarmBeforePresentTime_getScheduledInTheSameDayNextWeek() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 10, 30, null, false, false, true, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);
        LocalDateTime expectedTriggerTime = now.minusHours(2).plusWeeks(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertEquals(triggerTime, expectedTriggerTime);
    }

    @Test
    public void recurringAlarmAtTheSamePresentTime_getScheduledInTheSameDayNextWeek() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 12, 30, null, false, false, true, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);
        LocalDateTime expectedTriggerTime = now.plusWeeks(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertEquals(triggerTime, expectedTriggerTime);
    }

    @Test
    public void recurringAlarmWithAllDaysAtTheSamePresentTime_getScheduledAtNextDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 12, 30, null, true, true, true, true, true,
                true, true, AlarmStatus.ACTIVE, "ringtone", false);
        LocalDateTime expectedTriggerTime = now.plusDays(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertEquals(triggerTime, expectedTriggerTime);
    }
}
