package com.iflytek.mkl.getsmstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/10.
 */

public class SMSDynamicReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSGETTEST";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Dynamic broadcast receiver");
    }
}
