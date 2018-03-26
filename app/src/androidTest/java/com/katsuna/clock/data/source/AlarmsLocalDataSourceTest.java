/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.katsuna.clock.data.source;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.katsuna.clock.data.Alarm;
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
        AlarmsDao tasksDao = mDatabase.alarmsDao();

        // Make sure that we're not keeping a reference to the wrong instance.
        AlarmsLocalDataSource.clearInstance();
        mLocalDataSource = AlarmsLocalDataSource.getInstance(new SingleExecutors(), tasksDao);
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
    public void saveAlarm_retrievesTask() {
        // Given a new alarm
        final Alarm newAlarm = new Alarm(1, "desc");

        // When saved into the persistent repository
        mLocalDataSource.saveAlarm(newAlarm);

        // Then the alarm can be retrieved from the persistent repository
        mLocalDataSource.getAlarm(newAlarm.getId(), new AlarmsDataSource.GetAlarmCallback() {
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
    public void getTasks_retrieveSavedTasks() {
        // Given 2 new alarms in the persistent repository
        final Alarm newAlarm1 = new Alarm(1, "desc1");
        mLocalDataSource.saveAlarm(newAlarm1);
        final Alarm newAlarm2 = new Alarm(2, "desc2");
        mLocalDataSource.saveAlarm(newAlarm2);

        // Then the tasks can be retrieved from the persistent repository
        mLocalDataSource.getAlarms(new AlarmsDataSource.LoadAlarmsCallback() {
            @Override
            public void onAlarmsLoaded(List<Alarm> alarms) {
                assertNotNull(alarms);
                assertTrue(alarms.size() >= 2);

                boolean newAlarm1IdFound = false;
                boolean newAlarm2IdFound = false;
                for (Alarm alarm : alarms) {
                    if (alarm.getId().equals(newAlarm1.getId())) {
                        newAlarm1IdFound = true;
                    }
                    if (alarm.getId().equals(newAlarm2.getId())) {
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
