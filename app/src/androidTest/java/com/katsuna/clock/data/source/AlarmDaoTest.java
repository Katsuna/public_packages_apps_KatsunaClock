package com.katsuna.clock.data.source;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.katsuna.clock.data.Alarm;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AlarmDaoTest {

    private static final Alarm ALARM = new Alarm(1, "description", true, true, true, true, true,
            false, false);

    private ClockDatabase mDatabase;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                ClockDatabase.class).build();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void insertTaskAndGetById() {
        mDatabase.alarmsDao().insertAlarm(ALARM);

        Alarm loaded = mDatabase.alarmsDao().getAlarmById(ALARM.getId());

        assertEquals(ALARM, loaded);
    }

    @Test
    public void insertTaskReplacesOnConflict() {
        //Given that an alarm is inserted
        mDatabase.alarmsDao().insertAlarm(ALARM);

        // When an alarm with the same id is inserted
        Alarm newAlarm = new Alarm(ALARM.getId(), 2, "description2", false, false, false, false,
                false, false, false);
        mDatabase.alarmsDao().insertAlarm(newAlarm);

        // When getting the alarm by id from the database
        Alarm loaded = mDatabase.alarmsDao().getAlarmById(ALARM.getId());

        // The loaded data contains the expected values
        assertEquals(loaded, newAlarm);
    }

    @Test
    public void updateAlarmAndGetById() {
        // When inserting an alarm
        mDatabase.alarmsDao().insertAlarm(ALARM);

        // When the alarm is updated
        Alarm updatedAlarm = new Alarm(ALARM.getId(), 2, "description2", false, false, false, false,
                false, false, false);
        mDatabase.alarmsDao().updateAlarm(updatedAlarm);

        // When getting the alarm by id from the database
        Alarm loaded = mDatabase.alarmsDao().getAlarmById(ALARM.getId());

        // The loaded data contains the expected values
        assertEquals(loaded, updatedAlarm);
    }

    @Test
    public void deleteAlarmByIdAndGettingAlarms() {
        //Given an alarm inserted
        mDatabase.alarmsDao().insertAlarm(ALARM);

        //When deleting a task by id
        mDatabase.alarmsDao().deleteAlarmById(ALARM.getId());

        //When getting the alarms
        List<Alarm> alarms = mDatabase.alarmsDao().getAlarms();

        // The list is empty
        assertThat(alarms.size(), is(0));
    }

}
