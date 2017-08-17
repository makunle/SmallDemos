package com.iflytek.mkl.accessibilitywithwechatpractice;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String MAN = "MAN";
    private static final String SAY = "SAY";

    private TextView manTv;
    private TextView sayTv;

    public static void startMe(Context context, String man, String say) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAN, man);
        intent.putExtra(SAY, say);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String man = intent.getStringExtra(MAN);
        String say = intent.getStringExtra(SAY);
        Log.d(TAG, "onNewIntent  man: " + man);
        Log.d(TAG, "onNewIntent  say: " + say);
        manTv.setText(man);
        sayTv.setText(say);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.permission_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });

        String man = getIntent().getStringExtra(MAN);
        String say = getIntent().getStringExtra(SAY);

        manTv = (TextView) findViewById(R.id.man);
        sayTv = (TextView) findViewById(R.id.say);

        manTv.setText(man);
        sayTv.setText(say);

        Log.d(TAG, "onCreate  man: " + man);
        Log.d(TAG, "onCreate  say: " + say);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
