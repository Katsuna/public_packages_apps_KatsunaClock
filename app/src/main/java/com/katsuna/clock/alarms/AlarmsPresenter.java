package com.katsuna.clock.alarms;

import android.support.annotation.NonNull;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.EspressoIdlingResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link AlarmsActivity}), retrieves the data and updates the
 * UI as required.
 */
public class AlarmsPresenter implements AlarmsContract.Presenter {

    @NonNull
    private final AlarmsDataSource mAlarmsDataSource;

    @NonNull
    private final AlarmsContract.View mAlarmsView;

    @NonNull
    private final IAlarmsScheduler mAlarmsScheduler;

    AlarmsPresenter(@NonNull AlarmsDataSource alarmsDataSource,
                    @NonNull AlarmsContract.View alarmsView,
                    @NonNull IAlarmsScheduler alarmsScheduler) {
        mAlarmsDataSource = checkNotNull(alarmsDataSource, "dataSource cannot be null");
        mAlarmsView = checkNotNull(alarmsView, "alarmsView cannot be null!");
        mAlarmsScheduler = checkNotNull(alarmsScheduler, "alarmsScheduler cannot be null!");

        mAlarmsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadAlarms();
    }

    @Override
    public void loadAlarms() {
        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mAlarmsDataSource.getAlarms(new AlarmsDataSource.LoadAlarmsCallback() {
            @Override
            public void onAlarmsLoaded(List<Alarm> alarms) {
                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                mAlarmsView.showAlarms(alarms);
            }

            @Override
            public void onDataNotAvailable() {
                mAlarmsView.showNoAlarms();
            }
        });
    }

    public void addNewAlarm() {
        mAlarmsView.showAddAlarm();
    }

    @Override
    public void addNewReminder() {
        mAlarmsView.showAddReminder();
    }

    @Override
    public void openAlarmDetails(@NonNull Alarm requestedAlarm) {
        mAlarmsView.showAlarmDetailsUi(requestedAlarm.getAlarmId());
    }

    @Override
    public void deleteAlarm(@NonNull Alarm alarm) {
        mAlarmsDataSource.deleteAlarm(alarm.getAlarmId());
        mAlarmsScheduler.cancel(alarm);
        loadAlarms();
    }

    @Override
    public void updateAlarmStatus(@NonNull Alarm alarm, @NonNull AlarmStatus alarmStatus) {
        alarm.setAlarmStatus(alarmStatus);
        mAlarmsDataSource.saveAlarm(alarm);
        mAlarmsScheduler.reschedule(alarm);
        mAlarmsView.reloadAlarm(alarm);
    }

    @Override
    public void focusOnAlarm(@NonNull Alarm alarm, boolean focus) {
        mAlarmsView.focusOnAlarm(alarm, focus);
    }
}
