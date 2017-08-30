package com.iflytek.mkl.imetextchangetest;

import android.graphics.Color;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.IBinder;
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
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by makunle on 2017/8/30.
 */

public class MIMEService extends InputMethodService implements View.OnClickListener {

    private static final String TAG = "MIMEService";
    private static final int CURSOR_UPDATE = 7;

    /**
     * 创建输入法键盘view
     *
     * @return
     */
    @Override
    public View onCreateInputView() {
        View view = getLayoutInflater().inflate(R.layout.input_view, null);
        for (int i = R.id.btn1; i <= R.id.btn_func1; i++) {
            view.findViewById(i).setOnClickListener(this);
        }
        return view;
    }

    /**
     * 键盘按键点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_func1:
                func1();
                break;
            default:
                InputConnection connection = getInputConnection();
                if (connection != null) {
                    connection.commitText(((Button) view).getText() + "", 1);
                }
                break;
        }
    }

    /**
     * 改变指定位置字的内容和颜色、下划线
     * 考虑需要使用CustomSpan使得下划线和字体颜色不同，另外考虑改变下划线线型【未完成】
     */
    private void func1() {
        InputConnection connection = getInputConnection();

        //找寻最后一个”今天“
        CharSequence allText = getAllText(connection);
        Matcher matcher = Pattern.compile("今天").matcher(allText);
        int start = -1, end = 0;
        while (matcher.find(start+1)) {
            start = matcher.start();
            end = matcher.end();
        }
        if(end == 0) return;

        //替换为”后天“
        connection.setComposingRegion(start, end);
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
        connection.commitText(ss, 1);
    }

    /**
     * 输入法开始输入时回调
     *
     * @param attribute
     * @param restarting
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private InputConnection getInputConnection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getCurrentInputConnection().requestCursorUpdates(CURSOR_UPDATE);
        }
        return getCurrentInputConnection();
    }

    /**
     * 获取当前输入框内所有字符
     *
     * @param ic
     * @return
     */
    private CharSequence getAllText(InputConnection ic) {
        if (ic == null) return null;
        ic.performContextMenuAction(android.R.id.selectAll);
        CharSequence data = ic.getSelectedText(0);
        if (data == null) return null;
        ic.setSelection(data.length(), data.length());
        return data;
    }

    /**
     * 光标所在字符位置的变化
     */
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        Log.d(TAG, "onUpdateSelection: " + oldSelStart + " " + oldSelEnd + " " + newSelStart + " " + newSelEnd + " " + candidatesStart + " " + candidatesEnd);
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        getInputConnection();
    }

    @Override
    public void onUpdateExtractingViews(EditorInfo ei) {
        Log.d(TAG, "onUpdateExtractingViews: ");
    }

    /**
     * 光标在屏幕上位置的改变
     */
    @Override
    public void onUpdateCursor(Rect newCursor) {
        Log.d(TAG, "onUpdateCursor: ");
    }

    /**
     * 光标在屏幕上位置的改变
     */
    @Override
    public void onUpdateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) {
        super.onUpdateCursorAnchorInfo(cursorAnchorInfo);
        Log.d(TAG, "onUpdateCursorAnchorInfo: ");
    }

    @Override
    public void onUpdateExtractingVisibility(EditorInfo ei) {
        super.onUpdateExtractingVisibility(ei);
        Log.d(TAG, "onUpdateExtractingVisibility: ");
    }

    @Override
    public void onUpdateExtractedText(int token, ExtractedText text) {
        super.onUpdateExtractedText(token, text);
        Log.d(TAG, "onUpdateExtractedText: ");
    }
}
