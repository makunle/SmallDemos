package com.iflytek.mkl.inputmethodmonitor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;


/**
 * 通过通知栏判断输入法的开启与关闭，使用AccessibilityService也可以监听
 * 注：需用户开启两个及以上输入法
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class IMEMonitorByNLS extends NotificationListenerService {
    private static final String TAG = "IMEMonitorByNLS";
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification nf = sbn.getNotification();
        Log.d(TAG, "onNotificationPosted: " + sbn.getPackageName());
        if("android".equals(sbn.getPackageName())){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if(sbn.getNotification().extras != null){
                    Bundle bundle = sbn.getNotification().extras;
                    String title = bundle.getString("android.title");
                    String ime = bundle.getString("android.text");
                    if(title.contains("键盘") || title.contains("input method") || title.contains("输入法")){
                        if(ime.contains("输入法")){
                            Log.d(TAG, "onNotificationPosted: " + ime + "  显示");
                        }
                    }
                }
            }

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d(TAG, "onNotificationRemoved: ");
    }
}
