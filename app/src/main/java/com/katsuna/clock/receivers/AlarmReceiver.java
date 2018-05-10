package com.katsuna.clock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.katsuna.clock.AlarmActivationActivity;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmState;
import com.katsuna.clock.data.AlarmStateManager;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.Injection;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    // 1 minute auto snooze if we have alarms launching at the same time
    private final static int SNOOZE_DELAY_OVERLAPPING = 60;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // Put here YOUR code.
        final long alarmId = Objects.requireNonNull(intent.getExtras()).getLong(AlarmsScheduler.ALARM_ID);
        Log.e(TAG, "onReceive: " + alarmId);

        Toast.makeText(context, "Alarm with id: " + alarmId, Toast.LENGTH_LONG).show();

        AlarmsDataSource alarmsDataSource = Injection.provideAlarmsDataSource(context);
        alarmsDataSource.getAlarm(alarmId, new AlarmsDataSource.GetAlarmCallback() {
            @Override
            public void onAlarmLoaded(Alarm alarm) {

                IAlarmsScheduler alarmsScheduler = Injection.provideAlarmScheduler(context);

                AlarmStateManager alarmStateManager = AlarmStateManager.getInstance();

                AlarmState alarmState = alarmStateManager.getAlarmState(alarm);
                Log.e(TAG, "onReceive alarmState: " + alarmState);
                if (alarmState == null) {
                    if (alarmStateManager.alarmActive()) {
                        Log.e(TAG, "onReceive another alarm is active.");
                        alarmsScheduler.snooze(alarm, SNOOZE_DELAY_OVERLAPPING);
                    } else {
                        Log.e(TAG, "onReceive alarm activated.");
                        AlarmStateManager.getInstance().setAlarmState(alarm, AlarmState.ACTIVATED);
                        Intent i = new Intent(context, AlarmActivationActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(AlarmsScheduler.ALARM_ID, alarmId);
                        context.startActivity(i);
                     }
                }
            }

            @Override
            public void onDataNotAvailable() {
                Log.e(TAG, "alarm not found with alarmId: " + alarmId);
            }
        });
    }

}
