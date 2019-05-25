package com.katsuna.clock.alarm;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.katsuna.clock.R;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.formatters.DaysFormatter;
import com.katsuna.clock.formatters.TodayFormatter;
import com.katsuna.clock.util.Injection;
import com.katsuna.clock.validators.ValidationResult;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.ColorAdjusterV2;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.KeyboardUtils;
import com.katsuna.commons.utils.TypefaceUtils;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Iterator;
import java.util.List;

import static android.media.RingtoneManager.TYPE_ALARM;

public class ManageAlarmActivity extends KatsunaActivity implements ManageAlarmContract.View {

    public static final String EXTRA_ALARM_ID = "ALARM_ID";
    public static final String EXTRA_ALARM_TYPE = "ALARM_TYPE";
    public static final int RINGTONE_REQUEST_CODE = 1;

    private ManageAlarmContract.Presenter mPresenter;
    private FloatingActionButton mPreviousStepFab;
    private FloatingActionButton mNextStepFab;
    private View mAlarmDaysControl;
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
    private ImageView mAddHourButton;
    private ImageView mSubtractHourButton;
    private ImageView mAddMinuteButton;
    private ImageView mSubtractMinuteButton;
    private View mAlarmOptionsHandler;
    private View mAlarmOptionsContainer;
    private View mAlarmOptionsControl;
    private TextView mAlarmOptionsTitle;
    private TextView mRingtoneOption;
    private Uri mRingtoneUri;
    private ToggleButton mVibrateOption;
    private boolean mVibrate;
    private TextView mTimeSelected;
    private TextView mDaysSelected;
    private UserProfile mUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initActivity(UserProfile userProfile) {
        if (userProfile.isRightHanded) {
            setContentView(R.layout.activity_manage_alarm);
        } else {
            setContentView(R.layout.activity_manage_alarm_lh);
        }

        init();

        long alarmId = getIntent().getLongExtra(EXTRA_ALARM_ID, 0);
        AlarmType alarmType = (AlarmType) getIntent().getSerializableExtra(EXTRA_ALARM_TYPE);

        // Create the presenter
        new ManageAlarmPresenter(alarmId, alarmType,
                Injection.provideAlarmsDataSource(getApplicationContext()), this,
                Injection.provideAlarmValidator(),
                Injection.provideAlarmScheduler(getApplicationContext()));

        mPresenter.start();
    }

    private void adjustVibrateOption(boolean enabled) {
        mVibrateOption.setChecked(enabled);
    }

    private void init() {
        mAlarmTypeContainer = findViewById(R.id.alarm_type_container);
        mAlarmTypeHandler = findViewById(R.id.alarm_type_handler);
        mDescription = findViewById(R.id.alarm_description);
        mRingtoneOption = findViewById(R.id.alarm_ringtone_option);
        mRingtoneOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickRingtone();
            }
        });

        mVibrateOption = findViewById(R.id.alarm_vibrate_option);
        mVibrateOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrate = !mVibrate;
                adjustVibrateOption(mVibrate);
            }
        });

        mAlarmTimeHandler = findViewById(R.id.alarm_time_handler);
        mAlarmTimeContainer = findViewById(R.id.alarm_time_container);
        mAlarmTimeControl = findViewById(R.id.alarm_time_control);
        mTimeSelected = findViewById(R.id.time_selected);
        mAlarmTypeTitle = findViewById(R.id.alarm_type_text);
        mAlarmTimeTitle = findViewById(R.id.alarm_time_text);
        mAlarmDaysTitle = findViewById(R.id.alarm_days_text);
        mAlarmOptionsTitle = findViewById(R.id.alarm_options_text);

        mHour = findViewById(R.id.hour);
        mHour.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mHour.setTextColor(mPrimaryColor2);
                } else {
                    mHour.setTextColor(mBlack58Color);
                }
            }
        });
        mHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String output = null;
                try {
                    int input = Integer.parseInt(s.toString());
                    if (input > 23) {
                        output = "23";
                    } else if (input < 0) {
                        output = "00";
                    }
                } catch (NumberFormatException ex) {
                    output = "00";
                }

                // limits overriden or NumberFormatException case
                if (output != null) {
                    mHour.setText(output);
                }
            }
        });

        mMinute = findViewById(R.id.minute);
        mMinute.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mMinute.setTextColor(mPrimaryColor2);
                } else {
                    mMinute.setTextColor(mBlack58Color);
                }
            }
        });
        mMinute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String output = null;
                try {
                    int input = Integer.parseInt(s.toString());
                    if (input > 59) {
                        output = "59";
                    } else if (input < 0) {
                        output = "00";
                    }
                } catch (NumberFormatException ex) {
                    output = "00";
                }

                // limits overriden or NumberFormatException case
                if (output != null) {
                    mMinute.setText(output);
                }
            }
        });

        mAddHourButton = findViewById(R.id.add_hour_button);
        mAddHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addHours(mHour.getText().toString(), 1);
            }
        });
        mSubtractHourButton = findViewById(R.id.subtract_hour_button);
        mSubtractHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addHours(mHour.getText().toString(), -1);
            }
        });
        mAddMinuteButton = findViewById(R.id.add_minute_button);
        mAddMinuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addMinutes(mMinute.getText().toString(), 1);
            }
        });
        mSubtractMinuteButton = findViewById(R.id.subtract_minute_button);
        mSubtractMinuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addMinutes(mMinute.getText().toString(), -1);
            }
        });

        mAlarmDaysHandler = findViewById(R.id.alarm_days_handler);
        mAlarmDaysContainer = findViewById(R.id.alarm_days_container);
        mAlarmDaysControl = findViewById(R.id.alarm_days_radio_group);
        mDaysSelected = findViewById(R.id.days_selected);

        mAlarmOptionsHandler = findViewById(R.id.alarm_options_handler);
        mAlarmOptionsContainer = findViewById(R.id.alarm_options_container);
        mAlarmOptionsControl = findViewById(R.id.alarm_options_group);

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
        mVibrateOption.setOnCheckedChangeListener(daysOnCheckedChangeListener);
    }

    private void pickRingtone() {
        //List<KatsunaRingtone> ringtones = RingtoneUtils.getAllRingtones(this);

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mRingtoneUri);
        startActivityForResult(intent, RINGTONE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RINGTONE_REQUEST_CODE) {
                mRingtoneUri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                setRingtoneTitle(mRingtoneUri);
            }
        }
    }

    private void setRingtone(Alarm alarm) {
        mRingtoneUri = Uri.parse(alarm.getRingtone());
        setRingtoneTitle(mRingtoneUri);
    }

    private void setRingtoneTitle(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
        String title = getString(R.string.ringtone, ringtone.getTitle(this));
        mRingtoneOption.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        UserProfile userProfile = getActiveUserProfile();

        // first time init check
        if (mUserProfile == null) {
            mUserProfile = userProfile;
            initActivity(userProfile);
            adjustProfiles(userProfile);
        } else if (!mUserProfile.equals(userProfile)){
            // check if we need to change layouts
            if (mUserProfile.isRightHanded != userProfile.isRightHanded) {
                mUserProfile = userProfile;
                initActivity(userProfile);
            }
            adjustProfiles(userProfile);
        }
    }

    private void adjustProfiles(UserProfile userProfile) {
        mPrimaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_2,
                userProfile.colorProfile);
        mSecondaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.SECONDARY_COLOR_2,
                userProfile.colorProfile);

        adjustTimeControls();
        adjustSteps();

        // adjust left hand profile for today
        addToday(mMondayToggle, DayOfWeek.MONDAY, userProfile);
        addToday(mTuesdayToggle, DayOfWeek.TUESDAY, userProfile);
        addToday(mWednesdayToggle, DayOfWeek.WEDNESDAY, userProfile);
        addToday(mThursdayToggle, DayOfWeek.THURSDAY, userProfile);
        addToday(mFridayToggle, DayOfWeek.FRIDAY, userProfile);
        addToday(mSaturdayToggle, DayOfWeek.SATURDAY, userProfile);
        addToday(mSundayToggle, DayOfWeek.SUNDAY, userProfile);
    }

    private void adjustTimeControls() {
        GradientDrawable gD = new GradientDrawable();
        gD.setColor(mPrimaryColor2);
        gD.setShape(GradientDrawable.OVAL);
        mAddHourButton.setBackground(gD);
        mAddMinuteButton.setBackground(gD);
        mSubtractHourButton.setBackground(gD);
        mSubtractMinuteButton.setBackground(gD);


        final GradientDrawable shape = new GradientDrawable();
        float radius = getResources().getDimension(R.dimen.time_controls_radius);
        shape.setCornerRadius(radius);
        shape.setColor(mSecondaryColor2);

        mHour.post(new Runnable() {
            @Override
            public void run() {

                mHour.setBackground(shape);
            }
        });

        mMinute.post(new Runnable() {
            @Override
            public void run() {
                mMinute.setBackground(shape);
            }
        });
    }

    private void adjustSteps() {
        adjustTypeStep();
        adjustTimeStep();
        adjustDaysStep();
        adjustOptionsStep();
        adjustFloatingButtons();
    }

    private void adjustTypeStep() {
        mAlarmTypeHandler.setBackgroundColor(mPrimaryColor2);
        int colorToApply;
        if (mPresenter.getCurrentStep() == ManageAlarmStep.DESCRIPTION) {
            colorToApply = mPrimaryColor2;
        } else {
            colorToApply = mBlack58Color;
        }
        mAlarmTypeTitle.setTextColor(colorToApply);
        mDescription.setBackgroundTintList(ColorStateList.valueOf(colorToApply));
        ColorAdjusterV2.setTextViewDrawableColor(mAlarmTypeTitle, colorToApply);
    }

    private void adjustTimeStep() {
        switch (getTimeStepStatus()) {
            case NOT_SET:
                mAlarmTimeHandler.setBackgroundColor(mSecondaryColor2);
                mAlarmTimeTitle.setTextColor(mBlack34Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmTimeTitle, mBlack34Color);
                mTimeSelected.setVisibility(View.GONE);
                if (getAlarmType() == AlarmType.ALARM) {
                    mAlarmTimeTitle.setText(R.string.set_alarm_time);
                } else {
                    mAlarmTimeTitle.setText(R.string.set_reminder_time);
                }
                break;
            case ACTIVE:
                mAlarmTimeHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmTimeTitle.setTextColor(mPrimaryColor2);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmTimeTitle, mPrimaryColor2);
                mTimeSelected.setVisibility(View.GONE);
                if (getAlarmType() == AlarmType.ALARM) {
                    mAlarmTimeTitle.setText(R.string.set_alarm_time);
                } else {
                    mAlarmTimeTitle.setText(R.string.set_reminder_time);
                }
                break;
            case SET:
                mAlarmTimeHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmTimeTitle.setTextColor(mBlack58Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmTimeTitle, mBlack58Color);
                mTimeSelected.setVisibility(View.VISIBLE);
                String time = getString(R.string.time_display, mHour.getText(), mMinute.getText());
                mTimeSelected.setText(time);
                if (getAlarmType() == AlarmType.ALARM) {
                    mAlarmTimeTitle.setText(R.string.alarm_time_set);
                } else {
                    mAlarmTimeTitle.setText(R.string.reminder_time_set);
                }
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
                mAlarmDaysControl.setVisibility(View.GONE);
                mDaysSelected.setVisibility(View.GONE);
                break;
            case ACTIVE:
                mAlarmDaysHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmDaysTitle.setTextColor(mPrimaryColor2);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmDaysTitle, mPrimaryColor2);
                toggleColor = mPrimaryColor2;
                mAlarmDaysControl.setVisibility(View.VISIBLE);
                mDaysSelected.setVisibility(View.GONE);
                break;
            case SET:
                mAlarmDaysHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmDaysTitle.setTextColor(mBlack58Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmDaysTitle, mBlack58Color);
                toggleColor = mBlack58Color;
                mAlarmDaysControl.setVisibility(View.GONE);
                mDaysSelected.setText(getDaysDescription());
                mDaysSelected.setVisibility(View.VISIBLE);
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

    private String getDaysDescription() {
        return DaysFormatter.getDays(this, mMondayToggle.isChecked(), mTuesdayToggle.isChecked(),
                mWednesdayToggle.isChecked(), mThursdayToggle.isChecked(), mFridayToggle.isChecked(),
                mSaturdayToggle.isChecked(), mSundayToggle.isChecked());
    }

    private void adjustOptionsStep() {
        int toggleColor = 0;
        switch (getOptionsStepStatus()) {
            case NOT_SET:
                mAlarmOptionsHandler.setBackgroundColor(mSecondaryColor2);
                mAlarmOptionsTitle.setTextColor(mBlack34Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmOptionsTitle, mBlack34Color);
                toggleColor = mBlack34Color;
                break;
            case ACTIVE:
                mAlarmOptionsHandler.setBackgroundColor(mPrimaryColor2);
                mAlarmOptionsTitle.setTextColor(mPrimaryColor2);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmOptionsTitle, mPrimaryColor2);
                toggleColor = mPrimaryColor2;
                break;
            case SET:
                mAlarmOptionsHandler.setBackgroundColor(mBlack58Color);
                mAlarmOptionsTitle.setTextColor(mBlack58Color);
                ColorAdjusterV2.setTextViewDrawableColor(mAlarmOptionsTitle, mBlack58Color);
                toggleColor = mBlack58Color;
                break;
        }

        ColorAdjusterV2.setTextViewDrawableColor(mVibrateOption, toggleColor);
        if (mVibrateOption.isChecked()) {
            mVibrateOption.setTextColor(mPrimaryColor2);
        } else {
            mVibrateOption.setTextColor(mBlack58Color);
        }
    }

    private void adjustFloatingButtons() {
        if (mPresenter.getCurrentStep() == ManageAlarmStep.OPTIONS) {
            mNextStepFab.setImageResource(R.drawable.ic_check_black_24dp);
            mNextStepFab.setBackgroundTintList(ColorStateList.valueOf(mPrimaryColor2));
            mNextStepFab.setImageTintList(ColorStateList.valueOf(mWhiteColor));
        } else {
            mNextStepFab.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
            mNextStepFab.setBackgroundTintList(ColorStateList.valueOf(mWhiteColor));
            mNextStepFab.setImageTintList(ColorStateList.valueOf(mPrimaryColor2));
        }
        mPreviousStepFab.setImageTintList(ColorStateList.valueOf(mPrimaryColor2));
    }

    private StepStatus getTimeStepStatus() {
        ManageAlarmStep manageAlarmStep = mPresenter.getCurrentStep();
        switch (manageAlarmStep) {
            case DESCRIPTION:
                return StepStatus.NOT_SET;
            case TIME:
                return StepStatus.ACTIVE;
            case DAYS:
                return StepStatus.SET;
            case OPTIONS:
                return StepStatus.SET;
            default:
                throw new RuntimeException("manageAlarmStep not found");
        }
    }

    private StepStatus getDaysStepStatus() {
        ManageAlarmStep manageAlarmStep = mPresenter.getCurrentStep();
        switch (manageAlarmStep) {
            case DESCRIPTION:
                return StepStatus.NOT_SET;
            case TIME:
                return StepStatus.NOT_SET;
            case DAYS:
                return StepStatus.ACTIVE;
            case OPTIONS:
                return StepStatus.SET;
            default:
                throw new RuntimeException("manageAlarmStep not found");
        }
    }

    private StepStatus getOptionsStepStatus() {
        ManageAlarmStep manageAlarmStep = mPresenter.getCurrentStep();
        switch (manageAlarmStep) {
            case DESCRIPTION:
                return StepStatus.NOT_SET;
            case TIME:
                return StepStatus.NOT_SET;
            case DAYS:
                return StepStatus.NOT_SET;
            case OPTIONS:
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
    public void showAlarmsList(Alarm alarm) {
        Intent i = new Intent();
        i.putExtra("alarm", alarm);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void setTime(String hour, String minute) {
        mHour.setText(hour);
        mMinute.setText(minute);
    }

    @Override
    public void setHour(String hour) {
        mHour.setText(hour);
    }

    @Override
    public void setMinute(String minute) {
        mMinute.setText(minute);
    }

    @Override
    public void loadAlarm(Alarm alarm) {
        mDescription.setText(alarm.getDescription());
        LocalTime alarmTime = LocalTime.of(alarm.getHour(), alarm.getMinute());
        mHour.setText(alarmTime.format(DateTimeFormatter.ofPattern("HH")));
        mMinute.setText(alarmTime.format(DateTimeFormatter.ofPattern("mm")));
        mMondayToggle.setChecked(alarm.isMondayEnabled());
        mTuesdayToggle.setChecked(alarm.isTuesdayEnabled());
        mWednesdayToggle.setChecked(alarm.isWednesdayEnabled());
        mThursdayToggle.setChecked(alarm.isThursdayEnabled());
        mFridayToggle.setChecked(alarm.isFridayEnabled());
        mSaturdayToggle.setChecked(alarm.isSaturdayEnabled());
        mSundayToggle.setChecked(alarm.isSundayEnabled());
        setRingtone(alarm);
        mVibrate = alarm.isVibrate();
        adjustVibrateOption(mVibrate);
    }

    private void addToday(ToggleButton dayToggle, DayOfWeek dayOfWeek, UserProfile userProfile) {
        if (TodayFormatter.isToday(dayOfWeek)) {
            String currentText = "";
            if (dayToggle == mMondayToggle) {
                currentText = getResources().getString(R.string.monday);
            } else if (dayToggle == mTuesdayToggle) {
                currentText = getResources().getString(R.string.tuesday);
            } else if (dayToggle == mWednesdayToggle) {
                currentText = getResources().getString(R.string.wednesday);
            } else if (dayToggle == mThursdayToggle) {
                currentText = getResources().getString(R.string.thursday);
            } else if (dayToggle == mFridayToggle) {
                currentText = getResources().getString(R.string.friday);
            } else if (dayToggle == mSaturdayToggle) {
                currentText = getResources().getString(R.string.saturday);
            } else if (dayToggle == mSundayToggle) {
                currentText = getResources().getString(R.string.sunday);
            }
            String todayText = getResources().getString(R.string.common_today);
            String todayWrapped = " (" + todayText + ") ";

            Typeface typefaceMedium = Typeface.create("sans-serif-medium", Typeface.NORMAL);
            Typeface typefaceLight = Typeface.create("sans-serif-light", Typeface.NORMAL);

            if (userProfile.isRightHanded) {
                String fullText = currentText + todayWrapped;
                TypefaceUtils.applyCustomTypefaceSpan(dayToggle, typefaceMedium, typefaceLight,
                        fullText, currentText.length()-1);
            } else {
                String fullText = todayWrapped + currentText;

                TypefaceUtils.applyCustomTypefaceSpan(dayToggle, typefaceLight, typefaceMedium,
                        fullText, todayWrapped.length()-1);
            }
        }
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
        if (flag) {
            mNextStepFab.show();
        } else {
            mNextStepFab.hide();
        }
    }

    @Override
    public void showPreviousStepFab(boolean flag) {
        if (flag) {
            mPreviousStepFab.show();
        } else {
            mPreviousStepFab.hide();
        }
    }

    @Override
    public void showDescriptionControl(boolean flag) {
        if (flag) {
            mAlarmTypeContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_white));

            mDescription.setVisibility(View.VISIBLE);

            int elevation = getResources().getDimensionPixelSize(
                    R.dimen.common_selection_elevation);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = elevation;
            mAlarmTypeHandler.setElevation(elevation);
        } else {
            mAlarmTypeContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_grey50));

            mDescription.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    mAlarmTypeHandler.getLayoutParams();
            layoutParams.bottomMargin = 0;
            mAlarmTypeHandler.setElevation(0);
        }

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
    public void showAlarmOptionsControl(boolean flag) {
        if (flag) {
            mAlarmOptionsContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.common_white));

            int elevation = getResources().getDimensionPixelSize(
                    R.dimen.common_selection_elevation);

            mAlarmOptionsHandler.setElevation(elevation);

        } else {
            mAlarmOptionsContainer.setBackgroundColor(ContextCompat.getColor(this,
                    R.color.common_grey50));

            mAlarmOptionsHandler.setElevation(0);
        }

        adjustSteps();

        mAlarmOptionsControl.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void adjustFabPositions(ManageAlarmStep step) {
        CoordinatorLayout.LayoutParams nextStepFabParams =
                (CoordinatorLayout.LayoutParams) mNextStepFab.getLayoutParams();
        CoordinatorLayout.LayoutParams previousStepFabParams =
                (CoordinatorLayout.LayoutParams) mPreviousStepFab.getLayoutParams();

        int horizontalGravity = mUserProfile.isRightHanded ? Gravity.END : Gravity.START;

        switch (step) {
            case DESCRIPTION:
                nextStepFabParams.anchorGravity = Gravity.TOP | horizontalGravity;
                break;
            case TIME:
                previousStepFabParams.anchorGravity = Gravity.TOP | horizontalGravity;
                previousStepFabParams.setAnchorId(R.id.alarm_time_container);
                nextStepFabParams.setAnchorId(R.id.alarm_time_container);
                nextStepFabParams.anchorGravity = Gravity.BOTTOM | horizontalGravity;
                break;
            case DAYS:
                previousStepFabParams.anchorGravity = Gravity.TOP | horizontalGravity;
                previousStepFabParams.setAnchorId(R.id.alarm_days_container);
                nextStepFabParams.setAnchorId(R.id.alarm_days_container);
                nextStepFabParams.anchorGravity = Gravity.BOTTOM | horizontalGravity;
                break;
            case OPTIONS:
                previousStepFabParams.anchorGravity = Gravity.TOP | horizontalGravity;
                previousStepFabParams.setAnchorId(R.id.alarm_options_container);
                nextStepFabParams.setAnchorId(R.id.alarm_options_container);
                nextStepFabParams.anchorGravity = Gravity.BOTTOM | horizontalGravity;
                break;
        }
    }

    @Override
    public void showDescriptionStep(boolean flag) {
        mAlarmTypeContainer.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setAlarmTimeTitle(int resId) {
        mAlarmTimeTitle.setText(resId);
    }

    @Override
    public void setDefaultRingtone() {
        mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, TYPE_ALARM);
        setRingtoneTitle(mRingtoneUri);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setDefaultVibrate() {
        mVibrate = true;
        adjustVibrateOption(mVibrate);
    }

    @Override
    public void hideKeyboard() {
        KeyboardUtils.hideKeyboard(this);
    }

    @Override
    public void setPresenter(@NonNull ManageAlarmContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void onNextStep() {
        ManageAlarmStep step = mPresenter.getCurrentStep();
        switch (step) {
            case DESCRIPTION:
                mPresenter.validateAlarmTypeInfo(getAlarmType(), mDescription.getText().toString());
                break;
            case TIME:
                mPresenter.validateAlarmTime(mHour.getText().toString(),
                        mMinute.getText().toString());
                break;
            case DAYS:
                mPresenter.showStep(ManageAlarmStep.OPTIONS);
                break;
            case OPTIONS:
                mPresenter.saveAlarm(mDescription.getText().toString(),
                        mHour.getText().toString(), mMinute.getText().toString(),
                        mMondayToggle.isChecked(), mTuesdayToggle.isChecked(),
                        mWednesdayToggle.isChecked(), mThursdayToggle.isChecked(),
                        mFridayToggle.isChecked(), mSaturdayToggle.isChecked(),
                        mSundayToggle.isChecked(), mRingtoneUri.toString(), mVibrate);
                break;
        }
    }

    private void onPreviousStep() {
        mPresenter.previousStep();
    }

    private AlarmType getAlarmType() {
        return mPresenter.getAlarmType();
    }

    private enum StepStatus {
        ACTIVE, SET, NOT_SET
    }
}
