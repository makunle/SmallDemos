package com.iflytek.mkl.inputmethodmonitor.unuse;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.iflytek.mkl.inputmethodmonitor.FloatKeyboardMonitor;

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

    FloatKeyboardMonitor.KeyboardStateListener listener;

    @Override
    public void onCreate() {
        super.onCreate();
        listener = new FloatKeyboardMonitor.KeyboardStateListener() {
            @Override
            public void onKeyboardShow() {
                Log.d(TAG, "onKeyboardShow: service");
            }

            @Override
            public void onKeyboardHide() {
                Log.d(TAG, "onKeyboardHide: service");
            }
        };
        FloatKeyboardMonitor.register(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatKeyboardMonitor.unregister(listener);
    }
}
