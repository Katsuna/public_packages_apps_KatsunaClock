package com.katsuna.clock.alarm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.util.Injection;
import com.katsuna.clock.validators.ValidationResult;

import java.util.List;

public class ManageAlarmActivity extends AppCompatActivity implements ManageAlarmContract.View {

    private ManageAlarmContract.Presenter mPresenter;
    private FloatingActionButton mPreviousStepFab;
    private FloatingActionButton mNextStepFab;
    private View mAlarmDaysControl;
    private RadioGroup mAlarmTypeRadioGroup;
    private EditText mDescription;
    private EditText mHour;
    private EditText mMinute;
    private View mAlarmTimeControl;
    private ToggleButton mMondayToggle;
    private ToggleButton mTuesdayToggle;
    private ToggleButton mWednesdayToggle;
    private ToggleButton mThursdayToggle;
    private ToggleButton mFridayToggle;
    private ToggleButton mSaturdayToggle;
    private ToggleButton mSundayToggle;

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

        mAlarmTimeControl = findViewById(R.id.alarm_time_control);
        mHour = findViewById(R.id.hour);
        mMinute = findViewById(R.id.minute);

        mAlarmDaysControl = findViewById(R.id.alarm_days_radio_group);
        mMondayToggle = findViewById(R.id.monday_tb);
        mTuesdayToggle = findViewById(R.id.tuesday_tb);
        mWednesdayToggle = findViewById(R.id.wednesday_tb);
        mThursdayToggle = findViewById(R.id.thursday_tb);
        mFridayToggle = findViewById(R.id.friday_tb);
        mSaturdayToggle = findViewById(R.id.saturday_tb);
        mSundayToggle = findViewById(R.id.sunday_tb);

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
        finish();
    }

    @Override
    public void setTime(String hour, String minute) {
        mHour.setText(hour);
        mMinute.setText(minute);
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
        mAlarmTypeRadioGroup.setVisibility(View.GONE);
    }

    @Override
    public void showAlarmTimeControlInputMode() {
        mAlarmTimeControl.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAlarmTimeControlUnfocused() {
        // TODO
        mAlarmTimeControl.setVisibility(View.GONE);
    }

    @Override
    public void showAlarmTimeControl(boolean flag) {
        mAlarmTimeControl.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAlarmDaysControl(boolean flag) {
        mAlarmDaysControl.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPresenter(@NonNull ManageAlarmContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void onNextStep() {
        ManageAlarmStep step = mPresenter.getCurrentStep();
        switch (step) {
            case TYPE:
                mPresenter.validateAlarmTypeInfo(getAlarmType(), mDescription.getText().toString());
                break;
            case TIME:
                mPresenter.validateAlarmTime(mHour.getText().toString(),
                        mMinute.getText().toString());
                break;
            case DAYS:
                mPresenter.saveAlarm(getAlarmType(), mDescription.getText().toString(),
                        mHour.getText().toString(), mMinute.getText().toString(),
                        mMondayToggle.isChecked(), mTuesdayToggle.isChecked(),
                        mWednesdayToggle.isChecked(), mThursdayToggle.isChecked(),
                        mFridayToggle.isChecked(), mSaturdayToggle.isChecked(),
                        mSundayToggle.isChecked());
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
