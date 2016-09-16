package com.ace.floatwindow;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ace.floatwindow.eventbus.EventBusInfo;

import de.greenrobot.event.EventBus;

/**
 * Created by JunBin on 2016/1/11.
 */
public class NetworkManager {
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_ETHERNET = 2;
    public static final int TYPE_MOBILE = 3;

    private static final String TAG = "NetworkManager";
    private static boolean isNetworkConnected = true;
    private static int mNetworkType = TYPE_WIFI;
    private static Object lock = new Object();

    public static void init(final Context context) {
        mNetworkType = obtainNetworkType(context);
        isNetworkConnected = isWiFiActive(context) || isMobileDataActive(context) || isEthernetActive(context);
    }

    public static void updateNetworkState(final Context context) {
        try {
            synchronized (lock) {
                if (isWiFiActive(context) || isMobileDataActive(context) || isEthernetActive(context)) {
                    Logger.d(TAG, "network real state: " + true);
                    if (!isNetworkConnected) {
                        notifyNetworkChanged(context, true);
                    } else {
                        Logger.d(TAG, "skipped");
                    }
                } else {
                    Logger.d(TAG, "network real state: " + false);
                    if (isNetworkConnected) {
                        notifyNetworkChanged(context, false);
                    } else {
                        Logger.d(TAG, "skipped");
                    }
                }

                final int currentType = obtainNetworkType(context);
                if (currentType != mNetworkType) {
                    Logger.d(TAG, "network type changed: " + currentType);
                    mNetworkType = currentType;
                    notifyNetworkTypeChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void notifyNetworkChanged(final Context context, final boolean connected) {
        isNetworkConnected = connected;
        Logger.d(TAG, "notifyNetworkChanged: " + connected);
        EventBus.getDefault().post(new EventBusInfo(EventBusInfo.NETWORK_STATE_CHANGED));
    }

    private static void notifyNetworkTypeChanged() {
        EventBus.getDefault().post(new EventBusInfo(EventBusInfo.NETWORK_TYPE_CHANGED));
    }

    private static int obtainNetworkType(final Context context) {
        if (isEthernetActive(context)) {
            return TYPE_ETHERNET;
        } else if (isWiFiActive(context)) {
            return TYPE_WIFI;
        } else if (isMobileDataActive(context)) {
            return TYPE_MOBILE;
        } else {
            return TYPE_UNKNOWN;
        }
    }

    public static boolean isNetworkConnected() {
        return isNetworkConnected;
    }

    public static int getNetworkConnectType() {
        return mNetworkType;
    }

    public static boolean isWiFiActive(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi == null) {
            return false;
        }
        return wifi.isConnected();
    }

    public static boolean isMobileDataActive(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile == null) {
            return false;
        }
        return mobile.isConnected();

//        int netSubtype = mobile.getSubtype();
//        boolean b3GType = (netSubtype == TelephonyManager.NETWORK_TYPE_UMTS) ||
//                (netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_0) ||
//                (netSubtype == TelephonyManager.NETWORK_TYPE_EVDO_A) ||
//                (netSubtype == TelephonyManager.NETWORK_TYPE_1xRTT) ||
//                (netSubtype == TelephonyManager.NETWORK_TYPE_HSDPA) ||
//                (netSubtype == TelephonyManager.NETWORK_TYPE_HSUPA) ||
//                (netSubtype == TelephonyManager.NETWORK_TYPE_HSPA) ||
//                (netSubtype == 12) ||        // TelephonyManager.NETWORK_TYPE_EVDO_B
//                (netSubtype == 13) ||        // TelephonyManager.NETWORK_TYPE_LTE
//                (netSubtype == 14) ||        // TelephonyManager.NETWORK_TYPE_EHRPD
//                (netSubtype == 15);          // TelephonyManager.NETWORK_TYPE_HSPAP

//        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        if (b3GType && !mTelephony.isNetworkRoaming()) {
//            return mobile.isConnected();
//        }

//        return false;
    }

    public static boolean isEthernetActive(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethernet = conMan.getNetworkInfo(9);
        if (ethernet == null) {
            return false;
        }
        return ethernet.isConnected();
    }
}
