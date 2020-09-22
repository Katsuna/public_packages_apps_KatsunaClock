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
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.util.SingleExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Integration test for the {@link AlarmsLocalDataSource}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AlarmsLocalDataSourceTest {

    private AlarmsLocalDataSource mLocalDataSource;

    private ClockDatabase mDatabase;

    @Before
    public void setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                ClockDatabase.class)
                .build();
        AlarmsDao alarmsDao = mDatabase.alarmsDao();

        // Make sure that we're not keeping a reference to the wrong instance.
        AlarmsLocalDataSource.clearInstance();
        mLocalDataSource = AlarmsLocalDataSource.getInstance(new SingleExecutors(), alarmsDao);
    }

    @After
    public void cleanUp() {
        mDatabase.close();
        AlarmsLocalDataSource.clearInstance();
    }

    @Test
    public void testPreConditions() {
        assertNotNull(mLocalDataSource);
    }

    @Test
    public void saveAlarm_retrievesAlarm() {
        // Given a new alarm
        final Alarm newAlarm = new Alarm(AlarmType.ALARM, "desc");

        // When saved into the persistent repository
        mLocalDataSource.saveAlarm(newAlarm);

        // Then the alarm can be retrieved from the persistent repository
        mLocalDataSource.getAlarm(newAlarm.getAlarmId(), new AlarmsDataSource.GetAlarmCallback() {
            @Override
            public void onAlarmLoaded(Alarm alarm) {
                assertThat(alarm, is(newAlarm));
            }

            @Override
            public void onDataNotAvailable() {
                fail("Callback error");
            }
        });
    }

    @Test
    public void getAlarms_retrieveSavedAlarms() {
        // Given 2 new alarms in the persistent repository
        final Alarm newAlarm1 = new Alarm(AlarmType.ALARM, "desc1");
        mLocalDataSource.saveAlarm(newAlarm1);
        final Alarm newAlarm2 = new Alarm(AlarmType.ALARM, "desc2");
        mLocalDataSource.saveAlarm(newAlarm2);

        // Then the alarms can be retrieved from the persistent repository
        mLocalDataSource.getAlarms(new AlarmsDataSource.LoadAlarmsCallback() {
            @Override
            public void onAlarmsLoaded(List<Alarm> alarms) {
                assertNotNull(alarms);
                assertTrue(alarms.size() >= 2);

                boolean newAlarm1IdFound = false;
                boolean newAlarm2IdFound = false;
                for (Alarm alarm : alarms) {
                    if (alarm.getAlarmId()== newAlarm1.getAlarmId()) {
                        newAlarm1IdFound = true;
                    }
                    if (alarm.getAlarmId() == newAlarm2.getAlarmId()) {
                        newAlarm2IdFound = true;
                    }
                }
                assertTrue(newAlarm1IdFound);
                assertTrue(newAlarm2IdFound);
            }

            @Override
            public void onDataNotAvailable() {
                fail();
            }
        });
    }
}
