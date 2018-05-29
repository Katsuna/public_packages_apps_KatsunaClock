package com.katsuna.clock.util;

import android.app.KeyguardManager;
import android.content.Context;

public class Keyguard {

    private static String TAG = Keyguard.class.getSimpleName();

    public static void disableKeyguard(Context ctx) {
        KeyguardManager keyguard = (KeyguardManager) ctx.getSystemService(Context.KEYGUARD_SERVICE);
        keyguard.newKeyguardLock(TAG).disableKeyguard();
    }
}
