package com.iflytek.mkl.wcbdtest;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tencent.wcdb.database.SQLiteCipherSpec;

import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String DBType = "";
    private String PreDBType = "sqlite";

    private TextView output;
    private ScrollView scroll;

    public static final String CREATE_DB = "create table if not exists book(id integer primary key autoincrement, name text)";
    public static final String CIPHER_PWD = "chiperpassword";
    public static final String LITE_DB_NAME = "book_lite.db";
    public static final String CIPHER_DB_NAME = "book_chiper.db";
    public static final String WCDB_DB_NAME = "book_wcdb.db";


    SqliteDbHelper sqliteDbHelper;
    SqlcipherDbHelper sqlcipherDbHelper;
    WCDBDbHelper wcdbDbHelper;

    android.database.sqlite.SQLiteDatabase liteDb;
    SQLiteDatabase cipherDb;
    com.tencent.wcdb.database.SQLiteDatabase wcdbDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase.loadLibs(this);

        View.OnClickListener radioClickLsn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreDBType = DBType;
                switch (v.getId()) {
                    case R.id.sqlite:
                        DBType = "sqlite";
                        if (sqliteDbHelper != null && liteDb != null && liteDb.isOpen()) {
                            sqliteDbHelper.close();
                            sqliteDbHelper = null;
                            liteDb = null;
                        }
                        sqliteDbHelper = new SqliteDbHelper(MainActivity.this, LITE_DB_NAME, null, 1);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            sqliteDbHelper.setWriteAheadLoggingEnabled(true);
                        }
                        liteDb = sqliteDbHelper.getWritableDatabase();
                        break;
                    case R.id.sqlcipher:
                        DBType = "sqlcipher";
                        if (sqlcipherDbHelper != null && cipherDb != null && cipherDb.isOpen()) {
                            sqlcipherDbHelper.close();
                            sqlcipherDbHelper = null;
                            cipherDb = null;
                        }
                        sqlcipherDbHelper = new SqlcipherDbHelper(MainActivity.this, CIPHER_DB_NAME, null, 1);
                        cipherDb = sqlcipherDbHelper.getWritableDatabase(CIPHER_PWD);
                        break;
                    case R.id.wcdb:
                        DBType = "wcdb";
                        if (wcdbDbHelper != null && wcdbDb != null && wcdbDb.isOpen()) {
                            wcdbDbHelper.close();
                            wcdbDbHelper = null;
                            wcdbDb = null;
                        }
                        if (PreDBType.equals("sqlcipher")) {
                            SQLiteCipherSpec cipher = new SQLiteCipherSpec()
                                    .setPageSize(1024)
                                    .setSQLCipherVersion(3);
                            wcdbDbHelper = new WCDBDbHelper(MainActivity.this, CIPHER_DB_NAME, CIPHER_PWD.getBytes(),
                                    cipher, null, 1, null);
                        } else if (PreDBType.equals("sqlite")) {
                            wcdbDbHelper = new WCDBDbHelper(MainActivity.this, LITE_DB_NAME, null, 1);
                        } else {
                            //WCDB_DB_NAME
                            wcdbDbHelper = new WCDBDbHelper(MainActivity.this, WCDB_DB_NAME,
                                    CIPHER_PWD.getBytes(), null, 1, null);

                        }
                        wcdbDbHelper.setWriteAheadLoggingEnabled(true);
                        wcdbDb = wcdbDbHelper.getWritableDatabase();
                        break;
                }
            }
        };
        findViewById(R.id.sqlite).setOnClickListener(radioClickLsn);
        findViewById(R.id.sqlcipher).setOnClickListener(radioClickLsn);
        findViewById(R.id.wcdb).setOnClickListener(radioClickLsn);

        output = (TextView) findViewById(R.id.output);
        scroll = (ScrollView) findViewById(R.id.scroll);
    }

    private int bookId = 0;

    public void add(View view) {
        ContentValues values = new ContentValues();

        switch (DBType) {
            case "sqlite":
//                android.database.sqlite.SQLiteDatabase liteDb = getLiteDb();
                values.put("name", "sqlite_book" + bookId++);
                liteDb.insert("book", null, values);
                break;
            case "sqlcipher":
//                net.sqlcipher.database.SQLiteDatabase cipherDb = getCipherDb();
                values.put("name", "cipher_book" + bookId++);
                cipherDb.insert("book", null, values);
                break;
            case "wcdb":
//                com.tencent.wcdb.database.SQLiteDatabase wcdbDb = getWCDBDb();
                values.put("name", "wcdb_book" + bookId++);
                wcdbDb.insert("book", null, values);
                break;
        }
        show("added");
    }

    public void delete(View view) {
        int num = 0;
        switch (DBType) {
            case "sqlite":
//                android.database.sqlite.SQLiteDatabase liteDb = getLiteDb();
                num = liteDb.delete("book", null, null);
                break;
            case "sqlcipher":
//                net.sqlcipher.database.SQLiteDatabase cipherDb = getCipherDb();
                num = cipherDb.delete("book", null, null);
                break;
            case "wcdb":
//                com.tencent.wcdb.database.SQLiteDatabase wcdbDb = getWCDBDb();
                num = wcdbDb.delete("book", null, null);
                break;
        }
        show("delete " + num + " columns in " + DBType);
    }

    public void update(View view) {
        int num = 0;
        ContentValues values = new ContentValues();
        values.put("name", "update_" + DBType);
        switch (DBType) {
            case "sqlite":
//                android.database.sqlite.SQLiteDatabase liteDb = getLiteDb();
                num = liteDb.update("book", values, "id % 2 = 0", null);
                break;
            case "sqlcipher":
//                net.sqlcipher.database.SQLiteDatabase cipherDb = getCipherDb();
                num = cipherDb.update("book", values, "id % 2 = 0", null);
                break;
            case "wcdb":
//                com.tencent.wcdb.database.SQLiteDatabase wcdbDb = getWCDBDb();
                num = wcdbDb.update("book", values, "id % 2 = 0", null);
                break;
        }
        show("updated " + num + " columns in " + DBType);
    }

    public void query(View view) {
        Cursor cursor = null;
        switch (DBType) {
            case "sqlite":
//                android.database.sqlite.SQLiteDatabase liteDb = getLiteDb();
                cursor = liteDb.query("book", null, null, null, null, null, null);
                break;
            case "sqlcipher":
//                net.sqlcipher.database.SQLiteDatabase cipherDb = getCipherDb();
                cursor = cipherDb.query("book", null, null, null, null, null, null);
                break;
            case "wcdb":
//                com.tencent.wcdb.database.SQLiteDatabase wcdbDb = getWCDBDb();
                cursor = wcdbDb.query("book", null, null, null, null, null, null);
                break;
        }

        //show data;
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------" + DBType + "----------------------\n");
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            sb.append(cursor.getColumnName(i)).append("      \t");
        }
        show(sb.toString());
        while (cursor != null && cursor.moveToNext()) {
            sb = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                sb.append(cursor.getString(i)).append("      \t");
            }
            show(sb.toString());
        }
        cursor.close();
    }

    public void show(CharSequence msg) {
        Log.d(TAG, msg + "");
        output.append(msg + "\n");
        scroll.fullScroll(ScrollView.FOCUS_DOWN);
    }

//    private android.database.sqlite.SQLiteDatabase getLiteDb() {
//        if (liteDb == null || !liteDb.isOpen()) {
//            liteDb = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(
//                    getDatabasePath(LITE_DB_NAME), null);
//            liteDb.execSQL(CREATE_DB);
//        }
//        return liteDb;
//    }
//
//    private SQLiteDatabase getCipherDb() {
//        if (cipherDb == null || !cipherDb.isOpen()) {
//            SQLiteDatabase.loadLibs(this);
//            cipherDb = SQLiteDatabase.openOrCreateDatabase(
//                    getDatabasePath(CIPHER_DB_NAME), CIPHER_PWD, null);
//            cipherDb.execSQL(CREATE_DB);
//        }
//        return cipherDb;
//    }
//
//    private com.tencent.wcdb.database.SQLiteDatabase getWCDBDb() {
//        if (wcdbDb != null) {
//            wcdbDb.close();
//        }
//        if (PreDBType == "sqlcipher") {
//            SQLiteCipherSpec cipher = new SQLiteCipherSpec()
//                    .setPageSize(1024)
//                    .setSQLCipherVersion(3);
//            wcdbDb = com.tencent.wcdb.database.SQLiteDatabase.openOrCreateDatabase(
//                    getDatabasePath(CIPHER_DB_NAME),
//                    CIPHER_PWD.getBytes(),
//                    cipher,
//                    null,
//                    null,
//                    5
//            );
//
//        } else {
//            wcdbDb = com.tencent.wcdb.database.SQLiteDatabase.openOrCreateDatabase(
//                    getDatabasePath(LITE_DB_NAME),
//                    null
//            );
//
//        }
//        wcdbDb.execSQL(CREATE_DB);
//
//        return wcdbDb;
//    }
}
