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
