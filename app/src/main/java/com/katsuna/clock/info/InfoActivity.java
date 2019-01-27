package com.katsuna.clock.info;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.katsuna.clock.LogUtils;
import com.katsuna.clock.R;
import com.katsuna.commons.ui.KatsunaInfoActivity;

public class InfoActivity extends KatsunaInfoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initControls();
    }

    private void initControls() {
        initToolbar(R.drawable.common_ic_close_black54_24dp);

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mAppIcon.setImageResource(R.mipmap.ic_clock_launcher);
            mAppName.setText(R.string.app_name);
            mAppVersion.setText(getString(R.string.common_version_info, pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("%s %s", TAG, e.getMessage());
        }
    }
}
