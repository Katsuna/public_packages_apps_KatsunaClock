package com.katsuna.clock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.katsuna.clock.LogUtils;
import com.katsuna.clock.Utils;
import com.katsuna.clock.services.AlarmService;

public class AlarmInitReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmInitReceiver.class.getSimpleName();

    /**
     * When running on N devices, we're interested in the boot completed event that is sent while
     * the user is still locked, so that we can schedule alarms.
     */
    private static final String ACTION_BOOT_COMPLETED = Utils.isNOrLater()
            ? Intent.ACTION_LOCKED_BOOT_COMPLETED : Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        LogUtils.i(TAG, "AlarmInitReceiver action: " + action);

        if (ACTION_BOOT_COMPLETED.equals(action)
                || Intent.ACTION_TIME_CHANGED.equals(action)) {
            LogUtils.i(TAG, "BOOT COMPLETED action received:" + ACTION_BOOT_COMPLETED);
            // Stopwatch and timer data need to be updated on time change so the reboot
            // functionality works as expected.
            Intent myIntent = new Intent(context, AlarmService.class);
            context.startService(myIntent);
        }
    }
}
