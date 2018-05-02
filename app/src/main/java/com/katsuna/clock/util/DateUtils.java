package com.katsuna.clock.util;

import android.support.annotation.NonNull;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

public class DateUtils {

    public static long toEpochMillis(@NonNull LocalDateTime dateTime) {
        ZoneId systemZone = ZoneId.systemDefault();
        ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(dateTime);
        return dateTime.toInstant(currentOffsetForMyZone).toEpochMilli();
    }

}
