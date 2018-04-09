package com.katsuna.clock.alarms;

import android.support.annotation.NonNull;

import com.katsuna.clock.BasePresenter;
import com.katsuna.clock.BaseView;
import com.katsuna.clock.data.Alarm;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
interface AlarmsContract {

    interface View extends BaseView<Presenter> {

        void showAlarms(List<Alarm> alarms);

        void showDateTime(String time, String date);

        void showAddAlarm();

        void showAlarmDetailsUi(String alarmId);

        void showNoAlarms();

        void removeAlarm(Alarm alarm);
    }

    interface Presenter extends BasePresenter {

        void loadAlarms();

        void loadDateTime();

        void addNewAlarm();

        void openAlarmDetails(@NonNull Alarm alarm);

        void deleteAlarm(@NonNull Alarm alarm);

        void turnOffAlarm(@NonNull Alarm alarm);
    }
}
