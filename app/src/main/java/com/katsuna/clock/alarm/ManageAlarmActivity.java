package com.katsuna.clock.alarm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.util.Injection;
import com.katsuna.clock.validators.ValidationResult;

import java.util.Iterator;
import java.util.List;

public class ManageAlarmActivity extends AppCompatActivity implements ManageAlarmContract.View {

    public static final String EXTRA_ALARM_ID = "ALARM_ID";

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
    private RadioButton mReminderTypeRadioGroup;
    private RadioButton mAlarmTypeRadioButton;
    private View mAlarmTypeContainer;
    private View mAlarmTypeHandler;
    private View mAlarmTimeHandler;
    private View mAlarmTimeContainer;
    private View mAlarmDaysContainer;
    private View mAlarmDaysHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_alarm);

        init();

        String alarmId = getIntent().getStringExtra(EXTRA_ALARM_ID);

        // Create the presenter
        new ManageAlarmPresenter(alarmId,
                Injection.provideAlarmsDataSource(getApplicationContext()), this,
                Injection.provideAlarmValidator(),
                Injection.provideAlarmScheduler(getApplicationContext()));
    }

    private void init() {
        mAlarmTypeContainer = findViewById(R.id.alarm_type_container);
        mAlarmTypeHandler = findViewById(R.id.alarm_type_handler);
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
        mAlarmTypeRadioButton = findViewById(R.id.alarm_type_radio_button);
        mReminderTypeRadioGroup = findViewById(R.id.reminder_type_radio_button);
        mDescription = findViewById(R.id.alarm_description);

        mAlarmTimeHandler = findViewById(R.id.alarm_time_handler);
        mAlarmTimeContainer = findViewById(R.id.alarm_time_container);
        mAlarmTimeControl = findViewById(R.id.alarm_time_control);
        mHour = findViewById(R.id.hour);
        mMinute = findViewById(R.id.minute);

        mAlarmDaysHandler = findViewById(R.id.alarm_days_handler);
        mAlarmDaysContainer = findViewById(R.id.alarm_days_container);
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
        if (alarm.getAlarmType() == AlarmType.ALARM) {
            mAlarmTypeRadioButton.setChecked(true);
        } else {
            mReminderTypeRadioGroup.setChecked(true);
        }
        mDescription.setText(alarm.getDescription());
        mHour.setText(String.valueOf(alarm.getHour()));
        mMinute.setText(String.valueOf(alarm.getMinute()));
        mMondayToggle.setChecked(alarm.isMondayEnabled());
        mTuesdayToggle.setChecked(alarm.isTuesdayEnabled());
        mWednesdayToggle.setChecked(alarm.isWednesdayEnabled());
        mThursdayToggle.setChecked(alarm.isThursdayEnabled());
        mFridayToggle.setChecked(alarm.isFridayEnabled());
        mSaturdayToggle.setChecked(alarm.isSaturdayEnabled());
        mSundayToggle.setChecked(alarm.isSundayEnabled());
    }

    @Override
    public void showValidationResults(List<ValidationResult> results) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<ValidationResult> i = results.iterator(); i.hasNext(); ) {
            ValidationResult result = i.next();
            sb.append(getString(result.messageResId));
            if (i.hasNext()) {
                sb.append("\n");
            }
        }
        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
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
        if (flag) {
            mAlarmTypeContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_white));

            mAlarmTypeRadioButton.setVisibility(View.VISIBLE);
            mReminderTypeRadioGroup.setVisibility(View.VISIBLE);
            if (mReminderTypeRadioGroup.isChecked()) {
                mDescription.setVisibility(View.VISIBLE);
            }

            int elevation = getResources().getDimensionPixelSize(
                    R.dimen.common_selection_elevation);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = elevation;
            mAlarmTypeHandler.setElevation(elevation);
        } else {
            mAlarmTypeContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_grey50));

            mAlarmTypeRadioButton.setVisibility(mAlarmTypeRadioButton.isChecked() ? View.VISIBLE :
                    View.GONE);

            if (mReminderTypeRadioGroup.isChecked()) {
                mReminderTypeRadioGroup.setVisibility(View.VISIBLE);
                mDescription.setVisibility(View.VISIBLE);
            } else {
                mReminderTypeRadioGroup.setVisibility(View.GONE);
                mDescription.setVisibility(View.GONE);
            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = 0;
            mAlarmTypeHandler.setElevation(0);
        }

        mAlarmTypeRadioButton.setEnabled(flag);
        mReminderTypeRadioGroup.setEnabled(flag);
        mDescription.setEnabled(flag);
    }

    @Override
    public void showAlarmTimeControl(boolean flag) {
        if (flag) {
            mAlarmTimeContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_white));
            mAlarmTimeHandler.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_blueA700));

            int elevation = getResources().getDimensionPixelSize(
                    R.dimen.common_selection_elevation);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = elevation;
            mAlarmTimeHandler.setElevation(elevation);
        } else {
            mAlarmTimeContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_grey50));

            if (mPresenter.getCurrentStep() == ManageAlarmStep.TIME) {
                mAlarmTimeHandler.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.common_blueA700));
            } else {
                mAlarmTimeHandler.setBackgroundColor(ContextCompat.getColor(this,
                        R.color.common_solitude));
            }

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = 0;
            mAlarmTimeHandler.setElevation(0);

        }

        mAlarmTimeControl.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAlarmDaysControl(boolean flag) {
        if (flag) {
            mAlarmDaysContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_white));
            mAlarmDaysHandler.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_blueA700));

            int elevation = getResources().getDimensionPixelSize(
                    R.dimen.common_selection_elevation);

            mAlarmDaysHandler.setElevation(elevation);

        } else {
            mAlarmDaysContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_grey50));

            mAlarmDaysHandler.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_solitude));

            mAlarmDaysHandler.setElevation(0);
        }

        mAlarmDaysControl.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void adjustFabPositions(ManageAlarmStep step) {
        CoordinatorLayout.LayoutParams nextStepFabParams =
                (CoordinatorLayout.LayoutParams) mNextStepFab.getLayoutParams();
        CoordinatorLayout.LayoutParams previousStepFabParams =
                (CoordinatorLayout.LayoutParams) mPreviousStepFab.getLayoutParams();

        switch (step) {
            case TYPE:
                nextStepFabParams.anchorGravity = Gravity.TOP | Gravity.END;
                break;
            case TIME:
                previousStepFabParams.anchorGravity = Gravity.TOP | Gravity.END;
                nextStepFabParams.setAnchorId(R.id.alarm_time_container);
                nextStepFabParams.anchorGravity = Gravity.BOTTOM | Gravity.END;
                break;
            case DAYS:
                previousStepFabParams.anchorGravity = Gravity.BOTTOM | Gravity.END;
                nextStepFabParams.setAnchorId(R.id.alarm_days_container);
                nextStepFabParams.anchorGravity = Gravity.BOTTOM | Gravity.END;
                break;
        }
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
