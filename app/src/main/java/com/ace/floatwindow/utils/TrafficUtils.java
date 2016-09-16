package com.ace.floatwindow.utils;

import android.content.Context;

import com.ace.floatwindow.Logger;
import com.ace.floatwindow.R;
import com.ace.floatwindow.model.TrafficInfo;

/**
 * Created by JunBin on 2015/8/3.
 */
public class TrafficUtils {
    private final static String TAG = "TrafficUtils";
    private final static float KBYTE = 1024;
    private final static float MBYTE = KBYTE * 1024;
    private final static float GBYTE = MBYTE * 1024;

    public static TrafficCompareResult compare(final TrafficInfo trafficInfo, final TrafficInfo compareToTrafficInfo) {
        Logger.d(TAG, String.format("compare, info=[%s], compare=[%s]", trafficInfo, compareToTrafficInfo));
        final TrafficCompareResult result = new TrafficCompareResult();
        result.mMobileRx = trafficInfo.getMobileRx() - compareToTrafficInfo.getMobileRx();
        result.mMobileRxp = trafficInfo.getMobileRxp() - compareToTrafficInfo.getMobileRxp();
        result.mMobileTx = trafficInfo.getMobileTx() - compareToTrafficInfo.getMobileTx();
        result.mMobileTxp = trafficInfo.getMobileTxp() - compareToTrafficInfo.getMobileTxp();
        result.mTotalRx = trafficInfo.getTotalRx() - compareToTrafficInfo.getTotalRx();
        result.mTotalRxp = trafficInfo.getTotalRxp() - compareToTrafficInfo.getTotalRxp();
        result.mTotalTx = trafficInfo.getTotalTx() - compareToTrafficInfo.getTotalTx();
        result.mTotalTxp = trafficInfo.getTotalTxp() - compareToTrafficInfo.getTotalTxp();
        return result;
    }

    public static String getSpeed(final Context context, final long bytes1, final long bytes2) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getSpeed(context, bytes1));
        builder.append(" | ");
        builder.append(getSpeed(context, bytes2));
        return builder.toString();
    }

    public static String getSpeed(final Context context, final long bytes) {
        if (bytes > GBYTE) {
            return context.getString(R.string.float_speed_gb_per_s, String.format("%.1f", bytes / GBYTE));
        } else if (bytes > MBYTE) {
            return context.getString(R.string.float_speed_mb_per_s, String.format("%.1f", bytes / MBYTE));
        } else if (bytes > KBYTE) {
            return context.getString(R.string.float_speed_kb_per_s, String.format("%s", (long)(bytes / KBYTE)));
        } else {
            return context.getString(R.string.float_speed_b_per_s, String.format("%s", bytes));
        }
    }

    public static class TrafficCompareResult {
        private long mMobileRx;
        private long mMobileRxp;
        private long mMobileTx;
        private long mMobileTxp;
        private long mTotalRx;
        private long mTotalRxp;
        private long mTotalTx;
        private long mTotalTxp;

        private TrafficCompareResult() {}

        public long getMobileRx() {
            return mMobileRx;
        }

        public long getMobileRxp() {
            return mMobileRxp;
        }

        public long getMobileTx() {
            return mMobileTx;
        }

        public long getMobileTxp() {
            return mMobileTxp;
        }

        public long getTotalRx() {
            return mTotalRx;
        }

        public long getTotalRxp() {
            return mTotalRxp;
        }

        public long getTotalTx() {
            return mTotalTx;
        }

        public long getTotalTxp() {
            return mTotalTxp;
        }
    }
}
