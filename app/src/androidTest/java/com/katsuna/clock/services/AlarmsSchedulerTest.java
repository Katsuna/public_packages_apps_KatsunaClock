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
package com.katsuna.clock.services;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.services.utils.INextAlarmCalculator;
import com.katsuna.clock.services.utils.NextAlarmCalculator;
import com.katsuna.clock.util.Injection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

@RunWith(AndroidJUnit4.class)
public class AlarmsSchedulerTest {

    private AlarmsDataSource mAlarmsDatasource;

    private AlarmsScheduler mAlarmsScheduler;

    @Before
    public void init() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        mAlarmsDatasource = Injection.provideAlarmsDataSource(mContext);
        INextAlarmCalculator mNextAlarmCalculator = new NextAlarmCalculator();
        mAlarmsScheduler = new AlarmsScheduler(mContext, mAlarmsDatasource, mNextAlarmCalculator);
    }

    @Test
    public void newAlarmSaved_scheduledAndCancelled() {
        // setup
        LocalDateTime after5minutes = LocalDateTime.now().plusMinutes(5);

        final Alarm alarm = new Alarm(AlarmType.ALARM, after5minutes.getHour(),
                after5minutes.getMinute(), "", false, false, false, false, false, true, false,
                AlarmStatus.ACTIVE, "ringtone", false);
        mAlarmsDatasource.saveAlarm(alarm);

        // action set
        final boolean[] moveOn = new boolean[1];
        // action
        mAlarmsScheduler.schedule(new IAlarmsScheduler.CallBack() {
            @Override
            public void schedulingFinished() {
                moveOn[0] = true;
            }

            @Override
            public void schedulingFailed(Exception ex) {
                Assert.fail();
            }
        });

        while(!moveOn[0]) {
            waitFor(50);
        }

        // verify scheduled
        Assert.assertTrue(mAlarmsScheduler.isAlarmSet(alarm));

        // action
        mAlarmsScheduler.cancel(alarm);

        // verify not scheduled
        Assert.assertTrue(!mAlarmsScheduler.isAlarmSet(alarm));
    }

    @Test
    public void newAlarm_scheduledAndSnoozed() {
        // setup
        LocalDateTime after5minutes = LocalDateTime.now().plusMinutes(5);

        final Alarm alarm = new Alarm(AlarmType.ALARM, after5minutes.getHour(),
                after5minutes.getMinute(), "", false, false, false, false, false, true, false,
                AlarmStatus.ACTIVE, "ringtone", false);
        mAlarmsDatasource.saveAlarm(alarm);

        // action set
        final boolean[] moveOn = new boolean[1];

        mAlarmsScheduler.schedule(new IAlarmsScheduler.CallBack() {
            @Override
            public void schedulingFinished() {
                moveOn[0] = true;
            }

            @Override
            public void schedulingFailed(Exception ex) {
                Assert.fail();
            }
        });

        while(!moveOn[0]) {
            waitFor(50);
        }

        // verify
        Assert.assertTrue(mAlarmsScheduler.isAlarmSet(alarm));

        // action schedule
        mAlarmsScheduler.snooze(alarm, 60);

        // verify
        Assert.assertTrue(mAlarmsScheduler.isAlarmSet(alarm));

        // action cancel
        mAlarmsScheduler.cancel(alarm);

        // verify cancel
        Assert.assertTrue(!mAlarmsScheduler.isAlarmSet(alarm));
    }

    @Test
    public void twoAlarms_oneScheduledAndOneCancelled() {
        // setup
        LocalDateTime after5minutes = LocalDateTime.now().plusMinutes(5);

        final Alarm alarmOne = new Alarm(AlarmType.ALARM, after5minutes.getHour(),
                after5minutes.getMinute(), "", false, false, false, false, false, true, false,
                AlarmStatus.ACTIVE, "ringtone", false);
        mAlarmsDatasource.saveAlarm(alarmOne);

        final Alarm alarmTwo = new Alarm(AlarmType.ALARM, after5minutes.getHour(),
                after5minutes.getMinute(), "", false, false, false, false, false, true, false,
                AlarmStatus.ACTIVE, "ringtone", false);
        mAlarmsDatasource.saveAlarm(alarmTwo);

        // action set alarms
        final boolean[] moveOn = new boolean[1];

        mAlarmsScheduler.schedule(new IAlarmsScheduler.CallBack() {
            @Override
            public void schedulingFinished() {
                moveOn[0] = true;
            }

            @Override
            public void schedulingFailed(Exception ex) {
                Assert.fail();
            }
        });

        while(!moveOn[0]) {
            waitFor(50);
        }

        // verify
        Assert.assertTrue(mAlarmsScheduler.isAlarmSet(alarmOne));
        Assert.assertTrue(mAlarmsScheduler.isAlarmSet(alarmTwo));

        // action cancel first alarm
        mAlarmsScheduler.cancel(alarmOne);

        // verify
        Assert.assertTrue(!mAlarmsScheduler.isAlarmSet(alarmOne));
        Assert.assertTrue(mAlarmsScheduler.isAlarmSet(alarmTwo));

        // action cancel second to cleanup
        mAlarmsScheduler.cancel(alarmTwo);

        // verify cancel
        Assert.assertTrue(!mAlarmsScheduler.isAlarmSet(alarmTwo));

    }

    private void waitFor(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
