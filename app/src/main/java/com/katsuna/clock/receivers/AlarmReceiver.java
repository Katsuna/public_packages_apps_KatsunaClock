package com.katsuna.clock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmScheduler;
import com.katsuna.clock.services.utils.IAlarmScheduler;
import com.katsuna.clock.util.Injection;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    private AlarmsDataSource mAlarmsDataSource;
    private IAlarmScheduler mAlarmScheduler;


    // https://stackoverflow.com/a/8801990

    @Override
    public void onReceive(final Context context, Intent intent) {
/*        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();*/

        // Put here YOUR code.


        String alarmId = intent.getExtras().getString(AlarmScheduler.ALARM_ID);
        Log.e(TAG, "onReceive: " + alarmId);

        Toast.makeText(context, "Alarm with id: " + alarmId, Toast.LENGTH_LONG).show();

        mAlarmsDataSource = Injection.provideAlarmsDataSource(context);
        mAlarmsDataSource.getAlarm(alarmId, new AlarmsDataSource.GetAlarmCallback() {
            @Override
            public void onAlarmLoaded(Alarm alarm) {
                if (alarm.isRecurring()) {
                    // reshedule
                    Log.e(TAG, "onReceive rescheduling");
                    mAlarmScheduler = Injection.provideAlarmScheduler(context);
                    mAlarmScheduler.reschedule(alarm);
                } else {
                    // deactivate
                    Log.e(TAG, "onReceive deactivating");
                    alarm.setAlarmStatus(AlarmStatus.INACTIVE);
                    mAlarmsDataSource.saveAlarm(alarm);
                }
            }

            @Override
            public void onDataNotAvailable() {
                // TODO
            }
        });


        /*wl.release();*/
    }

}
