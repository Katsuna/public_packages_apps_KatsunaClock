/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
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
