package com.katsuna.clock.alarms;

import android.support.annotation.NonNull;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.util.EspressoIdlingResource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public AlarmsPresenter(@NonNull AlarmsDataSource alarmsDataSource,
                           @NonNull AlarmsContract.View alarmsView) {
        mAlarmsDataSource = checkNotNull(alarmsDataSource, "dataSource cannot be null");
        mAlarmsView = checkNotNull(alarmsView, "alarmsView cannot be null!");

        mAlarmsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadDateTime();
        loadAlarms();
    }

    @Override
    public void loadDateTime() {
        Date now = new Date();
        Locale locale = Locale.getDefault();
        String time = new SimpleDateFormat("HH:mm", locale).format(now);
        String date = new SimpleDateFormat("EEEE ,d MMMM", locale).format(now);
        mAlarmsView.showDateTime(time, date);
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
    public void openAlarmDetails(@NonNull Alarm requestedAlarm) {
        mAlarmsView.showAlarmDetailsUi(requestedAlarm.getId());
    }

    @Override
    public void deleteAlarm(@NonNull Alarm alarm) {
        mAlarmsDataSource.deleteAlarm(alarm.getId());
        mAlarmsView.removeAlarm(alarm);
    }

    @Override
    public void turnOffAlarm(@NonNull Alarm alarm) {

    }
}