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
package com.katsuna.clock;

import android.content.Context;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.services.utils.INextAlarmCalculator;
import com.katsuna.clock.services.utils.NextAlarmCalculator;
import com.katsuna.clock.util.Injection;
import com.katsuna.clock.util.Keyguard;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AlarmActivationTest {

    private static final String COM_KATSUNA_CLOCK = "com.katsuna.clock";

    private static final int LAUNCH_TIMEOUT = 2000;
    private static final int ONE_MINUTE = 60000;

    private UiDevice mDevice;

    private AlarmsDataSource mAlarmsDatasource;

    private AlarmsScheduler mAlarmsScheduler;
    private Alarm mAlarm;


    @Before
    public void start() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // init scheduler
        Context mContext = InstrumentationRegistry.getTargetContext();
        mAlarmsDatasource = Injection.provideAlarmsDataSource(mContext);
        INextAlarmCalculator mNextAlarmCalculator = new NextAlarmCalculator();
        mAlarmsScheduler = new AlarmsScheduler(mContext, mAlarmsDatasource, mNextAlarmCalculator);
    }

    @After
    public void stop() {
        // clean up
        if (mAlarm != null) {
            mAlarmsDatasource.deleteAlarm(mAlarm.getAlarmId());
        }
        mAlarm = null;
    }

    @Test
    public void checkPreconditions() {
        assertThat(mDevice, notNullValue());
    }

    @Test
    public void alarmScheduling_opensAlarmsActivity() {
        scheduleAlarm();

        boolean result = mDevice.wait(Until.hasObject(By.pkg(COM_KATSUNA_CLOCK).depth(0)),
                ONE_MINUTE);

        Assert.assertTrue(result);

        UiObject2 dismissButton = mDevice.findObject(By.res(COM_KATSUNA_CLOCK, "dismiss_button"));
        dismissButton.click();

        result = mDevice.wait(Until.gone(By.pkg(COM_KATSUNA_CLOCK)), LAUNCH_TIMEOUT);
        Assert.assertTrue(result);
    }

    @Test
    public void alarmSchedulingOnLockedScreen_opensAlarmsActivity() {
        scheduleAlarm();

        try {
            mDevice.sleep();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        boolean result = mDevice.wait(Until.hasObject(By.pkg(COM_KATSUNA_CLOCK).depth(0)),
                ONE_MINUTE);

        Assert.assertTrue(result);

        UiObject2 dismissButton = mDevice.findObject(By.res(COM_KATSUNA_CLOCK, "dismiss_button"));
        dismissButton.click();

        result = mDevice.wait(Until.gone(By.pkg(COM_KATSUNA_CLOCK)), LAUNCH_TIMEOUT);
        Assert.assertTrue(result);

        try {
            mDevice.wakeUp();
            Keyguard.disableKeyguard(InstrumentationRegistry.getTargetContext());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        //assertThat(mAlarmsScheduler.isAlarmSet(mAlarm), is(false));
    }

    @Test
    public void alarmSnooze_leavesAlarmSet() {
        scheduleAlarm();

        boolean result = mDevice.wait(Until.hasObject(By.pkg(COM_KATSUNA_CLOCK).depth(0)),
                ONE_MINUTE);

        Assert.assertTrue(result);

        UiObject2 dismissButton = mDevice.findObject(By.res(COM_KATSUNA_CLOCK, "snooze_button"));
        dismissButton.click();

        result = mDevice.wait(Until.gone(By.pkg(COM_KATSUNA_CLOCK)), LAUNCH_TIMEOUT);
        Assert.assertTrue(result);

        Assert.assertTrue(mAlarmsScheduler.isAlarmSet(mAlarm));
    }


    private void scheduleAlarm() {
        // setup
        LocalDateTime after1minute = LocalDateTime.now().plusMinutes(1);

        mAlarm = new Alarm(AlarmType.ALARM, after1minute.getHour(),
                after1minute.getMinute(), "", false, false, false, false, false, false, false,
                AlarmStatus.ACTIVE, "ringtone", false);
        mAlarmsDatasource.saveAlarm(mAlarm);

        // action
        mAlarmsScheduler.schedule(new IAlarmsScheduler.CallBack() {
            @Override
            public void schedulingFinished() {
                // verify scheduled
                Assert.assertTrue(mAlarmsScheduler.isAlarmSet(mAlarm));
            }

            @Override
            public void schedulingFailed(Exception ex) {
                Assert.fail();
            }
        });
    }
}
