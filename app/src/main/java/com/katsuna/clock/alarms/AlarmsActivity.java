package com.katsuna.clock.alarms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.katsuna.clock.LogUtils;
import com.katsuna.clock.R;
import com.katsuna.clock.alarm.ManageAlarmActivity;
import com.katsuna.clock.data.Alarm;
import com.katsuna.clock.data.AlarmStatus;
import com.katsuna.clock.info.InfoActivity;
import com.katsuna.clock.services.AlarmService;
import com.katsuna.clock.settings.SettingsActivity;
import com.katsuna.clock.util.Injection;
import com.katsuna.commons.controls.KatsunaNavigationView;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.ui.KatsunaActivity;
import com.katsuna.commons.utils.IUserProfileProvider;

import java.util.ArrayList;
import java.util.List;

import static com.katsuna.commons.utils.Constants.KATSUNA_PRIVACY_URL;


/**
 * Display a list of {@link Alarm}s. User can choose to view all active and inactive alarms,
 * and create and edit alarms.
 */
public class AlarmsActivity extends KatsunaActivity implements AlarmsContract.View,
        IUserProfileProvider {

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
    private TextView mNoAlarmsText;
    private ListView mAlarmsList;
    private AlarmsAdapter mAlarmsAdapter;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, AlarmService.class));

        setContentView(R.layout.activity_alarms);

        init();

        // Create the presenter
        new AlarmsPresenter(Injection.provideAlarmsDataSource(getApplicationContext()), this,
                Injection.provideAlarmScheduler(getApplicationContext()));
    }

    private void init() {
        mNoAlarmsText = findViewById(R.id.no_alarms);
        mPopupButton2 = findViewById(R.id.create_alarm_button);
        mPopupButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewAlarm();
            }
        });

        mFab2 = findViewById(R.id.create_alarm_fab);
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewAlarm();
            }
        });

        mAlarmsAdapter = new AlarmsAdapter(new ArrayList<Alarm>(0), mItemListener, this);
        mAlarmsList = findViewById(R.id.alarms_list);
        mAlarmsList.setAdapter(mAlarmsAdapter);

        initToolbar();
        initDrawer();
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
                        }

                        return true;
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
        mPopupButton2.setVisibility(View.GONE);
        LogUtils.d(TAG, "alarms fetched: " + alarms.size());
    }

    @Override
    public void showAddAlarm() {
        Intent i = new Intent(this, ManageAlarmActivity.class);
        startActivity(i);
    }

    @Override
    public void showAlarmDetailsUi(long alarmId) {
        Intent intent = new Intent(this, ManageAlarmActivity.class);
        intent.putExtra(ManageAlarmActivity.EXTRA_ALARM_ID, alarmId);
        startActivity(intent);
    }

    @Override
    public void showNoAlarms() {
        mNoAlarmsText.setVisibility(View.VISIBLE);
        mFab2.setVisibility(View.VISIBLE);
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
