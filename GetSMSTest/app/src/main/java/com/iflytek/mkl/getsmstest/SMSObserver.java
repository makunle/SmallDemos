package com.iflytek.mkl.getsmstest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/10.
 */

public class SMSObserver extends ContentObserver {
    private static final String TAG = "SMSGETTEST";

    Context context;

    public SMSObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Log.d(TAG, "on change " + uri.toString());
        if (selfChange) return;
        if(uri.toString().contains("inbox")) return;

        ContentResolver contentObserver = context.getContentResolver();
        Cursor cursor = contentObserver.query(uri, null, null, null, null);
        if(cursor != null){
            if(cursor.moveToNext())
            Log.d(TAG, "on change: " + cursor.getString(cursor.getColumnIndex("body")));
            else
                Log.d(TAG, "onChange: 0 msg");
        }
        Log.d(TAG, "is null? " + (cursor == null));
    }
}
