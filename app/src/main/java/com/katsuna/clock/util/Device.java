package com.katsuna.clock.util;

import android.os.Build;

public class Device {

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#N} or later
     */
    public static boolean isNOrLater() {
        return Build.VERSION.SDK_INT >= 24;
    }

}
