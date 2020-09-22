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
package com.katsuna.clock.validators;

import com.katsuna.clock.R;
import com.katsuna.clock.data.AlarmType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlarmValidatorTest {


    private IAlarmValidator mValidator;

    @Before
    public void start() {
        mValidator = new AlarmValidator();
    }

    @After
    public void stop() {
        mValidator = null;
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
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.validation_hour);
    }

    @Test
    public void emptyMinuteAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("10", "");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.validation_minute);
    }

    @Test
    public void outOfLowerRangeMinuteAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("10", "-1");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.validation_minute);
    }

    @Test
    public void outOfUpperRangeMinuteAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("10", "60");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.validation_minute);
    }

    @Test
    public void outOfLowerRangeHourAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("-1", "10");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.validation_hour);
    }

    @Test
    public void outOfUpperRangeHourAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("24", "59");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.validation_hour);
    }


    @Test
    public void unparsableAlarmInput_returnsValidationResult() {
        // given invalid input
        // validator returns results
        List<ValidationResult> results = mValidator.validateTime("text", "text");
        assertEquals(2, results.size());

        ValidationResult hourResult = results.get(0);
        assertEquals(hourResult.messageResId, R.string.validation_hour);

        ValidationResult minuteResult = results.get(1);
        assertEquals(minuteResult.messageResId, R.string.validation_minute);
    }

    @Test
    public void emptyAlarmType_returnsValidationResult() {
        // given invalid input
        // validator returns result
        List<ValidationResult> results = mValidator.validateAlarmType(null, "");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.validation_alarm_type);
    }

    @Test
    public void alarmTypeWithDescription_returnsValidationResult() {
        // given invalid input
        // validator returns result
        List<ValidationResult> results = mValidator.validateAlarmType(AlarmType.ALARM, "desc");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.unsupported_operation);
    }

    @Test
    public void reminderWithoutDescription_returnsValidationResult() {
        // given invalid input
        // validator returns result
        List<ValidationResult> results = mValidator.validateAlarmType(AlarmType.REMINDER, "");
        assertEquals(1, results.size());

        ValidationResult result = results.get(0);
        assertEquals(result.messageResId, R.string.reminder_with_no_description);
    }


}
