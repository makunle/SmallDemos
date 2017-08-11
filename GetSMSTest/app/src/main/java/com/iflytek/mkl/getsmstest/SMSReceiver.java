package com.iflytek.mkl.getsmstest;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSGETTEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Static broadcast receive");

    }
}
