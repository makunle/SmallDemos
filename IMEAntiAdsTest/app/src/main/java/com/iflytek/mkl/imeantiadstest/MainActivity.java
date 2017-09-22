package com.iflytek.mkl.imeantiadstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.iflytek.mkl.advertise.detector.AppAnalysisTool;

import java.io.IOException;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.b1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dualiad = AppAnalysisTool.a("dualiad", "043A48E4A53FADE6A11CE763588DCEA2");
                Log.d(TAG, "onClick: " + dualiad);


                Properties properties = new Properties();
                try {
                    properties.load(getResources().openRawResource(R.raw.adlibrary));
                    for (int i = 0; i < properties.size(); i++) {
                        Log.d(TAG, "" + properties.getProperty("" + i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
