package com.katsuna.clock.data.source;

import android.content.Context;

public class DatabaseInjection {

    public static ClockDatabase getDatabase(Context context) {
        return ClockDatabase.getInstance(context);
    }
}
