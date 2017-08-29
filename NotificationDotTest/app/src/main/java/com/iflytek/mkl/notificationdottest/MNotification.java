package com.iflytek.mkl.notificationdottest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static android.app.Notification.PRIORITY_MAX;

/**
 * Created by makunle on 2017/8/29.
 */

public class MNotification {

    private static final String TAG = "MNotification";

    public static Map<Integer, Integer> notifyNum = new HashMap<>();

    public static Context context;

    public static void init(Context context) {
        MNotification.context = context;
    }

    public static void showNotificaiton(int id) {
        int num = 0;
        if (notifyNum.get(id) != null) {
            num = notifyNum.get(id);
        }
        num = num % 20 + 1;
        notifyNum.put(id, num);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("I am title")
                .setContentText("I am text " + num)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.small_ico)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_large));

        Intent intent = new Intent();
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        Notification notification = builder.build();

        setBadgeForMiui(notification, num, "V8");
//        setBadgeForHuawei(num);

        manager.notify(1, notification);
    }


    private static void setBadgeForMiui(Notification notification, int num, String versionName) {
        Log.d(TAG, "num: " + num);
        if (versionName.equals("V6")) {
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name", context.getPackageName() + "/" + MainActivity.class.getName());
            localIntent.putExtra("android.intent.extra.update_application_message_text", num);
            context.sendBroadcast(localIntent);
        } else {
            try {
                Field field = notification.getClass().getDeclaredField("extraNotification");
                Object extraNotification = field.get(notification);
                Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
                method.invoke(extraNotification, num);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void setBadgeForHuawei(int count) {
        Bundle localBundle = new Bundle();
        localBundle.putString("package", context.getPackageName());
        localBundle.putString("class", MainActivity.class.getName());
        localBundle.putInt("badgenumber", count);
        context.getContentResolver().call(Uri.
                parse("content://com.huawei.android.launcher.settings/badge/"),
                "change_badge", null, localBundle);
    }

    public static boolean isSupportedByVersion() {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo("com.huawei.android.launcher", 0);
            if (info.versionCode >= 63029) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return false;
        }
    }

    private static void setBadgeOfSumsung(Context context, int count) {
        // 获取你当前的应用
        String launcherClassName = MainActivity.class.getName();
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }
}
