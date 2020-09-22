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
package com.katsuna.clock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.katsuna.clock.LogUtils;
import com.katsuna.clock.Utils;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.Injection;

public class AlarmInitReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmInitReceiver.class.getSimpleName();

    /**
     * When running on N devices, we're interested in the boot completed event that is sent while
     * the user is still locked, so that we can schedule alarms.
     */
    private static final String ACTION_BOOT_COMPLETED = Utils.isNOrLater()
            ? Intent.ACTION_LOCKED_BOOT_COMPLETED : Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        LogUtils.i("%s AlarmInitReceiver action: %s", TAG, action);

        if (isActionSupported(action)) {

            IAlarmsScheduler alarmsScheduler = Injection.provideAlarmScheduler(context);
            alarmsScheduler.schedule(new IAlarmsScheduler.CallBack() {
                @Override
                public void schedulingFinished() {
                    LogUtils.i("%s alarms scheduling completed", TAG);
                }

                @Override
                public void schedulingFailed(Exception ex) {
                    LogUtils.e("%s exception while scheduling alarms:  %s", TAG, ex.toString());
                }
            });

        }
    }

    private boolean isActionSupported(String action) {
        return ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_LOCALE_CHANGED.equals(action)
                || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)
                || Intent.ACTION_TIMEZONE_CHANGED.equals(action);
    }
}
