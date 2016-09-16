package com.ace.floatwindow.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JunBin on 2015/8/2.
 */
public class DatabaseManagerHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "float.db";
    private final static int DB_VERSION = 1;

    public DatabaseManagerHelper(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new TrafficDataAdapter(db).createTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
