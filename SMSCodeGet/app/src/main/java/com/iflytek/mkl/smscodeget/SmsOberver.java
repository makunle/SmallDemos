package com.iflytek.mkl.smscodeget;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Toast;

import com.iflytek.mkl.smscodeget.Util.GetCode;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SmsOberver extends ContentObserver {
    private static final String TAG = "smscode";

    private Context context;

    public SmsOberver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        if (Build.VERSION.SDK_INT >= 16) return;
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://sms/inbox"), null, null, null, "date desc limit 3"
        );
        cursor.moveToNext();
        String id = cursor.getString(cursor.getColumnIndex("_id"));
        onChange(selfChange, Uri.parse("content://sms/"+id));
        Log.d(TAG, "onChange with one paramater");
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Cursor cursor = context.getContentResolver().query(
                uri, null, null, null, null
        );
        cursor.moveToNext();
        String body = cursor.getString(cursor.getColumnIndex("body"));

        Log.d(TAG, "get sms body by observer: " + "code:"+ GetCode.Get(body)+"  body:" + body);
        Toast.makeText(context, "get sms body by observer: " + body, Toast.LENGTH_SHORT).show();

        String id = cursor.getString(cursor.getColumnIndex("_id"));
        Log.d(TAG, "onChange: sms id: " + id);

        String status = cursor.getString(cursor.getColumnIndex("status"));
        Log.d(TAG, "onChange: sms status: " + status);

        String read = cursor.getString(cursor.getColumnIndex("read"));
        Log.d(TAG, "onChange: sms read: " + read);

//        ContentValues values = new ContentValues();
//        values.put("read", 1);
//        context.getContentResolver().update(Uri.parse("content://sms/" + id), values, null, null);
//        context.getContentResolver().delete(Uri.parse("content://sms/"+id), null, null);

        cursor.close();

//        super.onChange(selfChange);
    }
}
