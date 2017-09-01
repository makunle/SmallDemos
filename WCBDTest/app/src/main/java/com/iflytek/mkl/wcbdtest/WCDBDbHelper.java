package com.iflytek.mkl.wcbdtest;

import android.content.Context;

import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;

/**
 * Created by makunle on 2017/9/1.
 */

public class WCDBDbHelper extends SQLiteOpenHelper {
    private Context context;

    public WCDBDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MainActivity.CREATE_DB);
        ((MainActivity)context).show("wcdb create success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
