package com.iflytek.mkl.imeantiadstest;

import android.inputmethodservice.InputMethodService;
import com.iflytek.mkl.log.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.iflytek.mkl.advertise.detect.AdDetect;

/**
 * 输入法服务，demo部分,仅用于测试
 */

public class IMEService extends InputMethodService {
    private static final String TAG = "IMEService";

    @Override
    public View onCreateInputView() {
        View v = getLayoutInflater().inflate(R.layout.keyboard_layout, null);
        return v;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        Log.d(TAG, "onStartInput: " + attribute.packageName);
//        AppAnalysisTool.getClassesInPkg(attribute.packageName, getApplicationContext());
        AdDetect.onStartInput(attribute);
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        Log.d(TAG, "onStartInputView: ");
        AdDetect.onStartInputView();
    }

    @Override
    public void onFinishInput() {
        Log.d(TAG, "onFinishInput: ");
        super.onFinishInput();
        AdDetect.onFinishInput();
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        Log.d(TAG, "onFinishInputView: ");
        super.onFinishInputView(finishingInput);
        AdDetect.onFinishInputView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AdDetect.onDestroy();
    }
}
