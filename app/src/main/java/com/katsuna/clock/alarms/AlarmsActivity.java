package com.katsuna.clock.alarms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.katsuna.clock.R;
import com.katsuna.clock.alarm.ManageAlarmActivity;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.util.Injection;

import java.util.ArrayList;
import java.util.List;


/**
 * Display a list of {@link Alarm}s. User can choose to view all active and inactive alarms,
 * and create and edit alarms.
 */
public class AlarmsActivity extends AppCompatActivity implements AlarmsContract.View {

    private static final String TAG = "AlarmsActivity";
    private AlarmsContract.Presenter mPresenter;
    /**
     * Listener for clicks on alarms in the ListView.
     */
    private final AlarmItemListener mItemListener = new AlarmItemListener() {
        @Override
        public void onAlarmFocus(@NonNull Alarm alarm, boolean focus) {
            mPresenter.focusOnAlarm(alarm, focus);
        }

        @Override
        public void onAlarmEdit(@NonNull Alarm alarm) {
            mPresenter.openAlarmDetails(alarm);
        }

        @Override
        public void onAlarmStatusUpdate(@NonNull Alarm alarm, @NonNull AlarmStatus alarmStatus) {
            mPresenter.updateAlarmStatus(alarm, alarmStatus);
        }

        @Override
        public void onAlarmDelete(@NonNull Alarm alarm) {
            mPresenter.deleteAlarm(alarm);
        }
    };
    private TextView mTime;
    private TextView mDate;
    private TextView mNoAlarmsText;
    private Button mCreateAlarmButton;
    private FloatingActionButton mCreateAlarmFab;
    private ListView mAlarmsList;
    private AlarmsAdapter mAlarmsAdapter;

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
        mNoAlarmsText = findViewById(R.id.no_alarms);
        mCreateAlarmButton = findViewById(R.id.create_alarm_button);
        mCreateAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewAlarm();
            }
        });

        mCreateAlarmFab = findViewById(R.id.create_alarm_fab);
        mCreateAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewAlarm();
            }
        });

        mAlarmsAdapter = new AlarmsAdapter(new ArrayList<Alarm>(0), mItemListener);
        mAlarmsList = findViewById(R.id.alarms_list);
        mAlarmsList.setAdapter(mAlarmsAdapter);
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
        mAlarmsAdapter.replaceData(alarms);

        mAlarmsList.setVisibility(View.VISIBLE);
        mNoAlarmsText.setVisibility(View.GONE);
        Log.d(TAG, "alarms fetched: " + alarms.size());
    }

    @Override
    public void showDateTime(String time, String date) {
        mTime.setText(time);
        mDate.setText(date);
    }

    @Override
    public void showAddAlarm() {
        Intent i = new Intent(this, ManageAlarmActivity.class);
        startActivity(i);
    }

    @Override
    public void showAlarmDetailsUi(String alarmId) {
        Intent intent = new Intent(this, ManageAlarmActivity.class);
        intent.putExtra(ManageAlarmActivity.EXTRA_ALARM_ID, alarmId);
        startActivity(intent);
    }

    @Override
    public void showNoAlarms() {
        mNoAlarmsText.setVisibility(View.VISIBLE);
        mCreateAlarmButton.setVisibility(View.VISIBLE);
        mAlarmsList.setVisibility(View.GONE);
    }

    @Override
    public void focusOnAlarm(Alarm alarm, boolean focus) {
        mAlarmsAdapter.focusOnAlarm(alarm, focus);
    }

    @Override
    public void reloadAlarm(Alarm alarm) {
        mAlarmsAdapter.reloadAlarm(alarm);
    }

}
