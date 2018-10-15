package com.katsuna.clock.formatters;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDateTime;

public class TodayFormatter {

    public static boolean isToday(DayOfWeek dayOfWeek) {
        return dayOfWeek == LocalDateTime.now().getDayOfWeek();
    }

}
