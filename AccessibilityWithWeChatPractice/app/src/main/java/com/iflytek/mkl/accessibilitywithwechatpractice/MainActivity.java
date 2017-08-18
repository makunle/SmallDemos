package com.iflytek.mkl.accessibilitywithwechatpractice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String MAN = "MAN";
    private static final String SAY = "SAY";

    private TextView manTv;
    private TextView sayTv;

    private TextToSpeech textToSpeech;

    public static void startMe(Context context, String man, String say) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAN, man);
        intent.putExtra(SAY, say);
        context.startActivity(intent);
    }

    private void show(final String man, final String say) {
        if (man == null || say == null) return;
        if (manTv != null && sayTv != null) {
            manTv.setText(man);
            sayTv.setText(say);
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();;
            textToSpeech = null;
        }
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    textToSpeech.speak("    "+man + "说" + say, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String man = intent.getStringExtra(MAN);
        String say = intent.getStringExtra(SAY);
        show(man, say);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
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
                moveTaskToBack(true);
            }
        });

        String man = getIntent().getStringExtra(MAN);
        String say = getIntent().getStringExtra(SAY);

        manTv = (TextView) findViewById(R.id.man);
        sayTv = (TextView) findViewById(R.id.say);
        show(man, say);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
