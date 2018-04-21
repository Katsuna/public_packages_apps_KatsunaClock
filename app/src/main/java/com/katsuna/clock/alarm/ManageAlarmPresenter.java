package com.katsuna.clock.alarm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.validators.IAlarmValidator;
import com.katsuna.clock.validators.ValidationResult;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

class ManageAlarmPresenter implements ManageAlarmContract.Presenter,
        AlarmsDataSource.GetAlarmCallback {

    @NonNull
    private final AlarmsDataSource mAlarmsDataSource;

    @NonNull
    private final ManageAlarmContract.View mManageAlarmView;

    @NonNull
    private final IAlarmValidator mAlarmValidator;

    @Nullable
    private final String mAlarmId;

    private boolean mIsDataMissing = true;
    private ManageAlarmStep mStep = ManageAlarmStep.TYPE;

    public ManageAlarmPresenter(@Nullable String alarmId,
                                @NonNull AlarmsDataSource alarmsDataSource,
                                @NonNull ManageAlarmContract.View manageAlarmView,
                                @NonNull IAlarmValidator alarmValidator) {
        mAlarmId = alarmId;
        mAlarmsDataSource = checkNotNull(alarmsDataSource, "dataSource cannot be null");
        mManageAlarmView = checkNotNull(manageAlarmView, "manageAlarmView cannot be null!");
        mAlarmValidator = checkNotNull(alarmValidator, "alarmValidator cannot be null!");

        mManageAlarmView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewAlarm() && mIsDataMissing) {
            populateAlarm();
        } else {
            initTime();
        }
    }

    private void initTime() {
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        mManageAlarmView.setTime(String.valueOf(currentHour), String.valueOf(currentMinute));
    }

    @Override
    public void saveAlarm(@NonNull AlarmType alarmType, String description, String hour, String minute,
                          boolean mondayEnabled, boolean tuesdayEnabled, boolean wednesdayEnabled,
                          boolean thursdayEnabled, boolean fridayEnabled, boolean saturdayEnabled,
                          boolean sundayEnabled) {
        List<ValidationResult> results = mAlarmValidator.validateAll(alarmType, description, hour,
                minute);
        if (results.size() == 0) {
            // all good, move on
            Alarm alarm;
            if (isNewAlarm()) {
                alarm = new Alarm(alarmType, Integer.parseInt(hour), Integer.parseInt(minute),
                        description, mondayEnabled, tuesdayEnabled, wednesdayEnabled, thursdayEnabled,
                        fridayEnabled, saturdayEnabled, sundayEnabled, AlarmStatus.ACTIVE);
            } else {
                alarm = new Alarm(Objects.requireNonNull(mAlarmId), alarmType, Integer.parseInt(hour), Integer.parseInt(minute),
                        description, mondayEnabled, tuesdayEnabled, wednesdayEnabled, thursdayEnabled,
                        fridayEnabled, saturdayEnabled, sundayEnabled, AlarmStatus.ACTIVE);
            }
            mAlarmsDataSource.saveAlarm(alarm);
            mManageAlarmView.showAlarmsList();
        } else {
            mManageAlarmView.showValidationResults(results);
        }
    }

    @Override
    public void populateAlarm() {
        if (isNewAlarm()) {
            throw new RuntimeException("populateAlarm() was called but alarm is new.");
        }
        //noinspection ConstantConditions
        mAlarmsDataSource.getAlarm(mAlarmId, this);
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public void previousStep() {
        ManageAlarmStep nextStep = null;
        switch (mStep) {
            case TIME:
                nextStep = ManageAlarmStep.TYPE;
                break;
            case DAYS:
                nextStep = ManageAlarmStep.TIME;
                break;
        }

        if (nextStep != null) {
            mStep = nextStep;
            showStep(nextStep);
        }
    }

    @Override
    public void validateAlarmTypeInfo(AlarmType alarmType, String description) {
        List<ValidationResult> results = mAlarmValidator.validateAlarmType(alarmType, description);
        if (results.size() == 0) {
            showStep(ManageAlarmStep.TIME);
        } else {
            mManageAlarmView.showValidationResults(results);
        }
    }

    @Override
    public void validateAlarmTime(String hour, String minute) {
        List<ValidationResult> results = mAlarmValidator.validateTime(hour, minute);
        if (results.size() == 0) {
            showStep(ManageAlarmStep.DAYS);
        } else {
            mManageAlarmView.showValidationResults(results);
        }
    }

    @VisibleForTesting
    private void showStep(ManageAlarmStep step) {
        switch (step) {
            case TYPE:
                mManageAlarmView.showAlarmTypeControl(true);
                mManageAlarmView.showAlarmTimeControl(false);
                mManageAlarmView.showPreviousStepFab(false);
                break;
            case TIME:
                mManageAlarmView.showAlarmTypeControlUnfocused();
                mManageAlarmView.showAlarmTimeControlInputMode();
                mManageAlarmView.showAlarmDaysControl(false);
                mManageAlarmView.showPreviousStepFab(true);
                break;
            case DAYS:
                mManageAlarmView.showAlarmTypeControlUnfocused();
                mManageAlarmView.showAlarmTimeControlUnfocused();
                mManageAlarmView.showAlarmDaysControl(true);
                break;
        }
        mStep = step;
    }

    @Override
    public ManageAlarmStep getCurrentStep() {
        return mStep;
    }

    @Override
    public void alarmTypeSelected(AlarmType alarmType) {
        mManageAlarmView.showDescriptionControl(alarmType == AlarmType.REMINDER);
        mManageAlarmView.showNextStepFab(true);
    }

    private boolean isNewAlarm() {
        return mAlarmId == null;
    }

    @Override
    public void onAlarmLoaded(Alarm alarm) {
        mManageAlarmView.loadAlarm(alarm);
        mIsDataMissing = false;
        mManageAlarmView.showNextStepFab(true);
    }

    @Override
    public void onDataNotAvailable() {
        mManageAlarmView.showEmptyAlarmError();
    }
}
