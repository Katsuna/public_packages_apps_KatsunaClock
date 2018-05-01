package com.katsuna.clock.services.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.receivers.AlarmReceiver;

import java.util.List;


public class AlarmScheduler implements IAlarmScheduler {

    private static final String TAG = "AlarmScheduler";
    public static final String ALARM_ID = "alarm_id";

    private final Context mContext;
    private final AlarmsDataSource mAlarmsDatasource;
    private final INextAlarmCalculator mNextAlarmCalculator;

    public AlarmScheduler(@NonNull Context context, @NonNull AlarmsDataSource alarmsDataSource,
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

                for (Alarm alarm : alarms) {
                    setAlarm(alarm);
                }

                callBack.schedulingFinished();
            }

            @Override
            public void onDataNotAvailable() {
                // TODO
                callBack.schedulingFinished();
            }
        });
    }

    @Override
    public void reschedule(Alarm alarm) {
        cancelAlarm(alarm);

        if (alarm.getAlarmStatus() == AlarmStatus.ACTIVE) {
            setAlarm(alarm);
        }
    }

    private void setAlarm(Alarm alarm) {
        if (alarm.getAlarmStatus() != AlarmStatus.ACTIVE) return;

        Log.e(TAG, "setAlarm: " + alarm.toString());
        PendingIntent pi = getPendingIntent(alarm);

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, mNextAlarmCalculator.getTriggerTime(alarm), pi);
    }

    private void cancelAlarm(Alarm alarm) {
        Log.e(TAG, "cancelAlarm: " + alarm.toString());
        PendingIntent pi = getPendingIntent(alarm);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    private PendingIntent getPendingIntent(Alarm alarm) {
        Intent i = new Intent(mContext, AlarmReceiver.class);
        i.putExtra(ALARM_ID, alarm.getId());
        return PendingIntent.getBroadcast(mContext, 0, i, 0);

    }

}
