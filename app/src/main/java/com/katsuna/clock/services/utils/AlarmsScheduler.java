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
package com.katsuna.clock.services.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.katsuna.clock.LogUtils;
import com.katsuna.clock.alarm.ManageAlarmActivity;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.receivers.AlarmReceiver;
import com.katsuna.clock.util.DateUtils;

import org.threeten.bp.LocalDateTime;

import java.util.List;
import java.util.Objects;

import static android.app.PendingIntent.FLAG_NO_CREATE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class AlarmsScheduler implements IAlarmsScheduler {

    public static final String ALARM_ID = "alarm_id";

    private static final String TAG = AlarmsScheduler.class.getSimpleName();
    private final Context mContext;
    private final AlarmsDataSource mAlarmsDatasource;
    private final INextAlarmCalculator mNextAlarmCalculator;

    public AlarmsScheduler(@NonNull Context context, @NonNull AlarmsDataSource alarmsDataSource,
                           @NonNull INextAlarmCalculator nextAlarmCalculator) {
        mContext = context;
        mAlarmsDatasource = alarmsDataSource;
        mNextAlarmCalculator = nextAlarmCalculator;
    }

    @Override
    public void schedule(@NonNull final CallBack callBack) {

        mAlarmsDatasource.getAlarms(new AlarmsDataSource.LoadAlarmsCallback() {
            @Override
            public void onAlarmsLoaded(List<Alarm> alarms) {

                try {
                    for (Alarm alarm : alarms) {
                        setAlarm(alarm);
                    }
                    callBack.schedulingFinished();
                } catch (Exception ex) {
                    callBack.schedulingFailed(ex);
                }
            }

            @Override
            public void onDataNotAvailable() {
                callBack.schedulingFinished();
            }
        });
    }

    @Override
    public void reschedule(Alarm alarm) {
        cancel(alarm);

        if (alarm.getAlarmStatus() == AlarmStatus.ACTIVE) {
            setAlarm(alarm);
        }
    }

    @Override
    public void setAlarm(Alarm alarm) {
        LogUtils.d("%s setAlarm called for alarm: %s", TAG, alarm.toString());

        if (alarm.getAlarmStatus() != AlarmStatus.ACTIVE) return;

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime triggerDateTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(
                DateUtils.toEpochMillis(triggerDateTime),
                getPendindEditIntent(alarm));

        Objects.requireNonNull(am).setAlarmClock(alarmClockInfo, getPendingTriggerIntent(alarm,
                FLAG_UPDATE_CURRENT));

        LogUtils.i("%s Alarm %s scheduled at (%s)",TAG, alarm, triggerDateTime);
    }

    @Override
    public void snooze(Alarm alarm, long delay) {
        LogUtils.d("%s snooze: %s", TAG, alarm);

        if (alarm.getAlarmStatus() != AlarmStatus.ACTIVE) return;

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        LocalDateTime rescheduledTime = LocalDateTime.now().plusSeconds(delay);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(
                DateUtils.toEpochMillis(rescheduledTime),
                getPendindEditIntent(alarm));
        Objects.requireNonNull(am).setAlarmClock(alarmClockInfo, getPendingTriggerIntent(alarm,
                FLAG_UPDATE_CURRENT));

        LogUtils.i("%s Alarm %s snoozed and  scheduled at (%s)", TAG, alarm, rescheduledTime);
    }

    @Override
    public void cancel(Alarm alarm) {
        LogUtils.d("%s cancelAlarm: %s", TAG, alarm.toString());
        PendingIntent pi = getPendingTriggerIntent(alarm, FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmManager).cancel(pi);

        pi.cancel();
    }

    private PendingIntent getPendingTriggerIntent(Alarm alarm, int flag) {
        Intent i = new Intent(mContext, AlarmReceiver.class);
        i.putExtra(ALARM_ID, alarm.getAlarmId());
        return PendingIntent.getBroadcast(mContext, alarm.hashCode(), i, flag);
    }

    private PendingIntent getPendindEditIntent(Alarm alarm) {
        Intent i = new Intent(mContext, ManageAlarmActivity.class);
        i.putExtra(ManageAlarmActivity.EXTRA_ALARM_ID, alarm.getAlarmId());
        return PendingIntent.getActivity(mContext, alarm.hashCode(), i, 0);
    }

    public boolean isAlarmSet(Alarm alarm) {
        PendingIntent pi = getPendingTriggerIntent(alarm, FLAG_NO_CREATE);
        return pi != null;
    }
}
