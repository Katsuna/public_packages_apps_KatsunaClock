package com.katsuna.clock.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.data.source.AlarmsLocalDataSource;
import com.katsuna.clock.data.source.ClockDatabase;
import com.katsuna.clock.services.utils.AlarmScheduler;
import com.katsuna.clock.services.utils.IAlarmScheduler;
import com.katsuna.clock.services.utils.NextAlarmCalculator;
import com.katsuna.clock.validators.AlarmValidator;
import com.katsuna.clock.validators.IAlarmValidator;

/**
 * Enables injection of implementations for various objects.
 */
public class Injection {

    public static AlarmsDataSource provideAlarmsDataSource(@NonNull Context context) {
        ClockDatabase database = ClockDatabase.getInstance(context);
        return AlarmsLocalDataSource.getInstance(new AppExecutors(), database.alarmsDao());
    }

    public static IAlarmValidator provideAlarmValidator() {
        return new AlarmValidator();
    }

    public static IAlarmScheduler provideAlarmScheduler(@NonNull Context context) {
        return new AlarmScheduler(context, provideAlarmsDataSource(context),
                new NextAlarmCalculator());
    }
}
