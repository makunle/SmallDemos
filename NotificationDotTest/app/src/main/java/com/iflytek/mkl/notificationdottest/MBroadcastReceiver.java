package com.iflytek.mkl.notificationdottest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MNotification.init(context);
        MNotification.showNotificaiton(1);
    }
}
