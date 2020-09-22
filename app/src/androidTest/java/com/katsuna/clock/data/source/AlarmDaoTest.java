/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.clock.data.source;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AlarmDaoTest {

    private static final Alarm ALARM = new Alarm(AlarmType.ALARM, "description");

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
        long alarmId = mDatabase.alarmsDao().insertAlarm(ALARM);
        ALARM.setAlarmId(alarmId);

        Alarm loaded = mDatabase.alarmsDao().getAlarmById(alarmId);

        Assert.assertEquals(ALARM, loaded);
    }

    @Test
    public void insertTaskReplacesOnConflict() {
        //Given that an alarm is inserted
        long alarmId = mDatabase.alarmsDao().insertAlarm(ALARM);
        ALARM.setAlarmId(alarmId);

        // When an alarm with the same id is inserted
        Alarm newAlarm = new Alarm(alarmId, AlarmType.ALARM, 0, 0, "description2", false,
                false, false, false, false, false, false, AlarmStatus.ACTIVE, "ringtone", false);
        mDatabase.alarmsDao().insertAlarm(newAlarm);

        // When getting the alarm by id from the database
        Alarm loaded = mDatabase.alarmsDao().getAlarmById(ALARM.getAlarmId());

        // The loaded data contains the expected values
        Assert.assertEquals(loaded, newAlarm);
    }

    @Test
    public void updateAlarmAndGetById() {
        // When inserting an alarm
        long alarmId = mDatabase.alarmsDao().insertAlarm(ALARM);
        ALARM.setAlarmId(alarmId);


        // When the alarm is updated
        Alarm updatedAlarm = new Alarm(ALARM.getAlarmId(), AlarmType.ALARM, 0, 0, "description2",
                false, false, false, false, false, false, false, AlarmStatus.ACTIVE, "ringtone",
                false);
        mDatabase.alarmsDao().updateAlarm(updatedAlarm);

        // When getting the alarm by id from the database
        Alarm loaded = mDatabase.alarmsDao().getAlarmById(ALARM.getAlarmId());

        // The loaded data contains the expected values
        Assert.assertEquals(loaded, updatedAlarm);
    }

    @Test
    public void deleteAlarmByIdAndGettingAlarms() {
        //Given an alarm inserted
        mDatabase.alarmsDao().insertAlarm(ALARM);

        //When deleting a task by id
        mDatabase.alarmsDao().deleteAlarmById(ALARM.getAlarmId());

        //When getting the alarms
        List<Alarm> alarms = mDatabase.alarmsDao().getAlarms();

        // The list is empty
        assertThat(alarms.size(), is(0));
    }

}
