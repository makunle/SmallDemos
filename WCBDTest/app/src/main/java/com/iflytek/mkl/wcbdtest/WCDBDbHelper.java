package com.iflytek.mkl.wcbdtest;

import android.content.Context;

import com.tencent.wcdb.DatabaseErrorHandler;
import com.tencent.wcdb.DatabaseUtils;
import com.tencent.wcdb.database.SQLiteCipherSpec;
import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;
import com.tencent.wcdb.repair.RepairKit;

import java.io.File;

/**
 * Created by makunle on 2017/9/1.
 */

public class WCDBDbHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String mDbName = null;
    private int mVersion = 0;
    private byte[] mPassword;

    public WCDBDbHelper(Context context, String name, byte[] password, SQLiteCipherSpec cipher, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, password, cipher, factory, version, errorHandler);
        this.mContext = context;
        this.mDbName = name;
        this.mVersion = version;
        this.mPassword = password;
    }

    public WCDBDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
        this.mDbName = name;
        this.mVersion = version;
    }

    public WCDBDbHelper(Context context, String name, byte[] password, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, password, factory, version, errorHandler);
        this.mContext = context;
        this.mDbName = name;
        this.mVersion = version;
        this.mPassword = password;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (mDbName.equals(MainActivity.WCDB_DB_NAME)) {
            File oldDbFile = mContext.getDatabasePath(MainActivity.LITE_DB_NAME);
            if (oldDbFile.exists()) {
                db.endTransaction();

                String sql = String.format("ATTACH DATABASE %s AS old KEY '';",
                        DatabaseUtils.sqlEscapeString(oldDbFile.getPath()));
                db.execSQL(sql);

                db.beginTransaction();
                DatabaseUtils.stringForQuery(db, "SELECT sqlcipher_export('main', 'old')", null);
                db.setTransactionSuccessful();
                db.endTransaction();

                int oldVersion = (int) DatabaseUtils.longForQuery(db, "PRAGMA old.user_version;", null);

                db.execSQL("DETACH DATABASE old;");

                oldDbFile.delete();

                db.beginTransaction();

                if (oldVersion > mVersion) {
                    onDowngrade(db, oldVersion, mVersion);
                } else {
                    onUpgrade(db, oldVersion, mVersion);
                }

                ((MainActivity) mContext).show("transform non encrypted to encrypted success");
            }
        } else {
            db.execSQL(MainActivity.CREATE_DB);
            ((MainActivity) mContext).show("wcdb create success");
        }

        RepairKit.MasterInfo.save(db, db.getPath() + "-mbak", mPassword);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RepairKit.MasterInfo.save(db, db.getPath() + "-mbak", mPassword);
    }
}
