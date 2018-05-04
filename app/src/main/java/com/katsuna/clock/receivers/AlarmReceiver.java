package com.katsuna.clock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.katsuna.clock.AlarmActivationActivity;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.Injection;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    private AlarmsDataSource mAlarmsDataSource;
    private IAlarmsScheduler mAlarmsScheduler;


    // https://stackoverflow.com/a/8801990

    @Override
    public void onReceive(final Context context, Intent intent) {
/*        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();*/

        // Put here YOUR code.
        String alarmId = Objects.requireNonNull(intent.getExtras())
                .getString(AlarmsScheduler.ALARM_ID);
        Log.e(TAG, "onReceive: " + alarmId);

        Toast.makeText(context, "Alarm with id: " + alarmId, Toast.LENGTH_LONG).show();

        mAlarmsDataSource = Injection.provideAlarmsDataSource(context);
        mAlarmsDataSource.getAlarm(alarmId, new AlarmsDataSource.GetAlarmCallback() {
            @Override
            public void onAlarmLoaded(Alarm alarm) {
                if (alarm.isRecurring()) {
                    // reshedule
                    Log.e(TAG, "onReceive rescheduling");
                    mAlarmsScheduler = Injection.provideAlarmScheduler(context);
                    mAlarmsScheduler.reschedule(alarm);
                } else {
                    // deactivate
                    Log.e(TAG, "onReceive deactivating");
                    alarm.setAlarmStatus(AlarmStatus.INACTIVE);
                    mAlarmsDataSource.saveAlarm(alarm);
                }

                Intent i = new Intent(context, AlarmActivationActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }

            @Override
            public void onDataNotAvailable() {
                // TODO
            }
        });


        /*wl.release();*/
    }

}
