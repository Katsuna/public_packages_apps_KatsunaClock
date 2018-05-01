package com.katsuna.clock.services.utils;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.TemporalAdjusters;

public class NextAlarmCalculatorTest {


    private NextAlarmCalculator mNextAlarmCalculator;

    @Before
    public void start() {
        mNextAlarmCalculator = new NextAlarmCalculator();
    }

    @After
    public void stop() {
        mNextAlarmCalculator = null;
    }

    @Test
    public void emptyHourAlarmInput_returnsValidationResult() {
        Alarm alarm = new Alarm(AlarmType.ALARM, 13, 30, null, true,
                false, false, false, false, false, false, AlarmStatus.ACTIVE);

        Alarm mondayAlarm = new Alarm(AlarmType.ALARM, 12, 0, null, true,
                false, false, false, false, false, false, AlarmStatus.ACTIVE);

        Alarm mondayAndFridayAlarm = new Alarm(AlarmType.ALARM, 12, 0, null, true,
                false, false, false, true, false, false, AlarmStatus.ACTIVE);

        Alarm sundayAlarm = new Alarm(AlarmType.ALARM, 12, 0, null, true,
                false, false, false, false, false, false, AlarmStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();


        System.out.println("now: " + now);

        if (alarm.isRecurring()) {
            if (alarm.isMondayEnabled()) {
                LocalDateTime nextMonday = LocalDateTime.of(
                        now.toLocalDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY)),
                        LocalTime.of(alarm.getHour(), alarm.getMinute()));
                System.out.println("nextMonday: " + nextMonday);
            }


        } else {
            LocalDateTime alarmDate = LocalDateTime.of(now.toLocalDate(),
                    LocalTime.of(alarm.getHour(), alarm.getMinute()));
            if (alarmDate.isBefore(now)) {
                alarmDate = alarmDate.plusDays(1);
            }
            System.out.println("alarmDate: " + alarmDate);
        }

/*
        System.out.println("now.getDayOfWeek(): " + now.getDayOfWeek());
        System.out.println("now.getDayOfWeek(): " + now.with(TemporalAdjusters.previous(now.getDayOfWeek())));
        System.out.println("nextMonday: " + nextMonday);*/


        // given invalid input
        // validator returns results
/*        List<ValidationResult> results = mValidator.validateTime("", "34");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_hour);*/
    }


}
