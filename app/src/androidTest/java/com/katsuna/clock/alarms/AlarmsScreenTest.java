package com.katsuna.clock.alarms;

import android.graphics.drawable.ColorDrawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.internal.util.Checks;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.katsuna.clock.R;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.utils.ColorCalcV2;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AlarmsScreenTest {

    private final static String DESCRIPTION = "DESCR";

    @Rule
    public ActivityTestRule<AlarmsActivity> mActivityRule =
            new ActivityTestRule<>(AlarmsActivity.class);

    @Test
    public void clickAddAlarmFab_opensAddAlarmUi() {
        // Click on the add alarm button
        onView(withId(R.id.create_alarm_fab)).perform(click());

        // Check if the add alarm screen is displayed
        onView(withId(R.id.reminder_type_radio_button)).check(matches(isDisplayed()));
    }

    @Test
    public void clickAddAlarmButton_opensAddAlarmUi() {
        // Click on the add alarm button
        onView(withId(R.id.create_alarm_button)).perform(click());

        // Check if the add alarm screen is displayed
        onView(withId(R.id.reminder_type_radio_button)).check(matches(isDisplayed()));
    }

    @Test
    public void editAlarm() {
        // First add an alarm
        createAlarm(AlarmType.REMINDER, DESCRIPTION);

        // Click on the alarm on the list
        onView(withText(DESCRIPTION)).perform(click());

        // Click on the edit alarm button
        onView(withId(R.id.button_edit)).perform(click());

        String editAlarmDescription = "New Description";

        // Edit alarm description
        onView(withId(R.id.alarm_description)).perform(replaceText(editAlarmDescription),
                closeSoftKeyboard()); // Type new alarm description and close the keyboard

        // Save the alarm through wizard steps
        onView(withId(R.id.next_step_fab)).perform(click());
        onView(withId(R.id.next_step_fab)).perform(click());
        onView(withId(R.id.next_step_fab)).perform(click());

        // Verify alarm is displayed on screen in the alarm list.
        onView(withItemText(editAlarmDescription)).check(matches(isDisplayed()));

        // Verify previous alarm description is not displayed
        onView(withItemText(DESCRIPTION)).check(doesNotExist());

        deleteAlarmWithDescription(editAlarmDescription);
    }

    @Test
    public void turnOffAlarm() {
        // First add an alarm
        createAlarm(AlarmType.REMINDER, DESCRIPTION);

        // Click on the alarm on the list
        onView(withText(DESCRIPTION)).perform(click());

        // Click on the turn off alarm button
        onView(allOf(withId(R.id.button_turn_off), isDisplayed()))
                .perform(click());

        //onView(withText(DESCRIPTION)).
        UserProfile userProfile = mActivityRule.getActivity().getProfile();
        int expectedColor = ColorCalcV2.getColor(InstrumentationRegistry.getTargetContext(),
                ColorProfileKeyV2.SECONDARY_GREY_2, userProfile.colorProfile);
        onView(allOf(withId(R.id.alarm_container_card_inner), hasDescendant(withText(DESCRIPTION))))
                .check(matches(withBackgroundColor(expectedColor)));

        // Click on the alarm on the list
        onView(withText(DESCRIPTION)).perform(click());
        deleteAlarmWithDescription(DESCRIPTION);
    }

    private void deleteAlarmWithDescription(String description) {
        onView(withItemText(description)).perform(click());

        onView(withId(R.id.txt_delete)).perform(click());

        // Verify alarm removed
        onView(withItemText(description)).check(matches(not(isDisplayed())));
    }

    private void createAlarm(AlarmType alarmType, String description) {
        // Click on the add alarm fab
        onView(withId(R.id.create_alarm_fab)).perform(click());

        if (alarmType == AlarmType.REMINDER) {
            onView(withId(R.id.reminder_type_radio_button)).perform(click());
            onView(withId(R.id.alarm_description)).perform(typeText(description),
                    closeSoftKeyboard());
        } else if (alarmType == AlarmType.ALARM) {
            onView(withId(R.id.alarm_type_radio_button)).perform(click());
        }

        // move to time selection step
        onView(withId(R.id.next_step_fab)).perform(click());

        // move to days selection step
        onView(withId(R.id.next_step_fab)).perform(click());

        onView(withId(R.id.monday_tb)).perform(click());
        onView(withId(R.id.friday_tb)).perform(click());

        // Save the alarm
        onView(withId(R.id.next_step_fab)).perform(click());
    }

    /**
     * A custom {@link Matcher} which matches an item in a {@link ListView} by its text.
     * <p>
     * View constraints:
     * <ul>
     * <li>View must be a child of a {@link ListView}
     * <ul>
     *
     * @param itemText the text to match
     * @return Matcher that matches text in the given view
     */
    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(ListView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA LV with text " + itemText);
            }
        };
    }

    public static Matcher<View> withBackgroundColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public boolean matchesSafely(View warning) {
                ColorDrawable colorDrawable = (ColorDrawable) warning.getBackground();
                return color == colorDrawable.getColor();
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }


}
