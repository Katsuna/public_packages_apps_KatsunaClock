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
package com.katsuna.clock.formatters;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AlarmFormatterTest {

    private Context mContext;

    private final String comma = ", ";
    private final String space = " ";

    @Before
    public void init() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void alarmDaysRepresentation() {
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 10, "", false, true, false,
                true, false, true, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        String expected = mContext.getString(R.string.tuesday) + comma +
                mContext.getString(R.string.thursday) + space +  mContext.getString(R.string.and) +
                space + mContext.getString(R.string.saturday);
        Assert.assertEquals(expected, formatter.getDays());
    }

    @Test
    public void alarmDaysAllDaysOn() {
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 10, "", true, true, true, true, true, true,
                true, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        String expected = mContext.getString(R.string.monday) + comma +
                mContext.getString(R.string.tuesday) + comma +
                mContext.getString(R.string.wednesday) + comma +
                mContext.getString(R.string.thursday) + comma +
                mContext.getString(R.string.friday) + comma +
                mContext.getString(R.string.saturday) + space +  mContext.getString(R.string.and) +
                        space + mContext.getString(R.string.sunday);
        Assert.assertEquals(expected, formatter.getDays());
    }

    @Test
    public void alarmDaysAllDaysOff() {
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 10, "", false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        String expected = "";
        Assert.assertEquals(expected, formatter.getDays());
    }

    @Test
    public void alarmTitle() {
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 10, "desc", false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        String expected = "14:10";
        Assert.assertTrue(formatter.getTitle().contains(expected));
    }

    @Test
    public void reminderTitle() {
        Alarm alarm = new Alarm(AlarmType.REMINDER, 14, 10, "desc", false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        if (formatter.getTitle() != null && alarm.getDescription() != null) {
            Assert.assertTrue(formatter.getTitle().contains(alarm.getDescription()));
        }
    }

    @Test
    public void alarmDescription() {
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 10, "", false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        String expected = mContext.getString(R.string.alarm);
        Assert.assertEquals(expected, formatter.getDescription());
    }

    @Test
    public void reminderDescription() {
        Alarm alarm = new Alarm(AlarmType.REMINDER, 14, 10, "", false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        String expected = "14:10";
        Assert.assertEquals(expected, formatter.getDescription());
    }

    @Test
    public void alarmTypeIconResId() {
        Alarm alarm = new Alarm(AlarmType.ALARM, 14, 10, "", false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        int expected = R.drawable.ic_access_time_24dp;
        Assert.assertEquals(expected, formatter.getAlarmTypeIconResId());
    }

    @Test
    public void reminderTypeIconResId() {
        Alarm alarm = new Alarm(AlarmType.REMINDER, 14, 10, "", false, false, false, false, false,
                false, false, AlarmStatus.ACTIVE, "ringtone", false);

        AlarmFormatter formatter = new AlarmFormatter(mContext, alarm);

        int expected = R.drawable.ic_notifications_black54_24dp;
        Assert.assertEquals(expected, formatter.getAlarmTypeIconResId());
    }

}
