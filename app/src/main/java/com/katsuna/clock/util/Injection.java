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
