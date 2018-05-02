package com.katsuna.clock.util;

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
}
