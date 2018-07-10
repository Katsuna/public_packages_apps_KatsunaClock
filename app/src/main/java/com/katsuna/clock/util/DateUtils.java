package com.katsuna.clock.util;

import android.support.annotation.NonNull;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoUnit;

public class DateUtils {

    public static long toEpochMillis(@NonNull LocalDateTime dateTime) {
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(dateTime);
        return dateTime.toInstant(currentOffsetForMyZone).toEpochMilli();
    }

    public static TimeDiff calculateTimeDiff(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        TimeDiff timeDiff = new TimeDiff();

        LocalDateTime tempDateTime = LocalDateTime.from( fromDateTime );

        long years = tempDateTime.until( toDateTime, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears( years );

        long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths( months );

        long days = tempDateTime.until( toDateTime, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays( days );

        long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours( hours );

        long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes( minutes );

        long seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS);

        timeDiff.years = years;
        timeDiff.months = months;
        timeDiff.days = days;
        timeDiff.hours = hours;
        timeDiff.minutes = minutes;
        timeDiff.seconds = seconds;

        return timeDiff;
    }

}
