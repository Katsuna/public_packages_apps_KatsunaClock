package com.katsuna.clock.alarm;

import android.support.annotation.NonNull;

import com.katsuna.clock.BasePresenter;
import com.katsuna.clock.BaseView;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.validators.ValidationResult;

import java.util.List;

class ManageAlarmContract {

    interface View extends BaseView<ManageAlarmContract.Presenter> {
        void showEmptyAlarmError();

        void showAlarmsList();

        void loadAlarm(Alarm alarm);

        void showValidationResults(List<ValidationResult> results);

        void showNextStepFab(boolean flag);

        void showPreviousStepFab(boolean flag);

        void showDescriptionControl(boolean flag);

        void showAlarmTypeControl(boolean flag);

        void showAlarmTimeControl(boolean flag);

        void showAlarmTypeControlUnfocused();

        void showAlarmTimeControlInputMode();

    }

    interface Presenter extends BasePresenter {

        void saveAlarm(boolean mondayEnabled, boolean tuesdayEnabled, boolean wednesdayEnabled,
                       boolean thursdayEnabled, boolean fridayEnabled, boolean saturdayEnabled,
                       boolean sundayEnabled, @NonNull AlarmStatus alarmStatus);

        void populateAlarm();

        boolean isDataMissing();

        void previousStep();

        void setAlarmTypeInfo(AlarmType alarmType, String description);

        void setAlarmTime(String hour, String minute);

        ManageAlarmStep getCurrentStep();

        void alarmTypeSelected(AlarmType alarmType);
    }

}
