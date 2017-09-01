package com.iflytek.mkl.wcbdtest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        View.OnClickListener radioClickLsn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreDBType = DBType;
                switch (v.getId()) {
                    case R.id.sqlite:
                        DBType = "sqlite";
//                        if (sqliteDbHelper == null) {
//                            sqliteDbHelper = new SqliteDbHelper(MainActivity.this, LITE_DB_NAME, null, 1);
//                        }
//                        sqliteDbHelper.getWritableDatabase().close();
                        break;
                    case R.id.sqlcipher:
                        DBType = "sqlcipher";
//                        if (sqlcipherDbHelper == null) {
//                            sqlcipherDbHelper = new SqlcipherDbHelper(MainActivity.this, CIPHER_DB_NAME, null, 1);
////                            SQLiteDatabase.loadLibs(MainActivity.this);
//                        }
//                        sqlcipherDbHelper.getWritableDatabase(CIPHER_PWD).close();
                        break;
                    case R.id.wcdb:
                        DBType = "wcdb";
//                        if (wcdbDbHelper == null) {
//                            wcdbDbHelper = new WCDBDbHelper(MainActivity.this, WCDB_DB_NAME, null, 1);
//                        }
//                        wcdbDbHelper.getWritableDatabase().close();
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
                android.database.sqlite.SQLiteDatabase db = getLiteDb();
                values.put("name", "sqlite_book" + bookId++);
                db.insert("book", null, values);
                break;
            case "sqlcipher":
                net.sqlcipher.database.SQLiteDatabase cipherdb = getCipherDb();
                values.put("name", "cipher_book" + bookId++);
                cipherdb.insert("book", null, values);
                break;
            case "wcdb":
                com.tencent.wcdb.database.SQLiteDatabase wcdbdb = getWCDBDb();
                values.put("name", "wcdb_book" + bookId++);
                wcdbdb.insert("book", null, values);
                break;
        }
        show("added");
    }

    public void delete(View view) {
        int num = 0;
        switch (DBType) {
            case "sqlite":
                android.database.sqlite.SQLiteDatabase db = getLiteDb();
                num = db.delete("book", null, null);
                break;
            case "sqlcipher":
                net.sqlcipher.database.SQLiteDatabase cipherdb = getCipherDb();
                num = cipherdb.delete("book", null, null);
                break;
            case "wcdb":
                com.tencent.wcdb.database.SQLiteDatabase wcdbdb = getWCDBDb();
                num = wcdbdb.delete("book", null, null);
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
                android.database.sqlite.SQLiteDatabase db = getLiteDb();
                num = db.update("book", values, "id % 2 = 0", null);
                break;
            case "sqlcipher":
                net.sqlcipher.database.SQLiteDatabase cipherdb = getCipherDb();
                num = cipherdb.update("book", values, "id % 2 = 0", null);
                break;
            case "wcdb":
                com.tencent.wcdb.database.SQLiteDatabase wcdbdb = getWCDBDb();
                num = wcdbdb.update("book", values, "id % 2 = 0", null);
                break;
        }
        show("updated " + num + " columns in " + DBType);
    }

    public void query(View view) {
        Cursor cursor = null;
        switch (DBType) {
            case "sqlite":
                android.database.sqlite.SQLiteDatabase db = getLiteDb();
                cursor = db.query("book", null, null, null, null, null, null);
                break;
            case "sqlcipher":
                net.sqlcipher.database.SQLiteDatabase cipherdb = getCipherDb();
                cursor = cipherdb.query("book", null, null, null, null, null, null);
                break;
            case "wcdb":
                com.tencent.wcdb.database.SQLiteDatabase wcdbdb = getWCDBDb();
                cursor = wcdbdb.query("book", null, null, null, null, null, null);
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

    private android.database.sqlite.SQLiteDatabase getLiteDb() {
        if (liteDb == null || !liteDb.isOpen()) {
            liteDb = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(
                    getDatabasePath(LITE_DB_NAME), null);
            liteDb.execSQL(CREATE_DB);
        }
        return liteDb;
    }

    private SQLiteDatabase getCipherDb() {
        if (cipherDb == null || !cipherDb.isOpen()) {
            SQLiteDatabase.loadLibs(this);
            cipherDb = SQLiteDatabase.openOrCreateDatabase(
                    getDatabasePath(CIPHER_DB_NAME), CIPHER_PWD, null);
            cipherDb.execSQL(CREATE_DB);
        }
        return cipherDb;
    }

    private com.tencent.wcdb.database.SQLiteDatabase getWCDBDb() {
        if (wcdbDb != null) {
            wcdbDb.close();
        }
        if (PreDBType == "sqlcipher") {
            SQLiteCipherSpec cipher = new SQLiteCipherSpec()
                    .setPageSize(1024)
                    .setSQLCipherVersion(3);
            wcdbDb = com.tencent.wcdb.database.SQLiteDatabase.openOrCreateDatabase(
                    getDatabasePath(CIPHER_DB_NAME),
                    CIPHER_PWD.getBytes(),
                    cipher,
                    null,
                    null,
                    5
            );

        } else {
            wcdbDb = com.tencent.wcdb.database.SQLiteDatabase.openOrCreateDatabase(
                    getDatabasePath(LITE_DB_NAME),
                    null
            );

        }
        wcdbDb.execSQL(CREATE_DB);

        return wcdbDb;
    }
}
