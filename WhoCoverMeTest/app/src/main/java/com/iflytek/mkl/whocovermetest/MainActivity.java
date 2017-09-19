package com.iflytek.mkl.whocovermetest;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import reflect.Reflect;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private View rootView;
    private EditText cmdInput;

    private boolean checkRun = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootView = findViewById(R.id.rootLayout);
        Log.d(TAG, "onCreate: ");

        Intent intent = new Intent(this, CheckAlertWindowService.class);
        startService(intent);

        AppOpsManager manager = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
        cmdInput = (EditText) findViewById(R.id.cmdinput);

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
//                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//                    List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(100);
//                    for (ActivityManager.RunningTaskInfo info : tasks) {
//                        Log.d(TAG, "exec: " + info.topActivity);
//                    }
//
//                    WindowManager wmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
//                    Display display = wmanager.getDefaultDisplay();

                    Rect rect = new Rect();
//                    getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//                    Log.d(TAG, "get rect: " + rect);

                    Reflect DMG = new Reflect("android.hardware.display.DisplayManagerGlobal");

                    Reflect DI = new Reflect("android.view.DisplayInfo");

                    Object instance = DMG.getMethod("getInstance").exec();

                    Display d = (Display) DMG.getMethod("getRealDisplay", int.class)
                            .with(instance).exec(Display.DEFAULT_DISPLAY);

                    Object displayInfoInstance = DMG.getMethod("getDisplayInfo", int.class)
                            .with(instance).exec(d.getDisplayId());

                    String infoString = (String) DI.getMethod("toString").with(displayInfoInstance).exec();
                    Log.d(TAG, "display info : " + infoString);


//                    Display d = DisplayManagerGlobal.getInstance().getRealDisplay(Display.DEFAULT_DISPLAY);
//                    d.getRectSize(outRect);


                    try {
                        Thread.sleep(2000);
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

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        ViewManager viewManager = getWindowManager();
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

    public void checkWindow(View view) {
        runnApp();
    }

    private void shell(String cmd) {
        try {
            Process exec = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                Log.d(TAG, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uiautomator() {
    }

    private void runnApp() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningTasks = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningTasks) {
            String list = "";
            for (int i = 0; i < info.pkgList.length; i++) {
                list += info.pkgList[i] + " ";
            }
            Log.d(TAG, "handleMessage: " + info.importance + " " + info.lru + " " + info + " " + info.processName + " " + list);
        }
    }


    AccessibilityManager.AccessibilityStateChangeListener accessiListener;

    private void windowService() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        accessiListener = new AccessibilityManager.AccessibilityStateChangeListener() {
            @Override
            public void onAccessibilityStateChanged(boolean enabled) {

            }
        };
    }

    private void reflect() {
        try {

            Class<?> clazz = Class.forName(
                    "android.view.accessibility.AccessibilityNodeInfoCache");

            Constructor<?> constructors = clazz.getDeclaredConstructor();
            Object o = constructors.newInstance();
            Method get = clazz.getDeclaredMethod("get", long.class);
            for (int i = 0; i < 100000; i++) {
                AccessibilityNodeInfo info = (AccessibilityNodeInfo) get.invoke(o, i);
                if (info != null)
                    Log.d(TAG, "reflect: " + info);
            }
            Log.d(TAG, "reflect: done");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void srufaceFlinger() {
    }

    public void runCmd(View view) {
        String cmd = cmdInput.getText().toString();
        shell(cmd);
    }

    //=======================================  onMethods ===========================================

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate: ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged: ");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostResume() {
        Log.d(TAG, "onPostResume: ");
        super.onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        Log.d(TAG, "onTitleChanged: ");
        super.onTitleChanged(title, color);
    }

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        Log.d(TAG, "onSupportActionModeStarted: ");
        super.onSupportActionModeStarted(mode);
    }

    @Override
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
        Log.d(TAG, "onSupportActionModeFinished: ");
        super.onSupportActionModeFinished(mode);
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(@NonNull ActionMode.Callback callback) {
        Log.d(TAG, "onWindowStartingSupportActionMode: ");
        return super.onWindowStartingSupportActionMode(callback);
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        Log.d(TAG, "onCreateSupportNavigateUpTaskStack: ");
        super.onCreateSupportNavigateUpTaskStack(builder);
    }

    @Override
    public void onPrepareSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        Log.d(TAG, "onPrepareSupportNavigateUpTaskStack: ");
        super.onPrepareSupportNavigateUpTaskStack(builder);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp: ");
        return super.onSupportNavigateUp();
    }

    @Override
    public void onContentChanged() {
        Log.d(TAG, "onContentChanged: ");
        super.onContentChanged();
    }

    @Override
    public void onSupportContentChanged() {
        Log.d(TAG, "onSupportContentChanged: ");
        super.onSupportContentChanged();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        Log.d(TAG, "onMenuOpened: ");
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        Log.d(TAG, "onPanelClosed: ");
        super.onPanelClosed(featureId, menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
    }
}
