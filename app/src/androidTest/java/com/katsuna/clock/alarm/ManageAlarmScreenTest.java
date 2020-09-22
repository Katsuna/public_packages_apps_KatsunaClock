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
package com.katsuna.clock.alarm;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.katsuna.clock.R;
import com.katsuna.clock.data.AlarmType;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.katsuna.clock.alarm.ManageAlarmActivity.EXTRA_ALARM_TYPE;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManageAlarmScreenTest {

    @Rule
    public ActivityTestRule<ManageAlarmActivity> mActivityRule =
            new ActivityTestRule<>(ManageAlarmActivity.class, false, false);

    @Test
    public void emptyDescriptionOfReminder_showsToastMessage() {
        // Launch activity to add a new alarm
        launchNewManageAlarmActivity();

        // Add invalid alarm type and description combination
        onView(withId(R.id.alarm_description)).perform(clearText());

        // Try to save the task
        onView(withId(R.id.next_step_fab)).perform(click());

        // Verify that the time control is not visible
        onView(withId(R.id.alarm_time_control)).check(matches(not(isDisplayed())));

        onView(withText(R.string.reminder_with_no_description))
                .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
                .check(matches(isDisplayed()));
    }

    private void launchNewManageAlarmActivity() {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                ManageAlarmActivity.class);
        intent.putExtra(EXTRA_ALARM_TYPE, AlarmType.REMINDER);
        mActivityRule.launchActivity(intent);
    }
}
