package com.katsuna.clock.validators;

import com.google.common.base.Strings;
import com.katsuna.clock.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmValidator implements BaseValidator {

    private final String mHour;
    private final String mMinute;

    public AlarmValidator(String hour, String minute) {
        mHour = hour;
        mMinute = minute;
    }

    @Override
    public List<ValidationResult> validate() {
        List<ValidationResult> results = new ArrayList<>();

        if (!isHourValid()) {
            results.add(new ValidationResult(R.string.validation_hour));
        }
        if (!isMinuteValid()) {
            results.add(new ValidationResult(R.string.validation_minute));
        }

        return results;
    }

    private boolean isHourValid() {
        boolean isHourValid = true;

        if (Strings.isNullOrEmpty(mHour)) {
            isHourValid = false;
        } else {
            try {
                int hour = Integer.parseInt(mHour);
                if (hour > 23 || hour < 0) {
                    isHourValid = false;
                }
            } catch (NumberFormatException ex) {
                isHourValid = false;
            }
        }
        return isHourValid;
    }

    private boolean isMinuteValid() {
        boolean isMinuteValid = true;

        if (Strings.isNullOrEmpty(mMinute)) {
            isMinuteValid = false;
        } else {
            try {
                int minute = Integer.parseInt(mMinute);
                if (minute > 59 || minute < 0) {
                    isMinuteValid = false;
                }
            } catch (NumberFormatException ex) {
                isMinuteValid = false;
            }
        }

        return isMinuteValid;
    }
}
