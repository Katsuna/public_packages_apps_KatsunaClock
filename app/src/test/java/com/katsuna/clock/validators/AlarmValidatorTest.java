package com.katsuna.clock.validators;

import com.katsuna.clock.R;
import com.katsuna.clock.data.AlarmType;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class AlarmValidatorTest {


    private IAlarmValidator mValidator;

    @Before
    public void setup() {
        mValidator = new AlarmValidator();
    }

    @Test
    public void validAlarmInput_returnsNoValidationResults() {
        // given valid input
        // validator returns zero results
        assertTrue(mValidator.validateAll(AlarmType.ALARM, "", "13", "34").isEmpty());
    }

    @Test
    public void emptyHourAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("", "34");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_hour);
    }

    @Test
    public void emptyMinuteAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("10", "");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_minute);
    }

    @Test
    public void outOfLowerRangeMinuteAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("10", "-1");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_minute);
    }

    @Test
    public void outOfUpperRangeMinuteAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("10", "60");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_minute);
    }

    @Test
    public void outOfLowerRangeHourAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("-1", "10");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_hour);
    }

    @Test
    public void outOfUpperRangeHourAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("24", "59");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_hour);
    }


    @Test
    public void unparsableAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("text", "text");
        assertTrue(results.size() == 2);

        ValidationResult hourResult = results.get(0);
        assertTrue(hourResult.messageResId == R.string.validation_hour);

        ValidationResult minuteResult = results.get(1);
        assertTrue(minuteResult.messageResId == R.string.validation_minute);
    }

    @Test
    public void emptyAlarmType_returnsValidationResult() {
        // given invalid input
        // validator returns result
        List<ValidationResult> results = mValidator.validateAlarmType(null, "");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.validation_alarm_type);
    }

    @Test
    public void alarmTypeWithDescription_returnsValidationResult() {
        // given invalid input
        // validator returns result
        List<ValidationResult> results = mValidator.validateAlarmType(AlarmType.ALARM, "desc");
        assertTrue(results.size() == 1);

        ValidationResult result = results.get(0);
        assertTrue(result.messageResId == R.string.unsupported_operation);
    }


}
