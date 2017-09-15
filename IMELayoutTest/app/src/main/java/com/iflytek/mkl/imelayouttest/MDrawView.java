package com.iflytek.mkl.imelayouttest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by makunle on 2017/9/15.
 */

public class MDrawView extends FrameLayout {

    private static final String TAG = "MDrawView";
    
    public MDrawView(Context context) {
        super(context);
    }

    public MDrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.GRAY);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        canvas.drawLine(0, 0, 100, 100, paint);
        canvas.save();
    }
}
