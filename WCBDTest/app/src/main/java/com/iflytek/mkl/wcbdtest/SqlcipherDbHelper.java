package com.iflytek.mkl.wcbdtest;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by makunle on 2017/9/1.
 */

public class SqlcipherDbHelper extends SQLiteOpenHelper {
    private Context context;

    public SqlcipherDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MainActivity.CREATE_DB);
        ((MainActivity) context).show("sqlcipher create success");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
