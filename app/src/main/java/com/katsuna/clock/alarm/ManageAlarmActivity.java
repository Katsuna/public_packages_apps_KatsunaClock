package com.katsuna.clock.alarm;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.util.Injection;
import com.katsuna.clock.validators.ValidationResult;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.ColorAdjusterV2;
import com.katsuna.commons.utils.ColorCalcV2;

import java.util.Iterator;
import java.util.List;

public class ManageAlarmActivity extends KatsunaActivity implements ManageAlarmContract.View {

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
    private int mPrimaryColor2;
    private int mSecondaryColor2;
    private TextView mAlarmTimeTitle;
    private int mBlack34Color;
    private int mBlack58Color;
    private int mWhiteColor;
    private TextView mAlarmDaysTitle;
    private TextView mAlarmTypeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_alarm);

        init();

        long alarmId = getIntent().getLongExtra(EXTRA_ALARM_ID, 0);

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
        mAlarmTypeTitle = findViewById(R.id.alarm_type_text);
        mAlarmTimeTitle = findViewById(R.id.alarm_time_text);
        mAlarmDaysTitle = findViewById(R.id.alarm_days_text);

        mHour = findViewById(R.id.hour);
        mMinute = findViewById(R.id.minute);

        mAlarmDaysHandler = findViewById(R.id.alarm_days_handler);
        mAlarmDaysContainer = findViewById(R.id.alarm_days_container);
        mAlarmDaysControl = findViewById(R.id.alarm_days_radio_group);

        CompoundButton.OnCheckedChangeListener daysOnCheckedChangeListener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            buttonView.setTextColor(mPrimaryColor2);
                        } else {
                            buttonView.setTextColor(mBlack58Color);
                        }
                    }
                };

        mMondayToggle = findViewById(R.id.monday_tb);
        mMondayToggle.setOnCheckedChangeListener(daysOnCheckedChangeListener);
        mTuesdayToggle = findViewById(R.id.tuesday_tb);
        mTuesdayToggle.setOnCheckedChangeListener(daysOnCheckedChangeListener);
        mWednesdayToggle = findViewById(R.id.wednesday_tb);
        mWednesdayToggle.setOnCheckedChangeListener(daysOnCheckedChangeListener);
        mThursdayToggle = findViewById(R.id.thursday_tb);
        mThursdayToggle.setOnCheckedChangeListener(daysOnCheckedChangeListener);
        mFridayToggle = findViewById(R.id.friday_tb);
        mFridayToggle.setOnCheckedChangeListener(daysOnCheckedChangeListener);
        mSaturdayToggle = findViewById(R.id.saturday_tb);
        mSaturdayToggle.setOnCheckedChangeListener(daysOnCheckedChangeListener);
        mSundayToggle = findViewById(R.id.sunday_tb);
        mSundayToggle.setOnCheckedChangeListener(daysOnCheckedChangeListener);

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

        initToolbar(R.drawable.common_ic_close_black54_24dp);

        // static colors init
        mBlack34Color = ContextCompat.getColor(this, R.color.common_black34);
        mBlack58Color = ContextCompat.getColor(this, R.color.common_black58);
        mWhiteColor = ContextCompat.getColor(this, R.color.common_white);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
        adjustProfiles();
    }

    private void adjustProfiles() {
        UserProfile userProfile = mUserProfileContainer.getActiveUserProfile();

        mPrimaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_2,
                userProfile.colorProfile);
        mSecondaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.SECONDARY_COLOR_2,
                userProfile.colorProfile);

        adjustSteps();
    }

    private void adjustSteps() {
        adjustTypeStep();
        adjustTimeStep();
        adjustDaysStep();
        adjustFloatingButtons();
    }

    private void adjustTypeStep() {
        mAlarmTypeHandler.setBackgroundColor(mPrimaryColor2);
        int colorToApply;
        if (mPresenter.getCurrentStep() == ManageAlarmStep.TYPE) {
            colorToApply = mPrimaryColor2;
        } else {
            colorToApply = mBlack58Color;
        }
        mAlarmTypeTitle.setTextColor(colorToApply);
        ColorAdjusterV2.setTextViewDrawableColor(mAlarmTypeTitle, colorToApply);
        ColorAdjusterV2.setTextViewDrawableColor(mAlarmTypeRadioButton, colorToApply);
        ColorAdjusterV2.setTextViewDrawableColor(mReminderTypeRadioGroup, colorToApply);
    }

    private void adjustTimeStep() {
        switch (getTimeStepStatus()) {
            case NOT_SET:
                mAlarmTimeHandler.setBackgroundColor(mSecondaryColor2);
                mAlarmTimeTitle.setTextColor(mBlack34Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmTimeTitle, mBlack34Color);
                break;
            case ACTIVE:
                mAlarmTimeHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmTimeTitle.setTextColor(mPrimaryColor2);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmTimeTitle, mPrimaryColor2);
                break;
            case SET:
                mAlarmTimeHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmTimeTitle.setTextColor(mBlack58Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmTimeTitle, mBlack58Color);
                break;
        }
    }

    private void adjustDaysStep() {
        int toggleColor = 0;
        switch (getDaysStepStatus()) {
            case NOT_SET:
                mAlarmDaysHandler.setBackgroundColor(mSecondaryColor2);
                mAlarmDaysTitle.setTextColor(mBlack34Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmDaysTitle, mBlack34Color);
                toggleColor = mBlack34Color;
                break;
            case ACTIVE:
                mAlarmDaysHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmDaysTitle.setTextColor(mPrimaryColor2);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmDaysTitle, mPrimaryColor2);
                toggleColor = mPrimaryColor2;
                break;
            case SET:
                mAlarmDaysHandler.setBackgroundColor(mBlack58Color);
                mAlarmDaysTitle.setTextColor(mBlack58Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmDaysTitle, mBlack58Color);
                toggleColor = mBlack58Color;
                break;
        }

        ColorAdjusterV2.setTextViewDrawableColor(mMondayToggle, toggleColor);
        ColorAdjusterV2.setTextViewDrawableColor(mTuesdayToggle, toggleColor);
        ColorAdjusterV2.setTextViewDrawableColor(mWednesdayToggle, toggleColor);
        ColorAdjusterV2.setTextViewDrawableColor(mThursdayToggle, toggleColor);
        ColorAdjusterV2.setTextViewDrawableColor(mFridayToggle, toggleColor);
        ColorAdjusterV2.setTextViewDrawableColor(mSaturdayToggle, toggleColor);
        ColorAdjusterV2.setTextViewDrawableColor(mSundayToggle, toggleColor);
    }

    private void adjustFloatingButtons() {
        if (mPresenter.getCurrentStep() == ManageAlarmStep.DAYS) {
            mNextStepFab.setBackgroundTintList(ColorStateList.valueOf(mPrimaryColor2));
            mNextStepFab.setImageTintList(ColorStateList.valueOf(mWhiteColor));
        } else {
            mNextStepFab.setBackgroundTintList(ColorStateList.valueOf(mWhiteColor));
            mNextStepFab.setImageTintList(ColorStateList.valueOf(mPrimaryColor2));
        }
        mPreviousStepFab.setImageTintList(ColorStateList.valueOf(mPrimaryColor2));
    }

    private StepStatus getTimeStepStatus() {
        ManageAlarmStep manageAlarmStep = mPresenter.getCurrentStep();
        switch (manageAlarmStep) {
            case TYPE:
                return StepStatus.NOT_SET;
            case TIME:
                return StepStatus.ACTIVE;
            case DAYS:
                return StepStatus.SET;
            default:
                throw new RuntimeException("manageAlarmStep not found");
        }
    }

    private StepStatus getDaysStepStatus() {
        ManageAlarmStep manageAlarmStep = mPresenter.getCurrentStep();
        switch (manageAlarmStep) {
            case TYPE:
                return StepStatus.NOT_SET;
            case TIME:
                return StepStatus.NOT_SET;
            case DAYS:
                return StepStatus.ACTIVE;
            default:
                throw new RuntimeException("manageAlarmStep not found");
        }
    }

    @Override
    protected void showPopup(boolean flag) {
        // no op
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

        adjustSteps();
    }

    @Override
    public void showAlarmTimeControl(boolean flag) {
        if (flag) {
            mAlarmTimeContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_white));

            int elevation = getResources().getDimensionPixelSize(
                    R.dimen.common_selection_elevation);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = elevation;
            mAlarmTimeHandler.setElevation(elevation);
        } else {
            mAlarmTimeContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_grey50));

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = 0;
            mAlarmTimeHandler.setElevation(0);

        }

        adjustSteps();

        mAlarmTimeControl.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAlarmDaysControl(boolean flag) {
        if (flag) {
            mAlarmDaysContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_white));

            int elevation = getResources().getDimensionPixelSize(
                    R.dimen.common_selection_elevation);

            mAlarmDaysHandler.setElevation(elevation);

        } else {
            mAlarmDaysContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_grey50));

            mAlarmDaysHandler.setElevation(0);
        }

        adjustSteps();

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

    private enum StepStatus {
        ACTIVE, SET, NOT_SET
    }
}
