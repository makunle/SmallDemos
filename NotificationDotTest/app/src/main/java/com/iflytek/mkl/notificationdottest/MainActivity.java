package com.iflytek.mkl.notificationdottest;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        if(Build.VERSION.SDK_INT >= 21){
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        Intent intent = new Intent("com.iflytek.mkl.broadcast.LOG_BROADCAST");
        sendBroadcast(intent);
        finish();
    }
}
