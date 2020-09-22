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
package com.katsuna.clock.alarm;

import android.support.annotation.NonNull;

import com.katsuna.clock.BasePresenter;
import com.katsuna.clock.BaseView;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.validators.ValidationResult;

import java.util.List;

class ManageAlarmContract {

    interface View extends BaseView<ManageAlarmContract.Presenter> {
        void showEmptyAlarmError();

        void showAlarmsList(Alarm alarm);

        void setTime(String hour, String minute);

        void setHour(String hour);

        void setMinute(String minute);

        void loadAlarm(Alarm alarm);

        void showValidationResults(List<ValidationResult> results);

        void showNextStepFab(boolean flag);

        void showPreviousStepFab(boolean flag);

        void showDescriptionControl(boolean flag);

        void showAlarmTimeControl(boolean flag);

        void showAlarmDaysControl(boolean flag);

        void adjustFabPositions(ManageAlarmStep step);

        void showAlarmOptionsControl(boolean flag);

        void showDescriptionStep(boolean flag);

        void setTitle(int resId);

        void setAlarmTimeTitle(int resId);

        void setDefaultRingtone();

        void setDefaultVibrate();

        void hideKeyboard();
    }

    interface Presenter extends BasePresenter {

        void saveAlarm(String description, String hour, String minute,
                       boolean mondayEnabled, boolean tuesdayEnabled, boolean wednesdayEnabled,
                       boolean thursdayEnabled, boolean fridayEnabled, boolean saturdayEnabled,
                       boolean sundayEnabled, @NonNull String ringToneUri, boolean vibrateEnabled);

        void populateAlarm();

        void previousStep();

        void validateAlarmTypeInfo(AlarmType alarmType, String description);

        void validateAlarmTime(String hour, String minute);

        ManageAlarmStep getCurrentStep();

        void addHours(String hour, int hours);

        void addMinutes(String minute, int minutes);

        AlarmType getAlarmType();

        void showStep(ManageAlarmStep step);
    }

}
