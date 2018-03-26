package com.katsuna.clock.alarms;

import android.support.annotation.NonNull;

import com.katsuna.clock.BasePresenter;
import com.katsuna.clock.BaseView;
import com.katsuna.clock.data.Alarm;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface AlarmsContract {

    interface View extends BaseView<Presenter> {

        void showAlarms(List<Alarm> alarms);

        void showAddAlarm();

        void showAlarmDetailsUi(String alarmId);

        void showLoadingAlarmsError();

        void showNoAlarms();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadAlarms();

        void addNewAlarm();

        void openAlarmDetails(@NonNull Alarm requestedAlarm);
    }
}
