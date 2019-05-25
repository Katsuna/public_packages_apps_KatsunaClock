package com.katsuna.clock.util;

import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    @Test
    public void toEpochMillisTest() {
        LocalDateTime now = LocalDateTime.now();

        long millis = DateUtils.toEpochMillis(now);

        LocalDateTime nowFromMillis = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis),
                ZoneId.systemDefault());

        assertEquals(now, nowFromMillis);
    }

    @Test
    public void calculateTimeDiffTest() {

        int secs = 10;
        int minutes = 30;
        int hours = 2;
        int days = 3;
        int months = 5;
        int years = 7;

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime target = now.plusSeconds(secs);
        target = target.plusMinutes(minutes);
        target = target.plusHours(hours);
        target = target.plusDays(days);
        target = target.plusMonths(months);
        target = target.plusYears(years);

        TimeDiff timeDiff = DateUtils.calculateTimeDiff(now, target);

        Assert.assertEquals(timeDiff.seconds, secs);
        Assert.assertEquals(timeDiff.minutes, minutes);
        Assert.assertEquals(timeDiff.hours, hours);
        Assert.assertEquals(timeDiff.days, days);
        Assert.assertEquals(timeDiff.months, months);
        Assert.assertEquals(timeDiff.years, years);
    }

}
