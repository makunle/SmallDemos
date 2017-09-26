package com.iflytek.mkl.imeantiadstest;

import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.iflytek.mkl.advertise.detector.AppAnalysisTool;

/**
 * Created by makunle on 2017/9/22.
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
        AppAnalysisTool.getClassesInPkg(attribute.packageName, getApplicationContext());
    }

    @Override
    public void onComputeInsets(Insets outInsets) {
        super.onComputeInsets(outInsets);
    }
}
