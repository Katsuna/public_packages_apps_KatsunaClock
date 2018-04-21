package com.katsuna.clock.validators;

import com.google.common.base.Strings;
import com.katsuna.clock.R;
import com.katsuna.clock.data.AlarmType;

import java.util.ArrayList;
import java.util.List;

public class AlarmValidator implements IAlarmValidator {

    @Override
    public List<ValidationResult> validateAll(AlarmType alarmType, String description, String hour,
                                              String minute) {
        List<ValidationResult> results = new ArrayList<>();
        results.addAll(validateAlarmType(alarmType, description));
        results.addAll(validateTime(hour, minute));

        return results;
    }

    @Override
    public List<ValidationResult> validateAlarmType(AlarmType alarmType, String description) {
        List<ValidationResult> results = new ArrayList<>();

        if (alarmType == null) {
            results.add(new ValidationResult(R.string.validation_alarm_type));
        } else {
            if (alarmType == AlarmType.ALARM && !Strings.isNullOrEmpty(description)) {
                results.add(new ValidationResult(R.string.unsupported_operation));
            }
        }

        return results;
    }

    @Override
    public List<ValidationResult> validateTime(String hour, String minute) {
        List<ValidationResult> results = new ArrayList<>();

        if (!isHourValid(hour)) {
            results.add(new ValidationResult(R.string.validation_hour));
        }
        if (!isMinuteValid(minute)) {
            results.add(new ValidationResult(R.string.validation_minute));
        }

        return results;
    }


    private boolean isHourValid(String input) {
        boolean isHourValid = true;

        if (Strings.isNullOrEmpty(input)) {
            isHourValid = false;
        } else {
            try {
                int hour = Integer.parseInt(input);
                if (hour > 23 || hour < 0) {
                    isHourValid = false;
                }
            } catch (NumberFormatException ex) {
                isHourValid = false;
            }
        }
        return isHourValid;
    }

    private boolean isMinuteValid(String input) {
        boolean isMinuteValid = true;

        if (Strings.isNullOrEmpty(input)) {
            isMinuteValid = false;
        } else {
            try {
                int minute = Integer.parseInt(input);
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
