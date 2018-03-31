package com.katsuna.clock.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.data.source.AlarmsLocalDataSource;
import com.katsuna.clock.data.source.ClockDatabase;

/**
 * Enables injection of mock implementations for
 * {@link AlarmsDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static AlarmsDataSource provideAlarmsDataSource(@NonNull Context context) {
        ClockDatabase database = ClockDatabase.getInstance(context);
        return AlarmsLocalDataSource.getInstance(new AppExecutors(), database.alarmsDao());
    }
}
