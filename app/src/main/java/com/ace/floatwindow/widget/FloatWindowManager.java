package com.ace.floatwindow.widget;

import android.content.Context;

import com.ace.floatwindow.Logger;

import java.util.ArrayList;

public class FloatWindowManager {
    private final static String TAG = "FloatWindowManager";

    private static FloatWindowManager mInstance;

    private TrafficFloatView mTrafficFloatView;
    private ArrayList<IFloatWindowListener> mFloatViewListeners = new ArrayList<IFloatWindowListener>();

    public static FloatWindowManager getInstance() {
        if (mInstance == null) {
            mInstance = new FloatWindowManager();
        }

        return mInstance;
    }

    private FloatWindowManager() {}

    public void showFloatView(final Context context) {
        if (mTrafficFloatView == null) {
            mTrafficFloatView = new TrafficFloatView(context);
        }

        if (mTrafficFloatView.isShown()) {
            Logger.d(TAG, "Float view is shown already, skipping.");
            return;
        }

        mTrafficFloatView.show();

        for (IFloatWindowListener listener : mFloatViewListeners) {
            if (listener != null) {
                listener.onFloatWindowShown(mTrafficFloatView);
            }
        }

        Logger.d(TAG, "Show float view end.");
    }

    public void  hideFloatView(final Context context) {
        if (mTrafficFloatView == null || !mTrafficFloatView.isShown()) {
            Logger.d(TAG, "Float view is not shown, skipping.");
            return;
        }

        Logger.d(TAG, "Hide float view start.");
        mTrafficFloatView.hide();

        for (IFloatWindowListener listener : mFloatViewListeners) {
            if (listener != null) {
                listener.onFloatWindowHidden(mTrafficFloatView);
            }
        }

        Logger.d(TAG, "Hide float view end.");
    }

    public void updateNetworkType() {
        if (mTrafficFloatView != null) {
            mTrafficFloatView.onNetworkTypeChanged();
        }
    }

    public void registerListener(final IFloatWindowListener listener) {
        if (listener != null && !mFloatViewListeners.contains(listener)) {
            mFloatViewListeners.add(listener);
        }
    }

    public void unregisterListener(final IFloatWindowListener listener) {
        mFloatViewListeners.remove(listener);
    }

    public static interface IFloatWindowListener {
        void onFloatWindowShown(final FloatView floatView);
        void onFloatWindowHidden(final FloatView floatView);
    }
}