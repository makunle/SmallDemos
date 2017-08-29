package com.iflytek.mkl.accessibilitywithwechatpractice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

public class ShowWindowActivity extends AppCompatActivity {

    private static final String TAG = "ShowWindowActivity";

    private static final String MAN = "MAN";
    private static final String SAY = "SAY";

    private static final String NEED_SAY = "NEED_SAY";

    private TextView manTv;
    private TextView sayTv;

    private ScrollView scrollView;

    private TextToSpeech textToSpeech;

    private ImageButton playPauseBtn;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private boolean needSpeak;

    public static void startMe(Context context, String man, String say) {
        Intent intent = new Intent(context, ShowWindowActivity.class);
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

        if (needSpeak) {
            speak();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        needSpeak = preferences.getBoolean(NEED_SAY, false);
        if (needSpeak) {
            playPauseBtn.setBackground(getResources().getDrawable(R.drawable.ic_pause));
        } else {
            playPauseBtn.setBackground(getResources().getDrawable(R.drawable.ic_play));
        }
    }

    private void speak() {
        if (manTv == null || sayTv == null) return;
        final String man = manTv.getText().toString();
        final String say = sayTv.getText().toString();
        if (TextUtils.isEmpty(man) || TextUtils.isEmpty(say)) return;

        stopSpeak();

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.speak("    " + man + "说" + say, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }

    private void stopSpeak() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
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
        if (textToSpeech != null) {
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

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

//        findViewById(R.id.permission_page).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                startActivity(intent);
//                moveTaskToBack(true);
//            }
//        });

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setOnTouchListener(scrollViewTouchListener);

        playPauseBtn = (ImageButton) findViewById(R.id.play_pause);
        playPauseBtn.setOnClickListener(playPauseListener);

        String man = getIntent().getStringExtra(MAN);
        String say = getIntent().getStringExtra(SAY);

        manTv = (TextView) findViewById(R.id.man);
        sayTv = (TextView) findViewById(R.id.say);
        show(man, say);
    }

    /**
     * 在ScrollView上双击时隐藏
     */
    View.OnTouchListener scrollViewTouchListener = new View.OnTouchListener() {
        private float px;
        private float py;
        private int clickNum = 0;
        private boolean needAddNum;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    px = event.getX();
                    py = event.getY();
                    needAddNum = true;
                    break;
                case MotionEvent.ACTION_UP:
                    if (needAddNum) {
                        clickNum++;
                        if (clickNum >= 2) {
                            hideWindow();
                            clickNum = 0;
                            return true;
                        }
                    } else {
                        clickNum = 0;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(event.getX() - px) + Math.abs(event.getY() - py) > 10) {
                        needAddNum = false;
                    }
                    break;
            }
            return false;
        }
    };


    /**
     * play_pause按钮
     */
    View.OnClickListener playPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (needSpeak) {
                v.setBackground(getResources().getDrawable(R.drawable.ic_play));
                needSpeak = false;
                stopSpeak();
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.ic_pause));
                needSpeak = true;
                speak();
            }
            editor.putBoolean(NEED_SAY, needSpeak).commit();
        }
    };

    @Override
    public void onBackPressed() {
        hideWindow();
    }

    private void hideWindow(){
        moveTaskToBack(true);
        manTv.setText("");
        sayTv.setText("");
    }
}
