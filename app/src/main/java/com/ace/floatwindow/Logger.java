package com.ace.floatwindow;

import android.util.Log;

public class Logger {
    public static void d(final String tag, final String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}