package com.iflytek.mkl.inputmethodmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.mkl.inputmethodmonitor.unuse.MonitorService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static View floatView;

    private FloatKeyboardMonitor.KeyboardStateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, MonitorService.class);
//        startService(intent);

//        floatView = new FloatKeyboardMonitorView(this);

        final TextView et = (TextView) findViewById(R.id.textView);

        FloatKeyboardMonitor.init(this);
        listener = new FloatKeyboardMonitor.KeyboardStateListener() {
            @Override
            public void onKeyboardShow() {
                Log.d(TAG, "onKeyboardShow");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et.setText("show");
                    }
                });
            }

            @Override
            public void onKeyboardHide() {
                Log.d(TAG, "onKeyboardHide");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et.setText("hide");
                    }
                });
            }
        };
        FloatKeyboardMonitor.register(listener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatKeyboardMonitor.unregister(listener);
    }
}
