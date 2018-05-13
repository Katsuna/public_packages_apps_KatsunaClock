package com.katsuna.clock.data.source;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseInjection {

    public static ClockDatabase getDatabase(Context context) {
        return Room.inMemoryDatabaseBuilder(context, ClockDatabase.class).build();
    }
}
