package com.katsuna.clock.alarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.util.Injection;

public class ManageAlarmActivity extends AppCompatActivity implements ManageAlarmContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_alarm);

        // Create the presenter
        new ManageAlarmPresenter(null, Injection.provideAlarmsDataSource(getApplicationContext()), this);
    }

    @Override
    public void showEmptyAlarmError() {

    }

    @Override
    public void showAlarmsList() {

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setDescription(String description) {

    }

    @Override
    public void setPresenter(ManageAlarmContract.Presenter presenter) {

    }

    // TODO: remove this
    public void createNewAlarm(View view) {
        AlarmsDataSource dataSource = Injection.provideAlarmsDataSource(this);
        dataSource.saveAlarm(new Alarm(AlarmType.ALARM, "desc 1"));
    }

    // TODO: remove this
    public void deleteAllAlarms(View view) {
        AlarmsDataSource dataSource = Injection.provideAlarmsDataSource(this);
        dataSource.deleteAlarms();
    }

}
