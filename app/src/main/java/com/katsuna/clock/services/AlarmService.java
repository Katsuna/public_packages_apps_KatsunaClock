package com.katsuna.clock.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.katsuna.clock.services.utils.IAlarmScheduler;
import com.katsuna.clock.util.Injection;

public class AlarmService extends Service {

    private static final String TAG = "KastunaAlarmService";
    private IAlarmScheduler mAlarmScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mAlarmScheduler = Injection.provideAlarmScheduler(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "onStartCommand");

        // Query the database and show alarm if it applies

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.


        mAlarmScheduler.schedule(new IAlarmScheduler.CallBack() {
            @Override
            public void schedulingFinished() {
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
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }


}
