package com.ace.floatwindow.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.ace.floatwindow.Logger;
import com.ace.floatwindow.model.TrafficInfo;

/**
 * Created by JunBin on 2015/8/2.
 */
public class TrafficDataHelper {
    private final static String TAG = "TrafficDataHelper";

    public static void update(final ContentResolver resolver, final TrafficInfo info) {
        final String where = String.format("%s=?", TrafficDataAdapter.KEY_TIMESTAMP);
        final String[] selectionArgs = new String[] { String.valueOf(info.getTimestamp()) };

        final ContentValues values = new ContentValues();
        values.put(TrafficDataAdapter.KEY_MOBILERX, info.getMobileRx());
        values.put(TrafficDataAdapter.KEY_MOBILERXP, info.getMobileRxp());
        values.put(TrafficDataAdapter.KEY_MOBILETX, info.getMobileTx());
        values.put(TrafficDataAdapter.KEY_MOBILETXP, info.getMobileTxp());
        values.put(TrafficDataAdapter.KEY_TOTALRX, info.getTotalRx());
        values.put(TrafficDataAdapter.KEY_TOTALRXP, info.getTotalRxp());
        values.put(TrafficDataAdapter.KEY_TOTALTX, info.getTotalTx());
        values.put(TrafficDataAdapter.KEY_TOTALTXP, info.getTotalTxp());

        if (exist(resolver, info.getTimestamp())) {
            resolver.update(TrafficDataAdapter.CONTENT_URI, values, where, selectionArgs);
            Logger.d(TAG, String.format("update traffic info, info=[%s]", info));
        } else {
            values.put(TrafficDataAdapter.KEY_TIMESTAMP, info.getTimestamp());
            resolver.insert(TrafficDataAdapter.CONTENT_URI, values);
            Logger.d(TAG, String.format("insert traffic info, info=[%s]", info));
        }
    }

    public static boolean exist(final ContentResolver resolver, final long timestamp) {
        final TrafficInfo existInfo = query(resolver, timestamp);
        return existInfo != null;
    }

    public static TrafficInfo query(final ContentResolver resolver, final long timestamp) {
        String selection = String.format("%s=?", TrafficDataAdapter.KEY_TIMESTAMP);
        String[] selectionArgs = new String[] { String.valueOf(timestamp) };
        final Cursor cursor = resolver.query(TrafficDataAdapter.CONTENT_URI, null, selection, selectionArgs, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return TrafficInfo.parse(cursor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return null;
    }
}
