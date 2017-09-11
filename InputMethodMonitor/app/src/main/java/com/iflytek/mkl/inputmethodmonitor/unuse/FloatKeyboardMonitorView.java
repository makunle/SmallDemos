package com.iflytek.mkl.inputmethodmonitor.unuse;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by makunle on 2017/9/11.
 */

public class FloatKeyboardMonitorView extends LinearLayout {

    private static final String TAG = "FloatKeyboardMonitorVie";

    private int maxHeight = 0;

    private Toast toast;

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

        setBackgroundColor(Color.RED);

        params.width = 5;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        manager.addView(this, params);

        toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
    }


    private void setScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
    }

    private void setSoftKeyboardHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(h > maxHeight) maxHeight = h;
        if(h >= maxHeight){
            toast.setText("IME hide");
            toast.show();
        }else{
            toast.setText("IME show");
            toast.show();
        }
        Log.d(TAG, "onSizeChanged: w:" + w+" h:"+h+" oldw:"+oldw+" oldh:"+oldh );
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: " + newConfig.orientation);
        maxHeight = 0;
    }
}
