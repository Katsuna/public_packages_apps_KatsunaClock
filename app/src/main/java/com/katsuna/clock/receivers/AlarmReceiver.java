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
import android.os.PowerManager;

import com.katsuna.clock.AlarmActivationActivity;
import com.katsuna.clock.LogUtils;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmState;
import com.katsuna.clock.data.AlarmStateManager;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.AlarmAlertWakeLock;
import com.katsuna.clock.util.AsyncHandler;
import com.katsuna.clock.util.Injection;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    // 1 minute auto snooze if we have alarms launching at the same time
    private final static int SNOOZE_DELAY_OVERLAPPING = 60;

    private void handleIntent(final Context context, Intent intent) {

        final long alarmId = Objects.requireNonNull(intent.getExtras())
                .getLong(AlarmsScheduler.ALARM_ID);
        LogUtils.i("%s onReceive, alarmId: %s", TAG, alarmId);

        AlarmsDataSource alarmsDataSource = Injection.provideAlarmsDataSource(context);
        Alarm alarm = alarmsDataSource.getAlarm(alarmId);

        if (alarm == null) {
            LogUtils.i("%s alarm not found with alarmId: %s", TAG, alarmId);
        } else {
            IAlarmsScheduler alarmsScheduler = Injection.provideAlarmScheduler(context);
            AlarmStateManager alarmStateManager = AlarmStateManager.getInstance();
            AlarmState alarmState = alarmStateManager.getAlarmState(alarm);
            if (alarmState == null) {
                if (alarmStateManager.alarmActive()) {
                    LogUtils.i("%s onReceive another alarm is active.", TAG);
                    alarmsScheduler.snooze(alarm, SNOOZE_DELAY_OVERLAPPING);
                } else {
                    LogUtils.i("%s onReceive alarm activated.", TAG);
                    AlarmStateManager.getInstance().setAlarmState(alarm, AlarmState.ACTIVATED);
                    Intent i = new Intent(context, AlarmActivationActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("alarm", alarm);
                    context.startActivity(i);
                    LogUtils.d("%s onReceive, startActivity requested.", TAG);
                }
            }
        }

    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final PendingResult result = goAsync();
        AsyncHandler.post(new Runnable() {
            @Override
            public void run() {
                final PowerManager.WakeLock wl = AlarmAlertWakeLock.createPartialWakeLock(context);
                // 1 minute should be enough
                long wakeLockTimeout = 60 * 1000;
                wl.acquire(wakeLockTimeout);
                LogUtils.d("%s onReceive, before handleIntent", TAG);
                handleIntent(context, intent);
                LogUtils.d("%s onReceive, after handleIntent", TAG);
                result.finish();
            }
        });
    }
}
