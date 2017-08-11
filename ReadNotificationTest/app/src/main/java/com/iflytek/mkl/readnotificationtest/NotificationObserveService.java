package com.iflytek.mkl.readnotificationtest;

import android.app.Notification;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

public class NotificationObserveService extends NotificationListenerService {

    private static final String TAG = "NotificationObserveServ";

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return super.onBind(intent);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "onListenerConnected");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d(TAG, "onListenerDisconnected");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Bundle bundle = sbn.getNotification().extras;
            String text = bundle.getString("android.text");
            Log.d(TAG, "onNotificationPosted: Text: " + text);
            Log.d(TAG, "onNotificationPosted: " + bundle.toString());
        }
        super.onNotificationPosted(sbn);
        String pkgName = sbn.getPackageName();
        String notiText = sbn.getNotification().toString();
        String text = (String) sbn.getNotification().tickerText;
        Log.d(TAG, "pkgName ===== " + pkgName);
        Log.d(TAG, "notiText ===== " + notiText);
        Log.d(TAG, "text ===== " + text);
        Toast.makeText(this, "text from nitificaiton: " + text, Toast.LENGTH_SHORT).show();
    }
}
