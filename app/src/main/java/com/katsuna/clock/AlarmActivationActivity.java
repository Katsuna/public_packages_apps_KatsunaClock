package com.katsuna.clock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStateManager;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.source.AlarmsDataSource;
import com.katsuna.clock.services.utils.AlarmsScheduler;
import com.katsuna.clock.services.utils.IAlarmsScheduler;
import com.katsuna.clock.util.Injection;

import java.util.Objects;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        enableLaunchingWhenLocked();
        hideActionBar();
        hideNavigationBar();

        setContentView(R.layout.activation);

        Long alarmId = getAlarmId();

        mAlarmsDataSource = Injection.provideAlarmsDataSource(this);
        mAlarmsScheduler = Injection.provideAlarmScheduler(this);

        //noinspection ConstantConditions
        mAlarmsDataSource.getAlarm(alarmId, new AlarmsDataSource.GetAlarmCallback() {
            @Override
            public void onAlarmLoaded(Alarm alarm) {
                mAlarm = alarm;
                init();
                soundTheAlarm();
            }

            @Override
            public void onDataNotAvailable() {
                // TODO
            }
        });

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

        mSnoozeButton = findViewById(R.id.snooze_button);
        mSnoozeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snoozeAlarm(SNOOZE_DELAY);
            }
        });

        mDismissButton = findViewById(R.id.dismiss_button);
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
            LogUtils.i(TAG, "onReceive rescheduling alarm: " + mAlarm);
            mAlarmsScheduler.reschedule(mAlarm);
        } else {
            // deactivate
            LogUtils.i(TAG, "onReceive deactivating alarm: " + mAlarm);
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
        AlarmKlaxon.start(this);
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
        LogUtils.d(TAG, "onStop called: handled=" + handled);
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

    private void hideActionBar() {
        // hide action bar
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
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