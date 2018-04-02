package com.katsuna.clock.alarm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.source.AlarmsDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class ManageAlarmPresenter implements ManageAlarmContract.Presenter, AlarmsDataSource.GetAlarmCallback {

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
    public void saveAlarm(Alarm alarm) {
        if (isNewAlarm()) {
            createAlarm(alarm);
        } else {
            updateAlarm(alarm);
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
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mAlarmsDataSource.saveAlarm(alarm);
        mManageAlarmView.showAlarmsList(); // After an edit, go back to the list.
    }

    @Override
    public void onAlarmLoaded(Alarm alarm) {
        //mManageAlarmView.setTitle(alarm.getTitle());
        mManageAlarmView.setDescription(alarm.getDescription());
        mIsDataMissing = false;
    }

    @Override
    public void onDataNotAvailable() {
        mManageAlarmView.showEmptyAlarmError();
    }
}
