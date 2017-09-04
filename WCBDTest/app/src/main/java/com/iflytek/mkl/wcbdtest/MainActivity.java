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

    SQLiteCipherSpec cipher = new SQLiteCipherSpec()
            .setPageSize(1024)
            .setSQLCipherVersion(3);

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
//                        if (PreDBType.equals("sqlcipher")) {
//                            wcdbDbHelper = new WCDBDbHelper(MainActivity.this, CIPHER_DB_NAME, CIPHER_PWD.getBytes(),
//                                    cipher, null, 1, null);
//                        } else if (PreDBType.equals("sqlite")) {
//                            wcdbDbHelper = new WCDBDbHelper(MainActivity.this, LITE_DB_NAME, null, 1);
//                        } else {
                        //WCDB_DB_NAME
                        wcdbDbHelper = new WCDBDbHelper(MainActivity.this, WCDB_DB_NAME, CIPHER_PWD.getBytes(),
                                cipher, null, 1, null);

//                        }
                        wcdbDbHelper.setWriteAheadLoggingEnabled(true);
                        wcdbDb = wcdbDbHelper.getWritableDatabase();

//                        wcdbDb = com.tencent.wcdb.database.SQLiteDatabase.openOrCreateDatabase(getDatabasePath(WCDB_DB_NAME),
//                                CIPHER_PWD.getBytes(), null, null, 4);
//                        wcdbDb.enableWriteAheadLogging();
//                        wcdbDb.execSQL(CREATE_DB);

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


    //测试多线程是并行还是串行
    private class MRunnable implements Runnable {
        int s, e;
        long start;

        public MRunnable(int start, int end) {
            this.s = start;
            this.e = end;
            this.start = System.currentTimeMillis();
        }

        @Override
        public void run() {
//            for (int i = s; i < e; i++) {
//                String k = "" + i;
//            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    show(Thread.currentThread().getId() + "  " + (System.currentTimeMillis() - start));
                }
            });
        }
    }


    public void add(View view) {
        bookId = 0;
        long totalTime = 0;
        int totalNum = 0;

        final long start = System.currentTimeMillis();
        final String type = DBType;
        final int threadNum = 1;
        final int insertSize = 50000;

        final ContentValues values = new ContentValues();
        values.put("name", "test_book" + bookId++);

        Runnable insertRunnable = new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case "sqlite":
                        for (int i = 0; i < insertSize / threadNum; i++) {
                            liteDb.insert("book", null, values);
                        }
                        break;
                    case "sqlcipher":
                        for (int i = 0; i < insertSize / threadNum; i++) {
                            cipherDb.insert("book", null, values);
                        }
                        break;
                    case "wcdb":
                        for (int i = 0; i < insertSize / threadNum; i++) {
                            wcdbDb.insert("book", null, values);
                        }
                        break;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show(type + " insert " + insertSize / threadNum + " columns, cause time: " + (System.currentTimeMillis() - start));
                    }
                });
            }
        };

        for (int i = 0; i < threadNum; i++) {
            new Thread(insertRunnable).start();
        }

//        for (int t = 0; t < 5; t++) {
//            ContentValues values = new ContentValues();
//            values.put("name", "test_book" + bookId++);
//            long start = System.currentTimeMillis();
//            switch (DBType) {
//                case "sqlite":
////                android.database.sqlite.SQLiteDatabase liteDb = getLiteDb();
//                    for (int i = 0; i < 10000; i++)
//                        liteDb.insert("book", null, values);
//                    break;
//                case "sqlcipher":
////                net.sqlcipher.database.SQLiteDatabase cipherDb = getCipherDb();
//                    for (int i = 0; i < 10000; i++)
//                        cipherDb.insert("book", null, values);
//                    break;
//                case "wcdb":
////                com.tencent.wcdb.database.SQLiteDatabase wcdbDb = getWCDBDb();
//                    for (int i = 0; i < 10000; i++)
//                        wcdbDb.insert("book", null, values);
//                    break;
//            }
//            long end = System.currentTimeMillis();
//            totalTime += end - start;
//            totalNum++;
//        }
//        show(DBType + " add 1w x 5 case average time: " + (totalTime/(double)totalNum));
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
        final int queryColum = 1;
        final int threadNum = 1;
        final String type = DBType;
        final long start = System.currentTimeMillis();

        Runnable queryRunnable = new Runnable() {
            @Override
            public void run() {
                for (int n = 0; n < queryColum; n++) {
                    Cursor cursor = null;
                    int num = 0;
                    switch (type) {
                        case "sqlite":
                            cursor = liteDb.query("book", null, "id % 2 = 0", null, null, null, null);
                            break;
                        case "sqlcipher":
                            cursor = cipherDb.query("book", null, "id % 2 = 0", null, null, null, null);
                            break;
                        case "wcdb":
                            cursor = wcdbDb.query("book", null, "id % 2 = 0", null, null, null, null);
                            break;
                    }
                    while (cursor.moveToNext()){
                        num++;
                    }
                    cursor.close();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        show(type + " query " + queryColum / threadNum + " , time " + (System.currentTimeMillis() - start));
                    }
                });
            }
        };
        for (int i = 0; i < threadNum; i++) {
            new Thread(queryRunnable).start();
        }


//        int totalTime = 0;
//        int totalNum = 0;
//        for (int t = 0; t < 100; t++) {
//
//
//            long start = System.currentTimeMillis();
//            Cursor cursor = null;
//            switch (DBType) {
//                case "sqlite":
////                android.database.sqlite.SQLiteDatabase liteDb = getLiteDb();
//                    cursor = liteDb.query("book", null, null, null, null, null, null);
//                    break;
//                case "sqlcipher":
////                net.sqlcipher.database.SQLiteDatabase cipherDb = getCipherDb();
//                    cursor = cipherDb.query("book", null, null, null, null, null, null);
//                    break;
//                case "wcdb":
////                com.tencent.wcdb.database.SQLiteDatabase wcdbDb = getWCDBDb();
//                    cursor = wcdbDb.query("book", null, null, null, null, null, null);
//                    break;
//            }
//
////        //show data;
////        StringBuilder sb = new StringBuilder();
////        sb.append("--------------------" + DBType + "----------------------\n");
////        for (int i = 0; i < cursor.getColumnCount(); i++) {
////            sb.append(cursor.getColumnName(i)).append("      \t");
////        }
////        show(sb.toString());
//            int num = 0;
//            while (cursor != null && cursor.moveToNext()) {
////            sb = new StringBuilder();
////            for (int i = 0; i < cursor.getColumnCount(); i++) {
////                sb.append(cursor.getString(i)).append("      \t");
////                cursor.getString(i);
////            }
//                num++;
////            show(sb.toString());
//            }
//            cursor.close();
//            long end = System.currentTimeMillis();
//            totalTime += end - start;
//            totalNum++;
//        }
//        show(DBType + " query 5w avg time: " + (totalTime / (double) totalNum) + " num: " + totalNum);

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
