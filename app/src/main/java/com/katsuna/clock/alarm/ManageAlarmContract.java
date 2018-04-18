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

        void showAlarmsList();

        void setTime(String hour, String minute);

        void loadAlarm(Alarm alarm);

        void showValidationResults(List<ValidationResult> results);

        void showNextStepFab(boolean flag);

        void showPreviousStepFab(boolean flag);

        void showDescriptionControl(boolean flag);

        void showAlarmTypeControl(boolean flag);

        void showAlarmTypeControlUnfocused();

        void showAlarmTimeControl(boolean flag);

        void showAlarmTimeControlInputMode();

        void showAlarmTimeControlUnfocused();

        void showAlarmDaysControl(boolean flag);
    }

    interface Presenter extends BasePresenter {

        void saveAlarm(@NonNull AlarmType alarmType, String description, String hour, String minute,
                       boolean mondayEnabled, boolean tuesdayEnabled, boolean wednesdayEnabled,
                       boolean thursdayEnabled, boolean fridayEnabled, boolean saturdayEnabled,
                       boolean sundayEnabled);

        void populateAlarm();

        boolean isDataMissing();

        void previousStep();

        void validateAlarmTypeInfo(AlarmType alarmType, String description);

        void validateAlarmTime(String hour, String minute);

        ManageAlarmStep getCurrentStep();

        void alarmTypeSelected(AlarmType alarmType);
    }

}
