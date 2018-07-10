package com.katsuna.clock.alarms;

import android.support.annotation.NonNull;

import com.katsuna.clock.BasePresenter;
import com.katsuna.clock.BaseView;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
interface AlarmsContract {

    interface View extends BaseView<Presenter> {

        void showAlarms(List<Alarm> alarms);

        void showAddAlarm();

        void showAddReminder();

        void showAlarmDetailsUi(long alarmId);

        void showNoAlarms();

        void focusOnAlarm(Alarm alarm, boolean focus);

        void reloadAlarm(Alarm alarm);
    }

    interface Presenter extends BasePresenter {

        void loadAlarms();

        void addNewAlarm();

        void addNewReminder();

        void openAlarmDetails(@NonNull Alarm alarm);

        void deleteAlarm(@NonNull Alarm alarm);

        void updateAlarmStatus(@NonNull Alarm alarm, @NonNull AlarmStatus alarmStatus);

        void focusOnAlarm(@NonNull Alarm alarm, boolean focus);
    }
}
