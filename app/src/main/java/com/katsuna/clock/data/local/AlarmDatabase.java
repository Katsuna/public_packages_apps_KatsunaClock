package com.katsuna.clock.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.katsuna.clock.data.Alarm;

@Database(entities = {Alarm.class}, version = 1)
public abstract class AlarmDatabase extends RoomDatabase {

    private static final Object sLock = new Object();
    private static AlarmDatabase INSTANCE;

    public static AlarmDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AlarmDatabase.class, "alarms.db")
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract AlarmsDao alarmsDao();

}
