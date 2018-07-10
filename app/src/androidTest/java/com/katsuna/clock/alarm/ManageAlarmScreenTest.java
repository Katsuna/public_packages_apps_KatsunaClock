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
