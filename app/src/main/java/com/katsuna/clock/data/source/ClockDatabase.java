package com.katsuna.clock.data.source;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.katsuna.clock.data.Alarm;

@Database(entities = {Alarm.class}, version = 3)
public abstract class ClockDatabase extends RoomDatabase {

    private static final Object sLock = new Object();
    private static ClockDatabase INSTANCE;

    public static ClockDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ClockDatabase.class, "alarms.db")
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract AlarmsDao alarmsDao();

}
