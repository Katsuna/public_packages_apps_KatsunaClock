package com.katsuna.clock.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.katsuna.clock.LogUtils;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.Injection;

public class AlarmService extends Service {

    private static final String TAG = AlarmService.class.getSimpleName();
    private IAlarmsScheduler mAlarmsScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("%s %s", TAG, "onCreate");

        mAlarmsScheduler = Injection.provideAlarmScheduler(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        LogUtils.d("%s %s", TAG, "onStartCommand");

        // Query the database and show alarm if it applies

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.


        mAlarmsScheduler.schedule(new IAlarmsScheduler.CallBack() {
            @Override
            public void schedulingFinished() {
                stopSelf();
            }

            @Override
            public void schedulingFailed(Exception ex) {
                LogUtils.e("%s exception while scheduling alarms:  %s", ex.toString());
                stopSelf();
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        LogUtils.d("%s %s", TAG, "onDestroy");
        super.onDestroy();
    }


}
