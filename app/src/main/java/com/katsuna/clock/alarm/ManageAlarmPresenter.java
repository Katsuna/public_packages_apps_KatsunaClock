package com.katsuna.clock.alarm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.validators.IAlarmValidator;
import com.katsuna.clock.validators.ValidationResult;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ManageAlarmPresenter implements ManageAlarmContract.Presenter,
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
    private Alarm mAlarm;

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
            mAlarm = new Alarm();
        }
    }

    @Override
    public void saveAlarm(boolean mondayEnabled, boolean tuesdayEnabled, boolean wednesdayEnabled,
                          boolean thursdayEnabled, boolean fridayEnabled, boolean saturdayEnabled,
                          boolean sundayEnabled, @NonNull AlarmStatus alarmStatus) {
        // all good, move on
        if (isNewAlarm()) {
            Alarm alarm = new Alarm(mAlarm.getAlarmType(), mAlarm.getHour(), mAlarm.getMinute(),
                    mAlarm.getDescription(), mondayEnabled, tuesdayEnabled, wednesdayEnabled,
                    thursdayEnabled, fridayEnabled, saturdayEnabled, sundayEnabled,
                    alarmStatus);
            createAlarm(alarm);
        } else {
            Alarm alarm = new Alarm(mAlarmId, mAlarm.getAlarmType(), mAlarm.getHour(),
                    mAlarm.getMinute(), mAlarm.getDescription(), mondayEnabled, tuesdayEnabled,
                    wednesdayEnabled, thursdayEnabled, fridayEnabled, saturdayEnabled,
                    sundayEnabled, alarmStatus);
            updateAlarm(alarm);
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
                nextStep = ManageAlarmStep.DAYS;
                break;
        }

        if (nextStep != null) {
            mStep = nextStep;
            showStep(nextStep);
        }
    }

    @Override
    public void setAlarmTypeInfo(AlarmType alarmType, String description) {
        List<ValidationResult> results = mAlarmValidator.validateAlarmType(alarmType, description);
        if (results.size() == 0) {
            mAlarm.setAlarmType(alarmType);
            mAlarm.setDescription(description);

            showStep(ManageAlarmStep.TIME);
        } else {
            mManageAlarmView.showValidationResults(results);
        }
    }

    @Override
    public void setAlarmTime(String hour, String minute) {
        List<ValidationResult> results = mAlarmValidator.validateTime(hour, minute);
        if (results.size() == 0) {
            mAlarm.setHour(Integer.parseInt(hour));
            mAlarm.setMinute(Integer.parseInt(minute));

            showStep(ManageAlarmStep.DAYS);
        } else {
            mManageAlarmView.showValidationResults(results);
        }
    }

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
                mManageAlarmView.showPreviousStepFab(true);
                break;
            case DAYS:

                break;
        }
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

    private void createAlarm(Alarm alarm) {
        mAlarmsDataSource.saveAlarm(alarm);
        mManageAlarmView.showAlarmsList();
    }

    private void updateAlarm(Alarm alarm) {
        mAlarmsDataSource.saveAlarm(alarm);
        mManageAlarmView.showAlarmsList(); // After an edit, go back to the list.
    }

    @Override
    public void onAlarmLoaded(Alarm alarm) {
        mManageAlarmView.loadAlarm(alarm);
        mIsDataMissing = false;
        mAlarm = alarm;
    }

    @Override
    public void onDataNotAvailable() {
        mManageAlarmView.showEmptyAlarmError();
    }
}
