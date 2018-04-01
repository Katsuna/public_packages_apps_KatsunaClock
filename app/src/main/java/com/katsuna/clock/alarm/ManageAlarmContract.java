package com.katsuna.clock.alarm;

import com.katsuna.clock.BasePresenter;
import com.katsuna.clock.BaseView;
import com.katsuna.clock.data.Alarm;

public class ManageAlarmContract {

    interface View extends BaseView<ManageAlarmContract.Presenter> {
        void showEmptyAlarmError();

        void showAlarmsList();

        void setTitle(String title);

        void setDescription(String description);
    }

    interface Presenter extends BasePresenter {

        void saveAlarm(Alarm alarm);

        void populateAlarm();

        boolean isDataMissing();
    }

}
