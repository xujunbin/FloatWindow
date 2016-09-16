package com.ace.floatwindow;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by JunBin on 2016/1/15.
 */
public class FloatApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
