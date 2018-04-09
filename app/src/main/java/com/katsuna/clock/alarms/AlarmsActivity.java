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
    private TextView mTime;
    private TextView mDate;
    private TextView mNoAlarmsText;
    private Button mCreateAlarmButton;
    private FloatingActionButton mCreateAlarmFab;
    private ListView mAlarmsList;
    private AlarmsAdapter mAlarmsAdapter;

    /**
     * Listener for clicks on alarms in the ListView.
     */
    private final AlarmItemListener mItemListener = new AlarmItemListener() {
        @Override
        public void onAlarmEdit(Alarm alarm) {
            //mPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onAlarmTurnOff(Alarm alarm) {
            //mPresenter.completeTask(completedTask);
        }

        @Override
        public void onAlarmDelete(Alarm alarm) {
            mPresenter.deleteAlarm(alarm);
        }
    };

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
                launchManageActivity();
            }
        });

        mCreateAlarmFab = findViewById(R.id.create_alarm_fab);
        mCreateAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchManageActivity();
            }
        });

        mAlarmsAdapter = new AlarmsAdapter(new ArrayList<Alarm>(0), mItemListener);
        mAlarmsList = findViewById(R.id.alarms_list);
        mAlarmsList.setAdapter(mAlarmsAdapter);
    }

    private void launchManageActivity() {
        Intent i = new Intent(this, ManageAlarmActivity.class);
        startActivity(i);
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

    }

    @Override
    public void showAlarmDetailsUi(String alarmId) {

    }

    @Override
    public void showNoAlarms() {
        mNoAlarmsText.setVisibility(View.VISIBLE);
        mCreateAlarmButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeAlarm(Alarm alarm) {
        // TODO
    }
}
