package com.iflytek.mkl.wcbdtest;

import android.content.Context;

/**
 * sqlite对应的dbhelper
 */
public class SqliteDbHelper extends android.database.sqlite.SQLiteOpenHelper {
    private Context context;

    public SqliteDbHelper(Context context, String name, android.database.sqlite.SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL(MainActivity.CREATE_DB);
        ((MainActivity) context).show("sqlite create success");
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
