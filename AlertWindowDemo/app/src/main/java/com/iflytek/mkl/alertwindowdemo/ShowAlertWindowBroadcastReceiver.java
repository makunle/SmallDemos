package com.iflytek.mkl.alertwindowdemo;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

public class ShowAlertWindowBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ShowAlertWindowBroadcas";

    private static View floatView;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        Log.d(TAG, "onReceive: " + this.toString());
//        monitoring();

        String window_type = intent.getStringExtra("WINDOW_TYPE");
        Log.d(TAG, "onReceive: window type: " + window_type);
        switch (window_type) {
            case "float":
                floatWindow();
                break;
            case "dialog":
                alertDialog();
                break;
            case "full":
                Intent full = new Intent(context, FullWindowAlertActivity.class);
                full.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(full);
                break;
            case "half":
                Intent half = new Intent(context, HalfWindowAlertActivity.class);
                half.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(half);
                break;
            case "close":
                if (floatView != null) {
                    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    manager.removeView(floatView);
                    floatView = null;
                }
                break;
        }
    }

    private void monitoring() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : tasks) {
            Log.d(TAG, "run: " + info.topActivity);
        }
    }

    private void popupWindow() {
        View popupView = View.inflate(context, R.layout.activity_half_window_alert, null);
        PopupWindow window = new PopupWindow(popupView, 400, 600);
        window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
    }

    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("AlertDialog弹窗");
        View view = View.inflate(context, R.layout.activity_half_window_alert, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        );
        dialog.show();
    }

    private void dialogStyleActivity() {
        Intent half = new Intent(context, HalfWindowAlertActivity.class);
        context.startActivity(half);
    }

    public void floatWindow() {
        if (floatView != null) return;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER | Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        params.width = ActionBar.LayoutParams.WRAP_CONTENT;
        params.height = ActionBar.LayoutParams.WRAP_CONTENT;

        floatView = View.inflate(context, R.layout.activity_half_window_alert, null);
        final TextView tv = (TextView) floatView.findViewById(R.id.float_tv);
        floatView.findViewById(R.id.float_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.append("\nhahahahah");
            }
        });
        manager.addView(floatView, params);
    }
}
