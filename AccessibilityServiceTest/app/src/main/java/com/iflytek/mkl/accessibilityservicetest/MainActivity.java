package com.iflytek.mkl.accessibilityservicetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED;

public class MainActivity extends AppCompatActivity {

    TextView output;
    CheckBox showAll;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("tempset", MODE_PRIVATE);
        editor = preferences.edit();

        findViewById(R.id.page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        Button say = (Button) findViewById(R.id.say);
        say.announceForAccessibility("这是一个测试");
        say.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "这是一个测试", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
            }
        });

        output = (TextView) findViewById(R.id.output);
        showAll = (CheckBox) findViewById(R.id.show_view_info);

        showAll.setChecked(preferences.getBoolean("show_all", true));

        showAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("show_all", isChecked);
                editor.commit();
            }
        });

        findViewById(R.id.manager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccessibilityManager service = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
                if (service != null) {
                    service.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
                        @Override
                        public void onAccessibilityStateChanged(boolean enabled) {
                            Util.show(enabled + "");
                        }
                    });
                }
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void show(String msg) {
        if (output != null && !TextUtils.isEmpty(msg)) {
            output.append(msg + "\n");
        }
    }
}
