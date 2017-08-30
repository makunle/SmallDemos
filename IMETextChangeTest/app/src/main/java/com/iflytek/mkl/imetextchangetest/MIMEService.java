package com.iflytek.mkl.imetextchangetest;

import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.support.v7.widget.AppCompatEditText;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by makunle on 2017/8/30.
 */

public class MIMEService extends InputMethodService implements View.OnClickListener {

    private static final String TAG = "MIMEService";
    private static final int CURSOR_UPDATE = InputConnection.CURSOR_UPDATE_MONITOR | InputConnection.CURSOR_UPDATE_IMMEDIATE;

    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.input_view, null);
        for (int i = R.id.btn1; i <= R.id.btn_func1; i++) {
            view.findViewById(i).setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_func1:
                func1();
                break;
            default:
                InputConnection connection = getCurrentInputConnection();
                if (connection != null) {
                    connection.commitText(((Button) view).getText() + "", 1);
                }
                break;
        }
    }

    private void func1() {
        InputConnection connection = getCurrentInputConnection();
        connection.setComposingRegion(7, 9);
        SpannableString ss = new SpannableString("后天");
        ss.setSpan(new ForegroundColorSpan(Color.rgb(58, 132, 255)), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
            }

            @Override
            public void onClick(View view) {
                AppCompatEditText et = (AppCompatEditText) view;
                Log.d(TAG, "onClick: " + et.getText());
            }
        }, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new UnderlineSpan(), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        connection.setComposingText(ss, 1);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        getCurrentInputConnection().requestCursorUpdates(CURSOR_UPDATE);

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void onUpdateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) {
        super.onUpdateCursorAnchorInfo(cursorAnchorInfo);
        Log.d(TAG, "onUpdateCursorAnchorInfo: ");
    }

    private CharSequence getAllText(InputConnection ic) {
        if (ic == null) return null;
        ic.performContextMenuAction(android.R.id.selectAll);
        CharSequence data = ic.getSelectedText(0);
        if (data == null) return null;
        ic.setSelection(data.length(), data.length());
        return data;
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        Log.d(TAG, "onUpdateSelection: " + oldSelStart + " " + oldSelEnd + " " + newSelStart + " " + newSelEnd + " " + candidatesStart + " " + candidatesEnd);
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
    }

}
