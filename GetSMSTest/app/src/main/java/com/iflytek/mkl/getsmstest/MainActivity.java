package com.iflytek.mkl.getsmstest;

import android.content.ContentResolver;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SMSGETTEST";

    SMSDynamicReceiver dynamicReceiver;
    SMSObserver smsReceiveObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dynamicReceiver = new SMSDynamicReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
        registerReceiver(dynamicReceiver, filter);

        smsReceiveObserver = new SMSObserver(new Handler(), this);
        getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true,
                smsReceiveObserver
        );

        testRead();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
        getContentResolver().unregisterContentObserver(smsReceiveObserver);
    }


    private void testReadDb(){
        String db = "/data/user_de/0/com.android.providers.telephony/databases/mmssms.db";
        SQLiteDatabase sb = SQLiteDatabase.openOrCreateDatabase(db, null);
        Log.d(TAG, "testReadDb: open db " + sb.toString());
    }

    private void testRead(){
        Log.d(TAG, "start: ---------------------------------------------------------------------\n");
        ContentResolver contentObserver = getContentResolver();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = contentObserver.query(uri, null, "source is null", null, "_id desc limit 30");
        if(cursor != null){
            while (cursor.moveToNext()){
                Log.d(TAG, "on change: ---------------------------------------------------------------------\n");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d(TAG, cursor.getColumnName(i) + "   =============>" + cursor.getString(i));
                }
            }
        }
        Log.d(TAG, "end: ---------------------------------------------------------------------\n");
    }
}
