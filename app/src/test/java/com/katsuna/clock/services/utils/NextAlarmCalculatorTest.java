package com.katsuna.clock.services.utils;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;

import static junit.framework.Assert.assertTrue;

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
                false, false, AlarmStatus.ACTIVE);
        LocalDateTime expectedTriggerTime = now.plusHours(2);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertTrue(triggerTime.equals(expectedTriggerTime));
    }

    @Test
    public void nonRecurringAlarmBeforePresentTime_getScheduledNextDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 10, 30, null, false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE);
        LocalDateTime expectedTriggerTime = now.plusDays(1).minusHours(2);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertTrue(triggerTime.equals(expectedTriggerTime));
    }

    @Test
    public void nonRecurringAlarmAtTheSamePresentTime_getScheduledNextDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 12, 30, null, false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE);
        LocalDateTime expectedTriggerTime = now.plusDays(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertTrue(triggerTime.equals(expectedTriggerTime));
    }

    @Test
    public void recurringAlarmAfterPresentTime_getScheduledInTheSameDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 30, null, false, false, true, false, false,
                false, false, AlarmStatus.ACTIVE);
        LocalDateTime expectedTriggerTime = now.plusHours(2);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertTrue(triggerTime.equals(expectedTriggerTime));
    }

    @Test
    public void recurringAlarmBeforePresentTime_getScheduledInTheSameDayNextWeek() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 10, 30, null, false, false, true, false, false,
                false, false, AlarmStatus.ACTIVE);
        LocalDateTime expectedTriggerTime = now.minusHours(2).plusWeeks(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertTrue(triggerTime.equals(expectedTriggerTime));
    }

    @Test
    public void recurringAlarmAtTheSamePresentTime_getScheduledInTheSameDayNextWeek() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 12, 30, null, false, false, true, false, false,
                false, false, AlarmStatus.ACTIVE);
        LocalDateTime expectedTriggerTime = now.plusWeeks(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertTrue(triggerTime.equals(expectedTriggerTime));
    }

    @Test
    public void recurringAlarmWithAllDaysAtTheSamePresentTime_getScheduledAtNextDay() {
        // setup
        Alarm alarm = new Alarm(AlarmType.ALARM, 12, 30, null, true, true, true, true, true,
                true, true, AlarmStatus.ACTIVE);
        LocalDateTime expectedTriggerTime = now.plusDays(1);

        // execute
        // calculate triggerTime
        LocalDateTime triggerTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        // evaluate
        assertTrue(triggerTime.equals(expectedTriggerTime));
    }
}
