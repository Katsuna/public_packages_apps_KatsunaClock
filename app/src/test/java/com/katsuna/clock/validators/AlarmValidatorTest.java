package com.katsuna.clock.validators;

import com.katsuna.clock.R;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.internal.configuration.GlobalConfiguration.validate;

public class AlarmValidatorTest {

    @Test
    public void validAlarmInput_returnsNoValidationResults(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("13", "34");

        // validator returns zero results
        assertTrue(alarmValidator.validate().isEmpty());
    }

    @Test
    public void emptyHourAlarmInput_returnsValidationResult(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("", "34");

        // validator returns results
        List<ValidationResult> results = alarmValidator.validate();
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_hour);
    }

    @Test
    public void emptyMinuteAlarmInput_returnsValidationResult(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("10", "");

        // validator returns results
        List<ValidationResult> results = alarmValidator.validate();
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_minute);
    }

    @Test
    public void outOfLowerRangeMinuteAlarmInput_returnsValidationResult(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("10", "-1");

        // validator returns results
        List<ValidationResult> results = alarmValidator.validate();
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_minute);
    }

    @Test
    public void outOfUpperRangeMinuteAlarmInput_returnsValidationResult(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("10", "60");

        // validator returns results
        List<ValidationResult> results = alarmValidator.validate();
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_minute);
    }

    @Test
    public void outOfLowerRangeHourAlarmInput_returnsValidationResult(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("-1", "10");

        // validator returns results
        List<ValidationResult> results = alarmValidator.validate();
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_hour);
    }

    @Test
    public void outOfUpperRangeHourAlarmInput_returnsValidationResult(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("24", "59");

        // validator returns results
        List<ValidationResult> results = alarmValidator.validate();
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_hour);
    }


    @Test
    public void unparsableAlarmInput_returnsValidationResult(){
        // given valid input
        AlarmValidator alarmValidator = new AlarmValidator("text", "text");

        // validator returns results
        List<ValidationResult> results = alarmValidator.validate();
        assertTrue(results.size() == 2);

        ValidationResult hourResult = results.get(0);
        assertTrue(hourResult.messageResId == R.string.validation_hour);

        ValidationResult minuteResult = results.get(1);
        assertTrue(minuteResult.messageResId == R.string.validation_minute);
    }


}
