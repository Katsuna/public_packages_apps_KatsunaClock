/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
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

        void moveFabsToBottomAndTint(boolean flag);
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
