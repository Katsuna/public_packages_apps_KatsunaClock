package com.katsuna.clock.formatters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.utils.ColorCalcV2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AlarmFormatter {

    private final Alarm mAlarm;
    private final Context mContext;

    public AlarmFormatter(@NonNull Context context, @NonNull Alarm alarm) {
        mContext = context;
        mAlarm = alarm;
    }

    public String getDays() {
        List<String> days = new ArrayList<>();
        if (mAlarm.isMondayEnabled()) {
            days.add(getString(R.string.monday));
        }
        if (mAlarm.isTuesdayEnabled()) {
            days.add(getString(R.string.tuesday));
        }
        if (mAlarm.isWednesdayEnabled()) {
            days.add(getString(R.string.wednesday));
        }
        if (mAlarm.isThursdayEnabled()) {
            days.add(getString(R.string.thursday));
        }
        if (mAlarm.isFridayEnabled()) {
            days.add(getString(R.string.friday));
        }
        if (mAlarm.isSaturdayEnabled()) {
            days.add(getString(R.string.saturday));
        }
        if (mAlarm.isSundayEnabled()) {
            days.add(getString(R.string.sunday));
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            sb.append(days.get(i));
            if (days.size() > (i+2)) {
                // we have at least two more to add
                sb.append(", ");
            } else if (days.size() > (i+1)) {
                // we have one more to add
                sb.append(" ").append(getString(R.string.and)).append(" ");
            }
        }

        return sb.toString();
    }

    public int getAlarmTypeIconResId() {
        int output;
        if (mAlarm.getAlarmType() == AlarmType.REMINDER) {
            output = R.drawable.ic_notifications_24dp;
        } else {
            output = R.drawable.ic_access_time_24dp;
        }
        return output;
    }

    public String getTitle() {
        String output;
        if (mAlarm.getAlarmType() == AlarmType.ALARM) {
            output = showTime();
        } else {
            output = mAlarm.getDescription();
        }
        return output;
    }

    public String getDescription() {
        String output;
        if (mAlarm.getAlarmType() == AlarmType.ALARM) {
            output = getString(R.string.alarm);
        } else {
            output = showTime();
        }
        return output;
    }

    private String getString(int resId) {
        return mContext.getString(resId);
    }

    private String showTime() {
        return String.format(Locale.getDefault(), "%02d:%02d", mAlarm.getHour(), mAlarm.getMinute());
    }

    public int getCardHandleColor(UserProfile profile) {
        ColorProfileKeyV2 profileKey;
        if (mAlarm.getAlarmStatus() == AlarmStatus.INACTIVE) {
            profileKey = ColorProfileKeyV2.PRIMARY_GREY_1;
        } else if (mAlarm.getAlarmType() == AlarmType.ALARM) {
            profileKey = ColorProfileKeyV2.PRIMARY_COLOR_2;
        } else {
            profileKey = ColorProfileKeyV2.PRIMARY_COLOR_1;
        }
        return ColorCalcV2.getColorResId(profileKey, profile.colorProfile);
    }

    public int getCardInnerColor(UserProfile profile) {
        ColorProfileKeyV2 profileKey;
        if (mAlarm.getAlarmStatus() == AlarmStatus.INACTIVE) {
            profileKey = ColorProfileKeyV2.SECONDARY_GREY_2;
        } else if (mAlarm.getAlarmType() == AlarmType.ALARM) {
            profileKey = ColorProfileKeyV2.SECONDARY_COLOR_2;
        } else {
            profileKey = ColorProfileKeyV2.SECONDARY_COLOR_1;
        }
        return ColorCalcV2.getColorResId(profileKey, profile.colorProfile);
    }

}
