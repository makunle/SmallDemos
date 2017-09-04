package com.iflytek.mkl.wcbdtest;

import android.content.Context;

import com.tencent.wcdb.DatabaseErrorHandler;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;

/**
 * Created by makunle on 2017/9/1.
 */

public class WCDBDbHelper extends SQLiteOpenHelper {
    private Context context;

    public WCDBDbHelper(Context context, String name, byte[] password, SQLiteCipherSpec cipher, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, password, cipher, factory, version, errorHandler);
        this.context = context;
    }

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
