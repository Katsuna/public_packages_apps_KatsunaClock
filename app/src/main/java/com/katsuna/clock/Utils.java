package com.katsuna.clock;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.AnyRes;

public class Utils {

    /**
     * @return {@code true} if the device is {@link Build.VERSION_CODES#N} or later
     */
    public static boolean isNOrLater() {
        return Build.VERSION.SDK_INT >= 24;
    }

    /**
     * @param resourceId identifies an application resource
     * @return the Uri by which the application resource is accessed
     */
    public static Uri getResourceUri(Context context, @AnyRes int resourceId) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(context.getPackageName())
                .path(String.valueOf(resourceId))
                .build();
    }

    public static long now() {
        return SystemClock.elapsedRealtime();
    }
}