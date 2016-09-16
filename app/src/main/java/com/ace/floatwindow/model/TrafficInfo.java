package com.ace.floatwindow.model;

import android.database.Cursor;

/**
 * Created by JunBin on 2015/8/2.
 */
public class TrafficInfo {
    /**
     * 产生流量当天的时间戳，同一天的时间戳都相同
     */
    private long mTimestamp;
    /**
     * 获取手机通过 2G/3G 接收的字节流量总数
     */
    private long mMobileRx;
    /**
     * 获取手机通过 2G/3G 接收的数据包总数
     */
    private long mMobileRxp;
    /**
     * 获取手机通过 2G/3G 发出的字节流量总数
     */
    private long mMobileTx;
    /**
     * 获取手机通过 2G/3G 发出的数据包总数
     */
    private long mMobileTxp;
    /**
     * 获取手机通过所有网络方式接收的字节流量总数(包括 wifi)
     */
    private long mTotalRx;
    /**
     * 获取手机通过所有网络方式接收的数据包总数(包括 wifi)
     */
    private long mTotalRxp;
    /**
     * 获取手机通过所有网络方式发送的字节流量总数(包括 wifi)
     */
    private long mTotalTx;
    /**
     * 获取手机通过所有网络方式发送的数据包总数(包括 wifi)
     */
    private long mTotalTxp;

    public static TrafficInfo parse(final Cursor cursor) {
        if (cursor != null) {
            final TrafficInfo trafficInfo = new TrafficInfo();
            trafficInfo.mTimestamp = cursor.getLong(1);
            trafficInfo.mMobileRx = cursor.getLong(2);
            trafficInfo.mMobileRxp = cursor.getLong(3);
            trafficInfo.mMobileTx = cursor.getLong(4);
            trafficInfo.mMobileTxp = cursor.getLong(5);
            trafficInfo.mTotalRx = cursor.getLong(6);
            trafficInfo.mTotalRxp = cursor.getLong(7);
            trafficInfo.mTotalTx = cursor.getLong(8);
            trafficInfo.mTotalTxp = cursor.getLong(9);
            return trafficInfo;
        }

        return  null;
    }

    private TrafficInfo() {}

    public TrafficInfo(final long timestamp) {
        mTimestamp = timestamp;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setMobileRx(final long rx) {
        mMobileRx = rx;
    }

    public long getMobileRx() {
        return  mMobileRx;
    }

    public void setMobileRxp(final long rxp) {
        mMobileRxp = rxp;
    }

    public long getMobileRxp() {
        return  mMobileRxp;
    }

    public void setMobileTx(final long tx) {
        mMobileTx = tx;
    }

    public long getMobileTx() {
        return  mMobileTx;
    }

    public void setMobileTxp(final long txp) {
        mMobileTxp = txp;
    }

    public long getMobileTxp() {
        return  mMobileTxp;
    }

    public void setTotalRx(final long rx) {
        mTotalRx = rx;
    }

    public long getTotalRx() {
        return mTotalRx;
    }

    public void setTotalRxp(final long rxp) {
        mTotalRxp = rxp;
    }

    public long getTotalRxp() {
        return  mTotalRxp;
    }

    public void setTotalTx(final long tx) {
        mTotalTx = tx;
    }

    public long getTotalTx() {
        return mTotalTx;
    }

    public void setTotalTxp(final long txp) {
        mTotalTxp = txp;
    }

    public long getTotalTxp() {
        return mTotalTxp;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("Timestamp=[%s], ", mTimestamp));
        builder.append(String.format("MobileRx=[%s], ", mMobileRx));
        builder.append(String.format("MobileRxp=[%s], ", mMobileRxp));
        builder.append(String.format("MobileTx=[%s], ", mMobileTx));
        builder.append(String.format("MobileTxp=[%s], ", mMobileTxp));
        builder.append(String.format("TotalRx=[%s], ", mTotalRx));
        builder.append(String.format("TotalRxp=[%s], ", mTotalRxp));
        builder.append(String.format("TotalTx=[%s], ", mTotalTx));
        builder.append(String.format("TotalTxp=[%s]", mTotalTxp));
        return builder.toString();
    }
}
