package com.iflytek.mkl.readnotificationtest;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

public class NotificationObserveService extends NotificationListenerService {

    private static final String TAG = "NotificationObserveServ";

    private MyBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return super.onBind(intent);
//        return binder;
    }



    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
//        EventBus.getDefault().register(this);
        Log.d(TAG, "onListenerConnected");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
//        EventBus.getDefault().unregister(this);
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
        super.onNotificationPosted(sbn);
        EventBus.getDefault().post(new Msg(sbn));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    private Activity activity;

    public class MyBinder extends Binder{

        public void setActivity(Activity activity){
            NotificationObserveService.this.activity = activity;
        }
    }

    public static class Msg{
        StatusBarNotification sbn;

        public Msg(StatusBarNotification sbn) {
            this.sbn = sbn;
        }
    }
}
