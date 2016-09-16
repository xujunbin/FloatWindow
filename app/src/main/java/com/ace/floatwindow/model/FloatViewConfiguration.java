package com.ace.floatwindow.model;

import android.content.Context;

import com.ace.floatwindow.Logger;
import com.ace.floatwindow.NetworkManager;
import com.ace.floatwindow.data.EncryptPreference;
import com.ace.floatwindow.data.Preference;

/**
 * Created by JunBin on 2015/8/16.
 */
public class FloatViewConfiguration {
    private final static String TAG = FloatViewConfiguration.class.getSimpleName();

    private boolean mShowTrafficFloatView = true;
    private boolean mAutoStartWithBoot = true;
    private boolean mSplitUpDownTraffic = false;
    private boolean mAllowDragToStatusBar = false;
    private boolean mLockFloatView = false;
    private boolean mAutoHideIfNoNetwork = false;
    private int mTrafficRefreshInterval = 1000;

    public static FloatViewConfiguration load(final Context context) {
        final FloatViewConfiguration configuration = new FloatViewConfiguration();
        final EncryptPreference preference = EncryptPreference.getInstance(context, Preference.DEFAULT_PREFERENCE_NAME);
        configuration.mShowTrafficFloatView = preference.getBoolean(Preference.SHOW_TRAFFIC_FLOAT_WINDOW, true);
        configuration.mAutoStartWithBoot = preference.getBoolean(Preference.AUTO_START_FLOAT_WINDOW_WITH_BOOT, true);
        configuration.mSplitUpDownTraffic = preference.getBoolean(Preference.SPLIT_UP_DOWN_TRAFFIC, configuration.mSplitUpDownTraffic);
        configuration.mTrafficRefreshInterval = preference.getInt(Preference.TRAFFIC_REFRESH_INTERVAL, configuration.mTrafficRefreshInterval);
        configuration.mAllowDragToStatusBar = preference.getBoolean(Preference.ALLOW_DRAG_TO_STATUS_BAR, configuration.mAllowDragToStatusBar);
        configuration.mLockFloatView = preference.getBoolean(Preference.LOCK_FLOAT_VIEW, configuration.mLockFloatView);
        configuration.mAutoHideIfNoNetwork = preference.getBoolean(Preference.AUTO_HIDE_IF_NO_NETWORK, configuration.mAutoHideIfNoNetwork);
        Logger.d(TAG, String.format("load from preference: %s", configuration));
        return configuration;
    }

    private FloatViewConfiguration() {}

    public boolean isShowTrafficFloatView() {
        return mShowTrafficFloatView && !(mAutoHideIfNoNetwork && !NetworkManager.isNetworkConnected());
    }

    public boolean isAutoStartWithBoot() {
        return mAutoStartWithBoot;
    }

    public boolean isSplitUpDownTraffic() {
        return mSplitUpDownTraffic;
    }

    public int getTrafficRefreshInterval() {
        return mTrafficRefreshInterval;
    }

    public int getTrafficRefreshIntervalInSecond() {
        return mTrafficRefreshInterval / 1000;
    }

    public boolean allowDragToStatusBar() {
        return mAllowDragToStatusBar;
    }

    public void setAllowDragToStatusBar(final boolean allow) {
        mAllowDragToStatusBar = allow;
    }

    public boolean lockFloatView() {
        return mLockFloatView;
    }

    public boolean autoHideIfNoNetwork() {
        return mAutoHideIfNoNetwork;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("ShowView=[%s], ", mShowTrafficFloatView));
        builder.append(String.format("AutoStart=[%s], ", mAutoStartWithBoot));
        builder.append(String.format("SplitUpDown=[%s], ", mSplitUpDownTraffic));
        builder.append(String.format("Interval=[%s], ", mTrafficRefreshInterval));
        builder.append(String.format("DragStatusBar=[%s], ", mAllowDragToStatusBar));
        builder.append(String.format("AutoHide=[%s], ", mAutoHideIfNoNetwork));
        builder.append(String.format("LockView=[%s]", mLockFloatView));
        return builder.toString();
    }
}
