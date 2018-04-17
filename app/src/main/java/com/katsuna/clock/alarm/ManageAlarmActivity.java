package com.katsuna.clock.alarm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.util.Injection;
import com.katsuna.clock.validators.ValidationResult;

import java.util.List;

public class ManageAlarmActivity extends AppCompatActivity implements ManageAlarmContract.View {

    private ManageAlarmContract.Presenter mPresenter;
    private FloatingActionButton mPreviousStepFab;
    private FloatingActionButton mNextStepFab;
    private View mAlarmTimeContainer;
    private View mAlarmTypeContainer;
    private View mAlarmDaysContainer;
    private RadioGroup mAlarmTypeRadioGroup;
    private EditText mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_alarm);

        init();

        // Create the presenter
        new ManageAlarmPresenter(null, Injection.provideAlarmsDataSource(getApplicationContext()),
                this, Injection.provideAlarmValidator());
    }

    private void init() {
        mAlarmTypeContainer = findViewById(R.id.alarm_type_container);
        mAlarmTypeRadioGroup = findViewById(R.id.alarm_type_radio_group);
        mAlarmTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.alarm_type_radio_button:
                        mPresenter.alarmTypeSelected(AlarmType.ALARM);
                        break;
                    case R.id.reminder_type_radio_button:
                        mPresenter.alarmTypeSelected(AlarmType.REMINDER);
                        break;
                }
            }
        });
        mDescription = findViewById(R.id.alarm_description);

        mAlarmTimeContainer = findViewById(R.id.alarm_time_container);
        mAlarmDaysContainer = findViewById(R.id.alarm_days_container);

        mPreviousStepFab = findViewById(R.id.prev_step_fab);
        mPreviousStepFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousStep();
            }
        });

        mNextStepFab = findViewById(R.id.next_step_fab);
        mNextStepFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextStep();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
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
    public void showNextStepFab(boolean flag) {
        mNextStepFab.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showPreviousStepFab(boolean flag) {
        mPreviousStepFab.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showDescriptionControl(boolean flag) {
        mDescription.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAlarmTypeControl(boolean flag) {
        mAlarmTypeRadioGroup.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAlarmTypeControlUnfocused() {
        // TODO
    }

    @Override
    public void showAlarmTimeControlInputMode() {
        mAlarmTimeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAlarmTimeControl(boolean flag) {
        mAlarmTimeContainer.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPresenter(@NonNull ManageAlarmContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void onNextStep() {
        ManageAlarmStep step = mPresenter.getCurrentStep();
        switch (step) {
            case TYPE:
                mPresenter.setAlarmTypeInfo(getAlarmType(), mDescription.getText().toString());
                break;
            case TIME:
                mPresenter.setAlarmTime("10", "15");
                break;
            case DAYS:
                AlarmsDataSource dataSource = Injection.provideAlarmsDataSource(this);
                dataSource.saveAlarm(new Alarm(AlarmType.ALARM, "desc 1"));
                break;
        }
    }

    private void onPreviousStep() {
        mPresenter.previousStep();
    }

    private AlarmType getAlarmType() {
        AlarmType alarmType = null;
        switch (mAlarmTypeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.alarm_type_radio_button:
                alarmType = AlarmType.ALARM;
                break;
            case R.id.reminder_type_radio_button:
                alarmType = AlarmType.REMINDER;
                break;

        }
        return alarmType;
    }
}
