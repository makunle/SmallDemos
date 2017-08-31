package com.iflytek.mkl.imetextchangetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText et1 = (EditText) findViewById(R.id.et1);
    }
}
