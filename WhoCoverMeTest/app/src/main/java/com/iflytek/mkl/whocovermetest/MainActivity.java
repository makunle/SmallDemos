package com.iflytek.mkl.whocovermetest;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private View rootView;

    private boolean checkRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.rootLayout);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        checkRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (checkRun) {
//                    visibleRect(rootView);
//                    Log.d(TAG, " ");
                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(100);
                    for (ActivityManager.RunningTaskInfo info : tasks) {
                        Log.d(TAG, "run: " + info.topActivity);
                    }

                    WindowManager wmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = wmanager.getDefaultDisplay();
                    registerDis
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void visibleRect(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        Log.d(TAG, "rect: " + rect.toString());
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                visibleRect(vg.getChildAt(i));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.AppTask> appTasks = manager.getAppTasks();
            for (ActivityManager.AppTask task : appTasks) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d(TAG, "onPause: " + task.getTaskInfo().topActivity);
                }
            }
        }
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        checkRun = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + keyCode + " 　　" + event);
        return super.onKeyDown(keyCode, event);
    }

    public void fullWindowAlert(View view) {
        sendWindowPopBroadcast("full");
    }

    public void closeFloatWindow(View view) {
        sendWindowPopBroadcast("close");
    }

    public void alertDialog(View view) {
        sendWindowPopBroadcast("dialog");
    }


    public void floatWindow(View view) {
        sendWindowPopBroadcast("float");
    }

    private void sendWindowPopBroadcast(String type) {
        Intent intent = new Intent("com.iflytek.mkl.broadcast.SHOW_ALERT_WINDOW");
        intent.putExtra("WINDOW_TYPE", type);
        sendBroadcast(intent);
    }

    public void halfWindowAlert(View view) {
        sendWindowPopBroadcast("half");
    }
}
