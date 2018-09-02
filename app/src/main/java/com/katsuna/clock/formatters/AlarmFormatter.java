package com.katsuna.clock.formatters;

import android.content.Context;
import android.support.annotation.NonNull;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.services.utils.NextAlarmCalculator;
import com.katsuna.clock.util.DateUtils;
import com.katsuna.clock.util.TimeDiff;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.utils.ColorCalcV2;

import org.threeten.bp.LocalDateTime;

import java.util.Locale;

public class AlarmFormatter {

    private final Alarm mAlarm;
    private final Context mContext;

    public AlarmFormatter(@NonNull Context context, @NonNull Alarm alarm) {
        mContext = context;
        mAlarm = alarm;
    }

    public String getDays() {
        return DaysFormatter.getDays(mContext, mAlarm.isMondayEnabled(), mAlarm.isTuesdayEnabled(),
                mAlarm.isWednesdayEnabled(), mAlarm.isThursdayEnabled(), mAlarm.isFridayEnabled(),
                mAlarm.isSaturdayEnabled(), mAlarm.isSundayEnabled());
    }

    public int getAlarmTypeIconResId() {
        int output;
        if (mAlarm.getAlarmType() == AlarmType.REMINDER) {
            output = R.drawable.ic_notifications_black54_24dp;
        } else {
            output = R.drawable.ic_access_time_24dp;
        }
        return output;
    }

    public String getTitle() {
        String output;
        if (mAlarm.getAlarmType() == AlarmType.ALARM) {
            output = mContext.getString(R.string.alarm_title, getString(R.string.alarm),
                    showTime());
        } else {
            output = mContext.getString(R.string.alarm_title, getString(R.string.reminder),
                    mAlarm.getDescription());
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
            profileKey = ColorProfileKeyV2.PRIMARY_COLOR_1;

        } else {
            profileKey = ColorProfileKeyV2.PRIMARY_COLOR_2;
        }
        return ColorCalcV2.getColorResId(profileKey, profile.colorProfile);
    }

    public int getCardInnerColor(UserProfile profile) {
        ColorProfileKeyV2 profileKey;
        if (mAlarm.getAlarmStatus() == AlarmStatus.INACTIVE) {
            profileKey = ColorProfileKeyV2.SECONDARY_GREY_2;
        } else if (mAlarm.getAlarmType() == AlarmType.ALARM) {
            profileKey = ColorProfileKeyV2.SECONDARY_COLOR_1;
        } else {
            profileKey = ColorProfileKeyV2.SECONDARY_COLOR_2;
        }
        return ColorCalcV2.getColorResId(profileKey, profile.colorProfile);
    }

    public String getAlertMessage() {
        String output = mAlarm.getAlarmType() == AlarmType.ALARM ?
                mContext.getString(R.string.alarm_is_set_at, showTime()) :
                mContext.getString(R.string.reminder_is_set_at, showTime());
        output += "\n";
        String days = getDaysFrequency();
        if (!days.isEmpty()) {
            output += days;
            output += "\n";
        }
        output += mContext.getString(R.string.remaining_time, getTimeUntilRing());
        return output;
    }

    public String getDaysFrequency() {
        String output = "";
        if (mAlarm.isRecurring()) {
            output += mContext.getString(R.string.every, getDays());
        }
        return output;
    }

    public String getRingInTime() {
        return mContext.getString(R.string.ring_in, getTimeUntilRing());
    }

    public String getTimeUntilRing() {
        NextAlarmCalculator nextAlarmCalculator = new NextAlarmCalculator();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime triggerDate  = nextAlarmCalculator.getTriggerDateTime(now, mAlarm);

        String timeDiffPresentation;
        TimeDiff timeDiff = DateUtils.calculateTimeDiff(now, triggerDate);
        if (timeDiff.days > 0) {
            timeDiffPresentation = mContext.getString(R.string.remaining_time_detail_days,
                    timeDiff.days, timeDiff.hours, timeDiff.minutes);
        } else if (timeDiff.hours > 0) {
            timeDiffPresentation = mContext.getString(R.string.remaining_time_detail_hours,
                    timeDiff.hours, timeDiff.minutes);
        } else {
            if (timeDiff.minutes == 0) {
                timeDiffPresentation = mContext.getString(R.string.remaining_time_few_seconds);
            } else {
                timeDiffPresentation = mContext.getString(R.string.remaining_time_detail_minutes,
                        timeDiff.minutes);
            }
        }
        return timeDiffPresentation;
    }


}
