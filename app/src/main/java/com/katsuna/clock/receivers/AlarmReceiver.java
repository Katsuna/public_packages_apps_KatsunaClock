package com.katsuna.clock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.katsuna.clock.AlarmActivationActivity;
import com.katsuna.clock.services.utils.AlarmsScheduler;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";


    @Override
    public void onReceive(final Context context, Intent intent) {
        // Put here YOUR code.
        long alarmId = Objects.requireNonNull(intent.getExtras()).getLong(AlarmsScheduler.ALARM_ID);
        Log.e(TAG, "onReceive: " + alarmId);

        Toast.makeText(context, "Alarm with id: " + alarmId, Toast.LENGTH_LONG).show();

        Intent i = new Intent(context, AlarmActivationActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(AlarmsScheduler.ALARM_ID, alarmId);
        context.startActivity(i);
    }

}
