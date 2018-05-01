package com.katsuna.clock.services.utils;

import android.util.Log;

import com.katsuna.clock.data.Alarm;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.ArrayList;
import java.util.List;

public class NextAlarmCalculator implements INextAlarmCalculator {

    private static final String TAG = "NextAlarmCalculator";

    @Override
    public long getTriggerTime(Alarm alarm) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime output = getTriggerDateTime(now, alarm);

        Log.e(TAG, "getTriggerTime output: " + output);

        ZoneId systemZone = ZoneId.systemDefault(); // my timezone
        ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(now);

        return output == null ? 0 : output.toInstant(currentOffsetForMyZone).toEpochMilli();
    }

    private LocalDateTime getTriggerDateTime(LocalDateTime now, Alarm alarm) {
        LocalDateTime output;

        if (alarm.isRecurring()) {
            output = getNearestTriggerDateTime(now, alarm);
        } else {
            output = LocalDateTime.of(now.toLocalDate(),
                    LocalTime.of(alarm.getHour(), alarm.getMinute()));
            if (output.isBefore(now)) {
                output = output.plusDays(1);
            }
        }

        return output;
    }

    private LocalDateTime getNearestTriggerDateTime(LocalDateTime now, Alarm alarm) {
        List<LocalDateTime> nearestDays = new ArrayList<>();
        if (alarm.isMondayEnabled()) {
            nearestDays.add(getNearestDay(now, DayOfWeek.MONDAY, alarm));
        }
        if (alarm.isTuesdayEnabled()) {
            nearestDays.add(getNearestDay(now, DayOfWeek.TUESDAY, alarm));
        }
        if (alarm.isWednesdayEnabled()) {
            nearestDays.add(getNearestDay(now, DayOfWeek.WEDNESDAY, alarm));
        }
        if (alarm.isThursdayEnabled()) {
            nearestDays.add(getNearestDay(now, DayOfWeek.THURSDAY, alarm));
        }
        if (alarm.isFridayEnabled()) {
            nearestDays.add(getNearestDay(now, DayOfWeek.FRIDAY, alarm));
        }
        if (alarm.isSaturdayEnabled()) {
            nearestDays.add(getNearestDay(now, DayOfWeek.SATURDAY, alarm));
        }
        if (alarm.isSundayEnabled()) {
            nearestDays.add(getNearestDay(now, DayOfWeek.SUNDAY, alarm));
        }

        LocalDateTime output = null;
        for (LocalDateTime dateTime : nearestDays) {
            if (output == null) {
                output = dateTime;
            } else if (dateTime.isBefore(output)) {
                output = dateTime;
            }
        }

        return output;
    }

    private LocalDateTime getNearestDay(LocalDateTime now, DayOfWeek dayOfWeek, Alarm alarm) {
        LocalDateTime output = LocalDateTime.of(now.toLocalDate(),
                LocalTime.of(alarm.getHour(), alarm.getMinute()));
        if (output.isBefore(now)) {
            output = LocalDateTime.of(
                    now.toLocalDate().with(TemporalAdjusters.next(dayOfWeek)),
                    LocalTime.of(alarm.getHour(), alarm.getMinute()));
        }

        return output;
    }

}
