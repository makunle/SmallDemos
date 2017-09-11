package com.iflytek.mkl.whocovermetest;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CheckAlertWindowService extends Service {

    private static final String TAG = "CheckAlertWindowService";

    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            check_handler.sendMessage(message);
        }
    };

    public CheckAlertWindowService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.schedule(task, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    Handler check_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
//                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(10);
//                for(ActivityManager.RunningTaskInfo info : runningTasks){
//                    Log.d(TAG, "handleMessage: " + info.topActivity);
//                }

//                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = manager.getRunningAppProcesses();
//                for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses){
//                    Log.d(TAG, "handleMessage: " + info.processName);
//                    for (int i = 0; i < info.pkgList.length; i++) {
//                        Log.d(TAG, "    handleMessage: " + info.pkgList[i]);
//                    }
//                }

//                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//                Log.d(TAG, "pkg:"+cn.getPackageName());
//                Log.d(TAG, "cls:"+cn.getClassName());
//                Log.d(TAG, " ");
//                runnApp();
//                Log.d(TAG, " ");
            }
            super.handleMessage(msg);
        }
    };

    private void runnApp() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningTasks = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningTasks) {

            String list = "";
            for (int i = 0; i < info.pkgList.length; i++) {
                list += info.pkgList[i] + " ";
            }
            if(list.contains("android.")) continue;
            Log.d(TAG, "handleMessage: " + info.importance + " " + info.lru + " " + info +" " + info.processName + " " + list
                + info.importanceReasonComponent + " " + info.importanceReasonCode + " " + info.importanceReasonPid + " ");
        }
    }
}
