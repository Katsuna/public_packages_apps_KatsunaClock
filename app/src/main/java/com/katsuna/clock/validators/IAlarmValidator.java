package com.katsuna.clock.validators;

import com.katsuna.clock.data.AlarmType;

import java.util.List;

public interface IAlarmValidator {

    List<ValidationResult> validateAll(AlarmType alarmType, String description, String hour,
                                       String minute);

    List<ValidationResult> validateAlarmType(AlarmType alarmType, String description);

    List<ValidationResult> validateTime(String hour, String minute);
}
