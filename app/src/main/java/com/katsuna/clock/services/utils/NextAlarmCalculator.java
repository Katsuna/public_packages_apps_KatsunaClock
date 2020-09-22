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

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.ArrayList;
import java.util.List;

public class NextAlarmCalculator implements INextAlarmCalculator {

    @Override
    public LocalDateTime getTriggerDateTime(LocalDateTime now, Alarm alarm) {
        LocalDateTime output;

        if (alarm.isRecurring()) {
            output = getNearestTriggerDateTime(now, alarm);
        } else {
            output = LocalDateTime.of(now.toLocalDate(),
                    LocalTime.of(alarm.getHour(), alarm.getMinute()));
            if (output.isBefore(now) || output.isEqual(now)) {
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

        // get nearest datetime
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
        LocalDateTime output;
        if (now.getDayOfWeek() == dayOfWeek) {
            output = LocalDateTime.of(now.toLocalDate(),
                    LocalTime.of(alarm.getHour(), alarm.getMinute()));
            if (output.isBefore(now) || output.isEqual(now)) {
                output = LocalDateTime.of(
                        now.toLocalDate().plusDays(1).with(TemporalAdjusters.next(dayOfWeek)),
                        LocalTime.of(alarm.getHour(), alarm.getMinute()));
            }
        } else {
            output = LocalDateTime.of(
                    now.toLocalDate().with(TemporalAdjusters.next(dayOfWeek)),
                    LocalTime.of(alarm.getHour(), alarm.getMinute()));
        }

        return output;
    }

}
