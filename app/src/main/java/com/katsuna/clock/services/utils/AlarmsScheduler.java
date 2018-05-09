package com.katsuna.clock.services.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.katsuna.clock.alarm.ManageAlarmActivity;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.receivers.AlarmReceiver;
import com.katsuna.clock.util.DateUtils;

import org.threeten.bp.LocalDateTime;

import java.util.List;
import java.util.Objects;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class AlarmsScheduler implements IAlarmsScheduler {

    private static final String TAG = "AlarmsScheduler";
    public static final String ALARM_ID = "alarm_id";

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
        cancel(alarm);

        if (alarm.getAlarmStatus() == AlarmStatus.ACTIVE) {
            setAlarm(alarm);
        }
    }

    private void setAlarm(Alarm alarm) {
        if (alarm.getAlarmStatus() != AlarmStatus.ACTIVE) return;

        Log.e(TAG, "setAlarm: " + alarm.toString());

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime triggerDateTime = mNextAlarmCalculator.getTriggerDateTime(now, alarm);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(
                DateUtils.toEpochMillis(triggerDateTime),
                getPendindEditIntent(alarm));
        Objects.requireNonNull(am).setAlarmClock(alarmClockInfo, getPendingTriggerIntent(alarm));

        Log.e(TAG, String.format("Alarm %s scheduled at (%s)", alarm, triggerDateTime));
    }

    @Override
    public void snooze(Alarm alarm, long delay) {
        if (alarm.getAlarmStatus() != AlarmStatus.ACTIVE) return;

        Log.e(TAG, "snooze: " + alarm.toString());

        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        LocalDateTime rescheduledTime = LocalDateTime.now().plusSeconds(delay);

        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(
                DateUtils.toEpochMillis(rescheduledTime),
                getPendindEditIntent(alarm));
        Objects.requireNonNull(am).setAlarmClock(alarmClockInfo, getPendingTriggerIntent(alarm));

        Log.e(TAG, String.format("Alarm %s snoozed and  scheduled at (%s)", alarm, rescheduledTime));
    }

    @Override
    public void cancel(Alarm alarm) {
        Log.e(TAG, "cancelAlarm: " + alarm.toString());
        PendingIntent pi = getPendingTriggerIntent(alarm);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Objects.requireNonNull(alarmManager).cancel(pi);
    }

    private PendingIntent getPendingTriggerIntent(Alarm alarm) {
        Intent i = new Intent(mContext, AlarmReceiver.class);
        i.putExtra(ALARM_ID, alarm.getAlarmId());
        return PendingIntent.getBroadcast(mContext, alarm.hashCode(), i, FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getPendindEditIntent(Alarm alarm) {
        Intent i = new Intent(mContext, ManageAlarmActivity.class);
        i.putExtra(ManageAlarmActivity.EXTRA_ALARM_ID, alarm.getAlarmId());
        return PendingIntent.getActivity(mContext, alarm.hashCode(), i, 0);
    }

}
