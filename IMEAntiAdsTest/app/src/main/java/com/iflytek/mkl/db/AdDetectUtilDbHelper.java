package com.iflytek.mkl.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by makunle on 2017/9/26.
 */

public class AdDetectUtilDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "AdDetectUtilDb";

    public static final String CREATE_DB_AD_CHECK =
            "create table if not exists ad_sdk_check(pkgname text primary key, have_ad_sdk integer);";
    public static final String CREATE_DB_DETECT_RESULT =
            "create table if not exists detect_result(id integer primary key autoincrement, pkgname text, score float, rule text, time integer);";
    public static final String CREATE_DB_CONTAIN_INPUT =
            "create table if not exists contain_input(pkgname text);";

    public AdDetectUtilDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_AD_CHECK);
        db.execSQL(CREATE_DB_DETECT_RESULT);
        db.execSQL(CREATE_DB_CONTAIN_INPUT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists ad_sdk_check;");
        db.execSQL("drop table if exists detect_result;");
        db.execSQL("drop table if exists contain_input;");
        onCreate(db);
    }
}
