package com.iflytek.mkl.inputmethodmonitor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by makunle on 2017/9/11.
 */

public class MonitorService extends Service {

    private static final String TAG = "MonitorService";

    private static View floatView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        floatView = new FloatKeyboardMonitorView(getApplicationContext());
    }
}
