package com.ace.floatwindow.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by JunBin on 2015/8/2.
 */
public class DatabaseProvider extends ContentProvider {
    public static final String SCHEME = "content://";
    public final static String AUTHORITY = "com.ace.floatwindow.provider.database";

    private final static UriMatcher sUriMatcher;
    private static final int TRAFFICDATA = 1;

    private DatabaseManagerHelper mOpenHelper;
    private final ReentrantLock lock = new ReentrantLock();

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TrafficDataAdapter.CONTENT_URI_MATCHER.toString(), TRAFFICDATA);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseManagerHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (sUriMatcher.match(uri)) {
            case TRAFFICDATA: {
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                return new TrafficDataAdapter(db).query(projection, selection, selectionArgs, sortOrder);
            }
            default: {
                throw new IllegalArgumentException("invalidate query data");
            }
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        lock.lock();
        try {
            return insertInternal(uri, values);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        lock.lock();
        try {
            return deleteInternal(uri, selection, selectionArgs);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        lock.lock();
        try {
            return updateInternal(uri, values, selection, selectionArgs);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        lock.lock();
        try {
            return applyBatchInternal(operations);
        } finally {
            lock.unlock();
        }
    }

    private int deleteInternal(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case TRAFFICDATA: {
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                return new TrafficDataAdapter(db).delete(selection, selectionArgs);
            }
        }
        return 0;
    }

    private Uri insertInternal(Uri uri, ContentValues values) {
        switch (sUriMatcher.match(uri)) {
            case TRAFFICDATA: {
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                return new TrafficDataAdapter(db).insert(values);
            }
            default: {
                throw new IllegalArgumentException("invalidate insert data");
            }
        }
    }

    private int updateInternal(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case TRAFFICDATA: {
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                return new TrafficDataAdapter(db).update(values, selection, selectionArgs);
            }
            default: {
                throw new IllegalArgumentException("invalidate update data");
            }
        }
    }

    public ContentProviderResult[] applyBatchInternal(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] result = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }
}
