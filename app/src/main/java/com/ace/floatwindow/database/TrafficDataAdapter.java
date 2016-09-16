package com.ace.floatwindow.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by JunBin on 2015/8/2.
 */
public class TrafficDataAdapter {
    public static final String PATH_TRAFFICDATA = "trafficdata";
    public static final String PATH_TRAFFICDATA_ID = PATH_TRAFFICDATA + "/id/";

    public static final Uri CONTENT_URI = Uri.parse(DatabaseProvider.SCHEME + DatabaseProvider.AUTHORITY + "/" + PATH_TRAFFICDATA);
    public static final Uri CONTENT_URI_MATCHER = Uri.parse(PATH_TRAFFICDATA);
    public static final Uri CONTENT_ID_URI_BASE = Uri.parse(DatabaseProvider.SCHEME + DatabaseProvider.AUTHORITY + "/" + PATH_TRAFFICDATA_ID);

    private static final String TABLE_NAME = "traffic";

    public static final String KEY_ROWID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    /**
     * 获取手机通过 2G/3G 接收的字节流量总数
     */
    public static final String KEY_MOBILERX = "mobilerx";
    /**
     * 获取手机通过 2G/3G 接收的数据包总数
     */
    public static final String KEY_MOBILERXP = "mobilerxp";
    /**
     * 获取手机通过 2G/3G 发出的字节流量总数
     */
    public static final String KEY_MOBILETX = "mobiletx";
    /**
     * 获取手机通过 2G/3G 发出的数据包总数
     */
    public static final String KEY_MOBILETXP = "mobiletxp";
    /**
     * 获取手机通过所有网络方式接收的字节流量总数(包括 wifi)
     */
    public static final String KEY_TOTALRX = "totalrx";
    /**
     * 获取手机通过所有网络方式接收的数据包总数(包括 wifi)
     */
    public static final String KEY_TOTALRXP = "totalrxp";
    /**
     * 获取手机通过所有网络方式发送的字节流量总数(包括 wifi)
     */
    public static final String KEY_TOTALTX = "totaltx";
    /**
     * 获取手机通过所有网络方式发送的数据包总数(包括 wifi)
     */
    public static final String KEY_TOTALTXP = "totaltxp";

    private final SQLiteDatabase db;

    private static final String[] Columns = new String[] {
            KEY_ROWID,
            KEY_TIMESTAMP,
            KEY_MOBILERX,
            KEY_MOBILERXP,
            KEY_MOBILETX,
            KEY_MOBILETXP,
            KEY_TOTALRX,
            KEY_TOTALRXP,
            KEY_TOTALTX,
            KEY_TOTALTXP
    };

    public TrafficDataAdapter(SQLiteDatabase db) {
        this.db = db;
    }

    public void createTable() {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ROWID + " INTEGER PRIMARY KEY, "
                + KEY_TIMESTAMP + " INTEGER, "
                + KEY_MOBILERX + " INTEGER, "
                + KEY_MOBILERXP + " INTEGER, "
                + KEY_MOBILETX + " INTEGER, "
                + KEY_MOBILETXP + " INTEGER, "
                + KEY_TOTALRX + " INTEGER, "
                + KEY_TOTALRXP + " INTEGER, "
                + KEY_TOTALTX + " INTEGER, "
                + KEY_TOTALTXP + " INTEGER"
                + ");");
        final String indexPathSql = "create index if not exists timestamp on "
                + TABLE_NAME + "("
                + KEY_TIMESTAMP + ");";
        db.execSQL(indexPathSql);
    }

    public void dropTable() {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void clearTable() {
        db.execSQL("delete from " + TABLE_NAME);
    }

    public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return db.query(
                TABLE_NAME,
                projection == null ? Columns : projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public Uri insert(ContentValues initialValues) {
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        long rowId = db.insert(TABLE_NAME, null, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(CONTENT_ID_URI_BASE, rowId);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into server db");
    }

    public int update(ContentValues values, String selection, String[] selectionArgs) {
        return db.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public int delete(String selection, String[] selectionArgs) {
        return db.delete(
                TABLE_NAME,
                selection,
                selectionArgs);
    }
}
