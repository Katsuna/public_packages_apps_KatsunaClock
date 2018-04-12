package com.katsuna.clock.alarm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.validators.AlarmValidator;
import com.katsuna.clock.validators.ValidationResult;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ManageAlarmPresenter implements ManageAlarmContract.Presenter,
        AlarmsDataSource.GetAlarmCallback {

    @NonNull
    private final AlarmsDataSource mAlarmsDataSource;

    @NonNull
    private final ManageAlarmContract.View mManageAlarmView;

    @Nullable
    private final String mAlarmId;

    private boolean mIsDataMissing = true;

    public ManageAlarmPresenter(@Nullable String alarmId,
                                @NonNull AlarmsDataSource alarmsDataSource,
                                @NonNull ManageAlarmContract.View manageAlarmView) {
        mAlarmId = alarmId;
        mAlarmsDataSource = checkNotNull(alarmsDataSource, "dataSource cannot be null");
        mManageAlarmView = checkNotNull(manageAlarmView, "manageAlarmView cannot be null!");

        mManageAlarmView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewAlarm() && mIsDataMissing) {
            populateAlarm();
        }
    }

    @Override
    public void saveAlarm(AlarmType alarmType, String hour, String minute, String description,
                          boolean mondayEnabled, boolean tuesdayEnabled, boolean wednesdayEnabled,
                          boolean thursdayEnabled, boolean fridayEnabled, boolean saturdayEnabled,
                          boolean sundayEnabled, AlarmStatus alarmStatus) {
        AlarmValidator validator = new AlarmValidator(hour, minute);

        List<ValidationResult> results = validator.validate();
        if (results.size() == 0) {
            // all good, move on
            if (isNewAlarm()) {
                Alarm alarm = new Alarm(alarmType, Integer.parseInt(hour), Integer.parseInt(minute),
                        description, mondayEnabled,  tuesdayEnabled,  wednesdayEnabled,
                        thursdayEnabled, fridayEnabled, saturdayEnabled, sundayEnabled,
                        alarmStatus);
                createAlarm(alarm);
            } else {
                Alarm alarm = new Alarm(mAlarmId, alarmType, Integer.parseInt(hour),
                        Integer.parseInt(minute), description, mondayEnabled, tuesdayEnabled,
                        wednesdayEnabled, thursdayEnabled, fridayEnabled, saturdayEnabled,
                        sundayEnabled, alarmStatus);
                updateAlarm(alarm);
            }
        } else {
            mManageAlarmView.showValidationResults(results);
        }
    }

    @Override
    public void populateAlarm() {
        if (isNewAlarm()) {
            throw new RuntimeException("populateAlarm() was called but task is new.");
        }
        //noinspection ConstantConditions
        mAlarmsDataSource.getAlarm(mAlarmId, this);
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewAlarm() {
        return mAlarmId == null;
    }

    private void createAlarm(Alarm alarm) {
        mAlarmsDataSource.saveAlarm(alarm);
        mManageAlarmView.showAlarmsList();
    }

    private void updateAlarm(Alarm alarm) {
        if (isNewAlarm()) {
            throw new RuntimeException("updateAlarm() was called but task is new.");
        }
        mAlarmsDataSource.saveAlarm(alarm);
        mManageAlarmView.showAlarmsList(); // After an edit, go back to the list.
    }

    @Override
    public void onAlarmLoaded(Alarm alarm) {
        mManageAlarmView.loadAlarm(alarm);
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        mManageAlarmView.showEmptyAlarmError();
    }
}
