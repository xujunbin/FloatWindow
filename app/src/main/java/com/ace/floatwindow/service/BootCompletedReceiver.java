package com.ace.floatwindow.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;

/**
 * Created by JunBin on 2015/8/6.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            final EncryptPreference preference = EncryptPreference.getInstance(context, Preference.DEFAULT_PREFERENCE_NAME);
            final boolean autoStart = preference.getBoolean(Preference.AUTO_START_FLOAT_WINDOW_WITH_BOOT, true);
            if (autoStart) {
                context.startService(new Intent(context, DeamonService.class));
            }
        }
    }
}
