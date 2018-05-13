package com.katsuna.clock.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.data.source.AlarmsLocalDataSource;
import com.katsuna.clock.data.source.ClockDatabase;
import com.katsuna.clock.data.source.DatabaseInjection;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.services.utils.NextAlarmCalculator;
import com.katsuna.clock.validators.AlarmValidator;
import com.katsuna.clock.validators.IAlarmValidator;

/**
 * Enables injection of implementations for various objects.
 */
public class Injection {

    public static AlarmsDataSource provideAlarmsDataSource(@NonNull Context context) {
        ClockDatabase database = DatabaseInjection.getDatabase(context);
        return AlarmsLocalDataSource.getInstance(new AppExecutors(), database.alarmsDao());
    }

    public static IAlarmValidator provideAlarmValidator() {
        return new AlarmValidator();
    }

    public static IAlarmsScheduler provideAlarmScheduler(@NonNull Context context) {
        return new AlarmsScheduler(context, provideAlarmsDataSource(context),
                new NextAlarmCalculator());
    }
}
