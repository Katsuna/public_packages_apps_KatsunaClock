package com.katsuna.clock;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class ClockApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AndroidThreeTen.init(this);
    }
}
