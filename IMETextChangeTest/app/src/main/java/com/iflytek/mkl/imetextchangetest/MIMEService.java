package com.iflytek.mkl.imetextchangetest;

import android.app.PendingIntent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.AbstractInputMethodService;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.AppCompatEditText;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.EasyEditSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.SuggestionSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.Button;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.inputmethod.InputConnection.GET_EXTRACTED_TEXT_MONITOR;

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
        for (int i = R.id.btn1; i <= R.id.btn_func2; i++) {
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
            case R.id.btn_func2:
                func2();
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
        while (matcher.find(start + 1)) {
            start = matcher.start();
            end = matcher.end();
        }
        if (end == 0) return;

        //替换为”后天“
        String newText = "明天";
        String[] suggest = {"今天", "明天", "后天"};
        SpannableStringBuilder ss = new SpannableStringBuilder();
//
//        ss.append(newText);
//        ss.setSpan(new SuggestionSpan(this, suggest, SuggestionSpan.FLAG_EASY_CORRECT), 0,
//                ss.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE|Spanned.SPAN_COMPOSING);
//        ss.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        connection.setComposingRegion(start, end);
//        connection.setComposingText(ss, 1);

        connection.setComposingRegion(start, end);
        connection.setComposingText(newText, 1);

        allText = getAllText(connection);
        ss.append(allText);
        ss.setSpan(new SuggestionSpan(getApplicationContext(), suggest, SuggestionSpan.FLAG_EASY_CORRECT),
                start, start + newText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING);
        ss.setSpan(new ForegroundColorSpan(Color.rgb(23, 123, 189)), start, start + newText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        connection.setComposingRegion(0, allText.length());
        connection.setComposingText(ss, 1);
    }

    /**
     * finish composing
     */
    private void func2() {
        getInputConnection().finishComposingText();
    }

    /**
     * 输入法开始输入时回调
     *
     * @param attribute
     * @param restarting
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        Log.d(TAG, "onStartInput: ");
        super.onStartInput(attribute, restarting);
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        Log.d(TAG, "onStartInput: editorinof: " + attribute.fieldName + " " + attribute.fieldId + " " + attribute.packageName);

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
        ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
        if (et == null) return null;
        return et.text;
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
    public void onViewClicked(boolean focusChanged) {
        super.onViewClicked(focusChanged);
        InputConnection ic = getCurrentInputConnection();
        ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), GET_EXTRACTED_TEXT_MONITOR);
        if (et != null)
            Log.d(TAG, "onViewClicked: " + et.partialStartOffset +
                    " " + et.selectionStart + " " + et.text
                    + " " + et.startOffset);
        ic.performPrivateCommand("ime_on", null);
    }

    @Override
    public void onUpdateExtractingViews(EditorInfo ei) {
        Log.d(TAG, "onUpdateExtractingViews: ");
    }


    //==============================================================================================

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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();
        Log.d(TAG, "onInitializeInterface: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }

    @Override
    public AbstractInputMethodImpl onCreateInputMethodInterface() {
        Log.d(TAG, "onCreateInputMethodInterface: ");
        return super.onCreateInputMethodInterface();
    }

    @Override
    public AbstractInputMethodSessionImpl onCreateInputMethodSessionInterface() {
        Log.d(TAG, "onCreateInputMethodSessionInterface: ");
        return super.onCreateInputMethodSessionInterface();
    }

    @Override
    public void onConfigureWindow(Window win, boolean isFullscreen, boolean isCandidatesOnly) {
        Log.d(TAG, "onConfigureWindow: ");
        super.onConfigureWindow(win, isFullscreen, isCandidatesOnly);
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        Log.d(TAG, "onEvaluateFullscreenMode: ");
        return super.onEvaluateFullscreenMode();
    }

    @Override
    public void onComputeInsets(Insets outInsets) {
        Log.d(TAG, "onComputeInsets: ");
        super.onComputeInsets(outInsets);
    }

    @Override
    public boolean onEvaluateInputViewShown() {
        Log.d(TAG, "onEvaluateInputViewShown: ");
        return super.onEvaluateInputViewShown();
    }

    @Override
    public View onCreateExtractTextView() {
        Log.d(TAG, "onCreateExtractTextView: ");
        return super.onCreateExtractTextView();
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d(TAG, "onCreateCandidatesView: ");
        return super.onCreateCandidatesView();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        Log.d(TAG, "onStartInputView: ");
        super.onStartInputView(info, restarting);
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        Log.d(TAG, "onFinishInputView: ");
        super.onFinishInputView(finishingInput);
    }

    @Override
    public void onStartCandidatesView(EditorInfo info, boolean restarting) {
        Log.d(TAG, "onStartCandidatesView: ");
        super.onStartCandidatesView(info, restarting);
    }

    @Override
    public void onFinishCandidatesView(boolean finishingInput) {
        Log.d(TAG, "onFinishCandidatesView: ");
        super.onFinishCandidatesView(finishingInput);
    }

    @Override
    public boolean onShowInputRequested(int flags, boolean configChange) {
        Log.d(TAG, "onShowInputRequested: ");
        return super.onShowInputRequested(flags, configChange);
    }

    @Override
    public void onWindowHidden() {
        Log.d(TAG, "onWindowHidden: ");
        super.onWindowHidden();
    }

    @Override
    public void onBindInput() {
        Log.d(TAG, "onBindInput: ");
        super.onBindInput();
    }

    @Override
    public void onUnbindInput() {
        Log.d(TAG, "onUnbindInput: ");
        super.onUnbindInput();
    }

    @Override
    public void onFinishInput() {
        Log.d(TAG, "onFinishInput: ");
        super.onFinishInput();
    }

    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        Log.d(TAG, "onDisplayCompletions: ");
        super.onDisplayCompletions(completions);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: ");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyLongPress: ");
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        Log.d(TAG, "onKeyMultiple: ");
        return super.onKeyMultiple(keyCode, count, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: ");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        Log.d(TAG, "onTrackballEvent: ");
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "onGenericMotionEvent: ");
        return super.onGenericMotionEvent(event);
    }

    @Override
    public void onAppPrivateCommand(String action, Bundle data) {
        Log.d(TAG, "onAppPrivateCommand: ");
        super.onAppPrivateCommand(action, data);
    }

    @Override
    public void onExtractedSelectionChanged(int start, int end) {
        Log.d(TAG, "onExtractedSelectionChanged: ");
        super.onExtractedSelectionChanged(start, end);
    }

    @Override
    public void onExtractedTextClicked() {
        Log.d(TAG, "onExtractedTextClicked: ");
        super.onExtractedTextClicked();
    }

    @Override
    public void onExtractedCursorMovement(int dx, int dy) {
        Log.d(TAG, "onExtractedCursorMovement: ");
        super.onExtractedCursorMovement(dx, dy);
    }

    @Override
    public boolean onExtractTextContextMenuItem(int id) {
        Log.d(TAG, "onExtractTextContextMenuItem: ");
        return super.onExtractTextContextMenuItem(id);
    }

    @Override
    public void onExtractingInputChanged(EditorInfo ei) {
        Log.d(TAG, "onExtractingInputChanged: ");
        super.onExtractingInputChanged(ei);
    }

    @Override
    protected void onCurrentInputMethodSubtypeChanged(InputMethodSubtype newSubtype) {
        Log.d(TAG, "onCurrentInputMethodSubtypeChanged: ");
        super.onCurrentInputMethodSubtypeChanged(newSubtype);
    }
}
