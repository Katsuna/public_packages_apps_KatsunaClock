package com.katsuna.clock.alarms;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.katsuna.clock.LogUtils;
import com.katsuna.clock.R;
import com.katsuna.clock.alarm.ManageAlarmActivity;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.data.AlarmType;
import com.katsuna.clock.formatters.AlarmFormatter;
import com.katsuna.clock.info.InfoActivity;
import com.katsuna.clock.settings.SettingsActivity;
import com.katsuna.clock.util.Injection;
import com.katsuna.commons.controls.KatsunaNavigationView;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.IUserProfileProvider;
import com.katsuna.commons.utils.KatsunaAlertBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.katsuna.clock.alarm.ManageAlarmActivity.EXTRA_ALARM_TYPE;
import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;
import static com.katsuna.commons.utils.Constants.KATSUNA_TERMS_OF_USE;


/**
 * Display a list of {@link Alarm}s. User can choose to view all active and inactive alarms,
 * and create and edit alarms.
 */
public class AlarmsActivity extends KatsunaActivity implements AlarmsContract.View,
        IUserProfileProvider {

    private static final int ALARMS_MINIMIZE_THRESHOLD = 6;

    private static final int REQUEST_CODE_NEW_ALARM = 1;
    private static final int REQUEST_CODE_EDIT_ALARM = 2;

    private static final String TAG = AlarmsActivity.class.getSimpleName();
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
    private TextView mNoAlarmsText;
    private ListView mAlarmsList;
    private AlarmsAdapter mAlarmsAdapter;
    private DrawerLayout mDrawerLayout;
    private View mDateMinimized;
    private View mDateExpanded;
    private long lastScrollCheckTimestamp;
    private boolean mTimeMinimized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarms);

        init();

        // Create the presenter
        new AlarmsPresenter(Injection.provideAlarmsDataSource(getApplicationContext()), this,
                Injection.provideAlarmScheduler(getApplicationContext()));
    }

    private void init() {
        mNoAlarmsText = findViewById(R.id.no_alarms);
        mPopupButton2 = findViewById(R.id.create_reminder_button);
        mPopupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewReminder();
            }
        });

        mFab2 = findViewById(R.id.create_reminder_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewReminder();
            }
        });


        mPopupButton1 = findViewById(R.id.create_alarm_button);
        mPopupButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewAlarm();
            }
        });

        mFab1 = findViewById(R.id.create_alarm_fab);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewAlarm();
            }
        });

        mAlarmsAdapter = new AlarmsAdapter(new ArrayList<Alarm>(0), mItemListener, this);
        mDateMinimized = findViewById(R.id.date_minimized);
        mDateExpanded = findViewById(R.id.date_expanded_container);
        mAlarmsList = findViewById(R.id.alarms_list);
        mAlarmsList.setAdapter(mAlarmsAdapter);
        mAlarmsList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                long now = SystemClock.elapsedRealtime();
                // check in intervals of 500ms to avoid consequent calls.
                //if (lastScrollCheckTimestamp != 0 && lastScrollCheckTimestamp + 100 > now) return;

                int firstVisiblePosition = mAlarmsList.getFirstVisiblePosition();
                if (firstVisiblePosition > 1) {
                    if (!mTimeMinimized) {
                        minimizeDate(true);
                    }
                } else {
                    // check if top is reached
                    if (!mAlarmsList.canScrollVertically(-1)) {
                        if (mTimeMinimized) {
                            minimizeDate(false);
                        }
                    }
                }
            }
        });

        initToolbar();
        initDrawer();
    }

    private void minimizeDate(boolean flag) {
        // skip minimize function when there are few alarms
        if (mAlarmsList.getCount() < ALARMS_MINIMIZE_THRESHOLD) return;

        if (flag) {
            fadeView(mDateExpanded, false);
            fadeView(mDateMinimized, true);
        } else {
            fadeView(mDateExpanded, true);
            fadeView(mDateMinimized, false);
        }
        mTimeMinimized = flag;
        lastScrollCheckTimestamp = SystemClock.elapsedRealtime();
    }

    private void fadeView(View view, boolean show) {
        if (show) {
            // Prepare the View for the animation
            view.setAlpha(0.0f);
            view.setVisibility(View.VISIBLE);

            // Start the animation
            view.animate().alpha(1.0f).setListener(null);
        } else {
            // Prepare the View for the animation
            view.setAlpha(1.0f);

            // Start the animation
            view.animate().alpha(0.0f).setListener(null);
            view.setVisibility(View.GONE);
        }

    }

    private void initDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.common_navigation_drawer_open,
                R.string.common_navigation_drawer_close);
        assert mDrawerLayout != null;
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupDrawerLayout();
    }

    private void setupDrawerLayout() {
        KatsunaNavigationView mKatsunaNavigationView = findViewById(R.id.katsuna_navigation_view);
        mKatsunaNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            case R.id.drawer_settings:
                                startActivity(new Intent(AlarmsActivity.this,
                                        SettingsActivity.class));
                                break;
                            case R.id.drawer_info:
                                startActivity(new Intent(AlarmsActivity.this, InfoActivity.class));
                                break;
                            case R.id.drawer_privacy:
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(KATSUNA_PRIVACY_URL));
                                startActivity(browserIntent);
                                break;
                            case R.id.drawer_terms:
                                Intent termsIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(KATSUNA_TERMS_OF_USE));
                                startActivity(termsIntent);
                                break;
                        }

                        return true;
                    }
                });
        mKatsunaNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    protected void showPopup(boolean flag) {
        // no op
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
        mPopupButton1.setVisibility(View.GONE);
        mPopupButton2.setVisibility(View.GONE);
        LogUtils.d("%s alarms fetched: %s", TAG, alarms.size());
    }

    @Override
    public void showAddAlarm() {
        Intent i = new Intent(this, ManageAlarmActivity.class);
        i.putExtra(EXTRA_ALARM_TYPE, AlarmType.ALARM);
        startActivityForResult(i, REQUEST_CODE_NEW_ALARM);
    }

    @Override
    public void showAddReminder() {
        Intent i = new Intent(this, ManageAlarmActivity.class);
        i.putExtra(EXTRA_ALARM_TYPE, AlarmType.REMINDER);
        startActivityForResult(i, REQUEST_CODE_NEW_ALARM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_ALARM || requestCode == REQUEST_CODE_EDIT_ALARM) {
            if (resultCode == RESULT_OK) {
                Alarm alarm = data.getParcelableExtra("alarm");
                Log.e(TAG, "alarm set with id: " + alarm);
                showAlarmSetAlert(alarm);
            }
        }
    }

    @Override
    public void showAlarmDetailsUi(long alarmId) {
        Intent intent = new Intent(this, ManageAlarmActivity.class);
        intent.putExtra(ManageAlarmActivity.EXTRA_ALARM_ID, alarmId);
        startActivityForResult(intent, REQUEST_CODE_EDIT_ALARM);
    }

    private void showAlarmSetAlert(final Alarm alarm) {
        KatsunaAlertBuilder builder = new KatsunaAlertBuilder(this);
        String title = alarm.getAlarmType() == AlarmType.ALARM ? getString(R.string.alarm_set) :
                getString(R.string.reminder_set);
        builder.setTitle(title);

        // calc and set message
        AlarmFormatter alarmFormatter = new AlarmFormatter(this, alarm);
        builder.setMessage(alarmFormatter.getAlertMessage());
        builder.setView(R.layout.common_katsuna_alert);
        builder.setUserProfile(mUserProfileContainer.getActiveUserProfile());
        builder.setOkListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        String back = getString(R.string.back);
        builder.setCancelButtonText(back);
        builder.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlarmDetailsUi(alarm.getAlarmId());
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    @Override
    public void showNoAlarms() {
        mNoAlarmsText.setVisibility(View.VISIBLE);
        mFab1.setVisibility(View.VISIBLE);
        mFab2.setVisibility(View.VISIBLE);
        mPopupButton1.setVisibility(View.VISIBLE);
        mPopupButton2.setVisibility(View.VISIBLE);
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

    @Override
    public UserProfile getProfile() {
        return mUserProfileContainer.getActiveUserProfile();
    }
}
