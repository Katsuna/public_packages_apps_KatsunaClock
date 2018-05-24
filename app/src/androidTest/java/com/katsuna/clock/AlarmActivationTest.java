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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDateTime;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
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
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            mDevice.wakeUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // init scheduler
        Context mContext = InstrumentationRegistry.getTargetContext();
        mAlarmsDatasource = Injection.provideAlarmsDataSource(mContext);
        INextAlarmCalculator mNextAlarmCalculator = new NextAlarmCalculator();
        mAlarmsScheduler = new AlarmsScheduler(mContext, mAlarmsDatasource, mNextAlarmCalculator);
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

        assertTrue(result);

        UiObject2 dismissButton = mDevice.findObject(By.res(COM_KATSUNA_CLOCK, "dismiss_button"));
        dismissButton.click();

        result = mDevice.wait(Until.gone(By.pkg(COM_KATSUNA_CLOCK)), LAUNCH_TIMEOUT);
        assertTrue(result);
    }

    @Test
    public void alarmSchedulingOnLockedScreen_opensAlarmsActivity() {
        scheduleAlarm();

        // Start from the home screen
        mDevice.pressHome();

        try {
            mDevice.sleep();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        boolean result = mDevice.wait(Until.hasObject(By.pkg(COM_KATSUNA_CLOCK).depth(0)),
                ONE_MINUTE);

        assertTrue(result);

        UiObject2 dismissButton = mDevice.findObject(By.res(COM_KATSUNA_CLOCK, "dismiss_button"));
        dismissButton.click();

        result = mDevice.wait(Until.gone(By.pkg(COM_KATSUNA_CLOCK)), LAUNCH_TIMEOUT);
        assertTrue(result);
    }

    private void scheduleAlarm() {
        // setup
        LocalDateTime after1minute = LocalDateTime.now().plusMinutes(1);

        mAlarm = new Alarm(AlarmType.ALARM, after1minute.getHour(),
                after1minute.getMinute(), "", false, false, false, false, false, false, false,
                AlarmStatus.ACTIVE);
        mAlarmsDatasource.saveAlarm(mAlarm);

        // action
        mAlarmsScheduler.schedule(new IAlarmsScheduler.CallBack() {
            @Override
            public void schedulingFinished() {
                // verify scheduled
                assertTrue(mAlarmsScheduler.isAlarmSet(mAlarm));
            }

            @Override
            public void schedulingFailed(Exception ex) {
                fail();
            }
        });
    }
}
