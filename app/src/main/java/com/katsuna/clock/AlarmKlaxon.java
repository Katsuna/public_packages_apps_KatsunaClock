package com.katsuna.clock;

import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Vibrator;

import com.katsuna.clock.data.Alarm;

/**
 * Manages playing alarm ringtones and vibrating the device.
 */
public final class AlarmKlaxon {

    private static final long[] VIBRATE_PATTERN = {500, 500};

    private static boolean sStarted = false;
    private static AsyncRingtonePlayer sAsyncRingtonePlayer;

    private AlarmKlaxon() {
    }

    public static void stop(Context context) {
        if (sStarted) {
            LogUtils.v("AlarmKlaxon.stop()");
            sStarted = false;
            getAsyncRingtonePlayer(context).stop();
            final Vibrator vibrator = getVibrator(context);
            vibrator.cancel();
        }
    }

    public static void start(Context context, Alarm alarm) {
        // Make sure we are stopped before starting
        stop(context);
        LogUtils.v("AlarmKlaxon.start()");

        Uri ringtone = Uri.parse(alarm.getRingtone());

        getAsyncRingtonePlayer(context).play(ringtone, 5000);

        if (alarm.isVibrate()) {
            final Vibrator vibrator = getVibrator(context);
            vibrate(vibrator);
        }

        sStarted = true;
    }

    private static void vibrate(Vibrator vibrator) {
        vibrator.vibrate(VIBRATE_PATTERN, 0, new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
    }

    private static Vibrator getVibrator(Context context) {
        return ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
    }

    private static synchronized AsyncRingtonePlayer getAsyncRingtonePlayer(Context context) {
        if (sAsyncRingtonePlayer == null) {
            sAsyncRingtonePlayer = new AsyncRingtonePlayer(context.getApplicationContext());
        }

        return sAsyncRingtonePlayer;
    }
}