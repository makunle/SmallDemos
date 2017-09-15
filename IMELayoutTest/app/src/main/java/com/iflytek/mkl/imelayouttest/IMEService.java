package com.iflytek.mkl.imelayouttest;

import android.app.Dialog;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makunle on 2017/9/14.
 */

public class IMEService extends InputMethodService implements OnClickListener {

    private static final String TAG = "IMEService";

    private List<TextView> keys = new ArrayList<>();

    private boolean upperCase = false;


    View layout_abc1;
    View layout_abc2;
    View layout_abc3;
    View layout_num;

    LinearLayout keyboardRoot;

    private TextView candidateTextView;

    @Override
    public void onClick(View v) {
        InputConnection inputConnection = getCurrentInputConnection();

        switch (v.getId()) {
            case R.id.hide:
                hideWindow();
                break;
            case R.id.caseChange:
                for (TextView key : keys) {
                    if (!upperCase) key.setText(key.getText().toString().toUpperCase());
                    else key.setText(key.getText().toString().toLowerCase());
                }
                upperCase = !upperCase;
                break;
            case R.id.delete:
                inputConnection.deleteSurroundingText(1, 0);
                break;
            case R.id.candidate:
                inputConnection.commitText(candidateTextView.getText().toString(), 1);
                candidateTextView.setText("");
                break;
            case R.id.change_layout:

                break;
            default:
                TextView editText = (TextView) v;
                candidateTextView.append(editText.getText(), 0, 1);

                if (editText.getText().equals("a")) {
                    if (layout_num.getVisibility() == View.VISIBLE) {
                        layout_num.setVisibility(View.GONE);
//                    layout_abc2.setVisibility(View.GONE);
//                        mLinearLayout.fillLayout.getLayoutParams().height = 1413;

                    } else {
                        layout_num.setVisibility(View.VISIBLE);
//                    layout_abc2.setVisibility(View.VISIBLE);
//                        mLinearLayout.fillLayout.getLayoutParams().height = 1269;
//                        mLinearLayout.getLayoutParams().height = 591;
                    }
                    boolean b = getWindow().getWindow().getDecorView() == null;
                    Log.d(TAG, "onClick: decor view is null: " + b);
                    View decorView = getWindow().getWindow().getDecorView();
                    decorView.setBackgroundColor(Color.RED);
                }
                break;
        }
    }

    MLinearLayout mLinearLayout;

    @Override
    public View onCreateInputView() {
        Log.d(TAG, "onCreateInputView: ");
        View view = getLayoutInflater().inflate(R.layout.keyboard, null);
        for (int i = R.id.n1; i <= R.id.delete; i++) {
            if (view.findViewById(i) instanceof TextView == false) continue;
            TextView key = (TextView) view.findViewById(i);
            key.setOnClickListener(this);
            keys.add(key);
        }

        layout_abc1 = view.findViewById(R.id.layout_abc1);
        layout_abc2 = view.findViewById(R.id.layout_abc2);
        layout_abc3 = view.findViewById(R.id.layout_abc3);
        layout_num = view.findViewById(R.id.layout_num);

        keyboardRoot = (LinearLayout) view.findViewById(R.id.kb_root);
        keyboardRoot.setLayoutAnimation(new LayoutAnimationController(new ScaleAnimation(0, 100, 0, 100), 2000));

        mLinearLayout = (MLinearLayout) view.findViewById(R.id.kb_root);
        getWindow().getWindow().setWindowAnimations(R.style.noAnimTheme);
        WindowManager.LayoutParams params = getWindow().getWindow().getAttributes();
        getWindow().getWindow().getDecorView().setBackgroundColor(Color.BLUE);

        return view;

//        drawView = (MDrawView) getLayoutInflater().inflate(R.layout.use_drawview, null);
//        ViewGroup.LayoutParams params = drawView.getLayoutParams();
//        Log.d(TAG, "onCreateInputView: params is null: " + (params == null));
//        return drawView;

//        View lv = getLayoutInflater().inflate(R.layout.use_mlinearlayout, null);
//        final Button b1 = (Button) lv.findViewById(R.id.b1);
//        final Button b2 = (Button) lv.findViewById(R.id.b2);
//        final Button b3 = (Button) lv.findViewById(R.id.b3);
//        lv.findViewById(R.id.b2).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(b1.getVisibility() == View.VISIBLE){
//                    b1.setVisibility(View.GONE);
//                    b3.setVisibility(View.GONE);
//                }else{
//                    b1.setVisibility(View.VISIBLE);
//                    b3.setVisibility(View.VISIBLE);
//                }
//                getWindow().dismiss();
//                getWindow().show();
//            }
//        });
//
//        Dialog dialog = getWindow();
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.windowAnimations = R.style.noAnimTheme;

//        return lv;
    }


    FrameLayout fullLayout;

    @Override
    public View onCreateCandidatesView() {
        Log.d(TAG, "onCreateCandidatesView: ");
        View v = getLayoutInflater().inflate(R.layout.candidate, null);
        candidateTextView = (TextView) v.findViewById(R.id.candidate);
        candidateTextView.setOnClickListener(this);
        v.findViewById(R.id.hide).setOnClickListener(this);
        v.findViewById(R.id.change_layout).setOnClickListener(this);
//        return v;

        View transparentView = getLayoutInflater().inflate(R.layout.transparent_candidate, null);
        fullLayout = (FrameLayout) transparentView.findViewById(R.id.full_layout);

        mLinearLayout.fillLayout = fullLayout;

        getWindow().getWindow().getAttributes().gravity = Gravity.TOP;
        return transparentView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        Log.d(TAG, "onStartInputView: ");
    }
}
