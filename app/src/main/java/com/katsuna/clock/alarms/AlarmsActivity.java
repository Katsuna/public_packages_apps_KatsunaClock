package com.katsuna.clock.alarms;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.util.Injection;

import java.util.List;


/**
 * Display a list of {@link Alarm}s. User can choose to view all active and inactive alarms,
 * and create and edit alarms.
 */
public class AlarmsActivity extends AppCompatActivity implements AlarmsContract.View {

    private static final String TAG = "AlarmsActivity";
    private AlarmsContract.Presenter mPresenter;
    private TextView mTime;
    private TextView mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms);

        init();

        // Create the presenter
        new AlarmsPresenter(Injection.provideAlarmsDataSource(getApplicationContext()), this);
    }

    private void init() {
        mTime = findViewById(R.id.time);
        mDate = findViewById(R.id.date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull AlarmsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showAlarms(List<Alarm> alarms) {
        Log.e(TAG, "alarms fetched: " + alarms.size());
    }

    @Override
    public void showDateTime(String time, String date) {
        mTime.setText(time);
        mDate.setText(date);
    }

    @Override
    public void showAddAlarm() {

    }

    @Override
    public void showAlarmDetailsUi(String alarmId) {

    }

    @Override
    public void showLoadingAlarmsError() {

    }

    @Override
    public void showNoAlarms() {
        Log.e(TAG, "showNoAlarms called");
    }

    @Override
    public void showSuccessfullySavedMessage() {

    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void showFilteringPopUpMenu() {

    }

    // TODO: remove this
    public void createNewAlarm(View view) {
        AlarmsDataSource dataSource = Injection.provideAlarmsDataSource(this);
        dataSource.saveAlarm(new Alarm(1, "desc 1"));
    }

    // TODO: remove this
    public void deleteAllAlarms(View view) {
        AlarmsDataSource dataSource = Injection.provideAlarmsDataSource(this);
        dataSource.deleteAlarms();
    }
}
