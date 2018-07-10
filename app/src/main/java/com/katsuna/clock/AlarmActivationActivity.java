package com.katsuna.clock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStateManager;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.Injection;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKeyV2;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalcV2;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.Shape;

public class AlarmActivationActivity extends AppCompatActivity {

    private final static String TAG = AlarmActivationActivity.class.getSimpleName();
    // 3 minutes default snooze
    private final static int SNOOZE_DELAY = 3 * 60;

    private Button mSnoozeButton;
    private Button mDismissButton;
    private IAlarmsScheduler mAlarmsScheduler;
    private AlarmsDataSource mAlarmsDataSource;
    private Alarm mAlarm;
    private boolean handled = false;
    private boolean mFocusDuringOnPause;
    private TextView mDescription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.d("%s onCreate", TAG);

        super.onCreate(savedInstanceState);

        enableLaunchingWhenLocked();
        hideNavigationBar();

        setContentView(R.layout.activation);

        mSnoozeButton = findViewById(R.id.snooze_button);
        mDismissButton = findViewById(R.id.dismiss_button);
        mDescription = findViewById(R.id.alarm_description);

        Long alarmId = getAlarmId();

        mAlarmsDataSource = Injection.provideAlarmsDataSource(this);
        mAlarmsScheduler = Injection.provideAlarmScheduler(this);

        //noinspection ConstantConditions
        mAlarmsDataSource.getAlarm(alarmId, new AlarmsDataSource.GetAlarmCallback() {
            @Override
            public void onAlarmLoaded(Alarm alarm) {
                mAlarm = alarm;
                if (mAlarm.getAlarmType() == AlarmType.REMINDER) {
                    mDescription.setText(mAlarm.getDescription());
                }

                init();
                soundTheAlarm();
            }

            @Override
            public void onDataNotAvailable() {
                // TODO
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        adjustProfiles();
    }

    private void adjustProfiles() {
        UserProfileContainer userProfileContainer = ProfileReader.getKatsunaUserProfile(this);
        UserProfile userProfile = userProfileContainer.getActiveUserProfile();

        // color adjustments
        int primaryColor1 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_1,
                userProfile.colorProfile);
        int white = ContextCompat.getColor(this, R.color.common_white);

        if (userProfile.colorProfile == ColorProfile.CONTRAST) {
            int grey300 = ContextCompat.getColor(this, R.color.common_grey300);
            getWindow().getDecorView().setBackgroundColor(grey300);
        } else {
            getWindow().getDecorView().setBackgroundColor(primaryColor1);
        }

        int primaryColor2 = ColorCalcV2.getColor(this, ColorProfileKeyV2.PRIMARY_COLOR_2,
                userProfile.colorProfile);
        float radius = getResources().getDimension(R.dimen.common_8dp);
        Shape.setRoundedBackground(mDismissButton, primaryColor2, radius);
        Shape.setRoundedBackground(mSnoozeButton, white, radius);
    }

    private Long getAlarmId() {
        Long alarmId = null;
        Intent i = getIntent();
        if (i.getExtras() != null) {
            alarmId = i.getExtras().getLong(AlarmsScheduler.ALARM_ID);
        }
        return alarmId;
    }

    private void init() {
        mAlarmsScheduler = Injection.provideAlarmScheduler(this);

        mSnoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snoozeAlarm(SNOOZE_DELAY);
            }
        });
        mDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAlarm();
            }
        });
    }

    private void dismissAlarm() {
        stopAlarm();

        if (mAlarm.isRecurring()) {
            // reshedule
            LogUtils.i("%s onReceive rescheduling alarm: %s", TAG, mAlarm);
            mAlarmsScheduler.reschedule(mAlarm);
        } else {
            // deactivate
            LogUtils.i("%s onReceive deactivating alarm: %s", TAG, mAlarm);
            mAlarm.setAlarmStatus(AlarmStatus.INACTIVE);
            mAlarmsDataSource.saveAlarm(mAlarm);
        }
        handled = true;

        AlarmStateManager.getInstance().removeAlarm(mAlarm);
        finish();
    }

    private void snoozeAlarm(long delay) {
        AlarmStateManager.getInstance().removeAlarm(mAlarm);
        stopAlarm();
        mAlarmsScheduler.snooze(mAlarm, delay);
        handled = true;
        finish();
    }

    private void soundTheAlarm() {
        AlarmKlaxon.start(this, mAlarm);
    }


    private void stopAlarm() {
        AlarmKlaxon.stop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFocusDuringOnPause = hasWindowFocus();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d("%s onStop called: handled= %s", TAG, handled);
        if (mFocusDuringOnPause) {
            if (!handled) {
                snoozeAlarm(SNOOZE_DELAY);
            }
        }
        /* else {
                // activity was started when screen was off / screen was on with keygaurd displayed
                // see https://stackoverflow.com/a/25474853
           } */
    }

    private void enableLaunchingWhenLocked() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
    }

    private void hideNavigationBar() {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
    }
}