package com.iflytek.mkl.inputmethodmonitor;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by makunle on 2017/9/11.
 */

public class FloatKeyboardMonitorView extends LinearLayout {

    private static final String TAG = "FloatKeyboardMonitorVie";


    private int screenHeight = 0;
    private int softKeyboardHeight = 0;
    private int oldOrientation = 0;

    public FloatKeyboardMonitorView(Context context) {
        super(context);
        setScreenHeight(context);
        setSoftKeyboardHeight(context);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888;
        params.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM|
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 0;
        params.y = 0;

        params.width = 0;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        manager.addView(this, params);
    }


    private void setScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;//手机屏幕高度
    }

    private void setSoftKeyboardHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        softKeyboardHeight = (int) (dm.density * 100f + 0.5f);//软键盘的高度，这里定义了一个随机值，假设所有手机输入法最小高度为100dp
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG, "screenHeight=" + screenHeight + ";w=" + w + ";h=" + h + ";oldw=" + oldw + ";oldh=" + oldh);
        if (h == screenHeight) {
            if (oldh != 0) {
                Log.i(TAG, "变化为全屏了.");
            } else {
                Log.i(TAG, "初始化，当前为全屏状态.");
            }
        } else if (Math.abs(h - oldh) > softKeyboardHeight) {
            if (h >= oldh) {
                Log.i(TAG, "变化为正常状态(输入法关闭).");
            } else {
                Log.i(TAG, "输入法显示了.");
            }
        } else {
            if (oldh != 0) {
                Log.i(TAG, "变化为正常状态.(全屏关闭)");
            } else {
                Log.i(TAG, "初始化，当前为正常状态.");
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged neworientation=" + newConfig.orientation + ";oldOrientation=" + oldOrientation);
        if (oldOrientation != newConfig.orientation) {
            setScreenHeight(getContext());
            oldOrientation = newConfig.orientation;
        }
    }
}
