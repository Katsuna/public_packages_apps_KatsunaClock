package com.katsuna.clock.alarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.util.Injection;
import com.katsuna.clock.validators.ValidationResult;

import java.util.List;

public class ManageAlarmActivity extends AppCompatActivity implements ManageAlarmContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_alarm);

        // Create the presenter
        new ManageAlarmPresenter(null, Injection.provideAlarmsDataSource(getApplicationContext()),
                this, Injection.provideAlarmValidator());
    }

    @Override
    public void showEmptyAlarmError() {

    }

    @Override
    public void showAlarmsList() {

    }

    @Override
    public void loadAlarm(Alarm alarm) {

    }

    @Override
    public void showValidationResults(List<ValidationResult> results) {
        Log.d("AAA", results.toString());
    }

    @Override
    public void showStep(ManageAlarmStep step) {
        // TODO
    }

    @Override
    public void setPresenter(ManageAlarmContract.Presenter presenter) {

    }

    // TODO: remove this
    public void createNewAlarm(View view) {
        AlarmsDataSource dataSource = Injection.provideAlarmsDataSource(this);
        dataSource.saveAlarm(new Alarm(AlarmType.ALARM, "desc 1"));
    }

}
