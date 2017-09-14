package com.iflytek.mkl.imelayouttest;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makunle on 2017/9/14.
 */

public class IMEService extends InputMethodService implements OnClickListener{

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
                if (layout_num.getVisibility() == View.VISIBLE) {
                    layout_num.setVisibility(View.GONE);
                    layout_abc2.setVisibility(View.GONE);
                } else {
                    layout_num.setVisibility(View.VISIBLE);
                    layout_abc2.setVisibility(View.VISIBLE);
                }
                break;
            default:
                TextView editText = (TextView) v;
                candidateTextView.append(editText.getText(), 0, 1);
                break;
        }
    }

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
        return view;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d(TAG, "onCreateCandidatesView: ");
        setCandidatesViewShown(true);
        View v = getLayoutInflater().inflate(R.layout.candidate, null);
        candidateTextView = (TextView) v.findViewById(R.id.candidate);
        candidateTextView.setOnClickListener(this);
        v.findViewById(R.id.hide).setOnClickListener(this);
        v.findViewById(R.id.change_layout).setOnClickListener(this);
        return v;
    }
}
