package com.iflytek.mkl.permissionpageopen;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void PACKAGE_USAGE_STATS_page(View view) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    public void changeViewSize(View view) {
        View root = findViewById(R.id.root);
        ViewGroup.LayoutParams params = root.getLayoutParams();
        if(params.height > 800){
            params.height = 700;
        }else{
            params.height = 900;
        }
        root.setLayoutParams(params);
    }
}
