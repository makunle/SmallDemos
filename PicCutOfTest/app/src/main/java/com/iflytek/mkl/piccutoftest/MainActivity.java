package com.iflytek.mkl.piccutoftest;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Environment;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ImageView imageView;
    TextView infoOut;
    View previewArea;
    Bitmap bitmap;

    Rect previewAreaRect = new Rect();
    Rect imageAreaRect = new Rect();

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;


    private Matrix matrix;
    private int imageX, imageY;
    private float mouseX, mouseY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrix = new Matrix();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                return true;
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                matrix.postTranslate(-distanceX, -distanceY);
                return true;
            }
        });


        imageView = (ImageView) findViewById(R.id.iv);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                scaleGestureDetector.onTouchEvent(motionEvent);
                imageView.setImageMatrix(matrix);

                mouseX = motionEvent.getX();
                mouseY = motionEvent.getY();

                adjustToArea();
                showInfo();
                return true;
            }
        });

//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.desert);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.small);
        ;
        imageX = bitmap.getWidth();
        imageY = bitmap.getHeight();
        imageView.setImageBitmap(bitmap);

        infoOut = (TextView) findViewById(R.id.et);
        previewArea = findViewById(R.id.preview_layout);
    }

    private void showInfo() {
        previewArea.getGlobalVisibleRect(previewAreaRect);
        imageView.getGlobalVisibleRect(imageAreaRect);

        float value[] = new float[9];
        matrix.getValues(value);
        String show = "image: " + imageX + " " + imageY +
                "\nscale: " + value[0] + "\ntranslate x: " + value[2] + "\ntranslate y: " + value[5] +
                "\nimageview: " + imageAreaRect + "\npreview: " + previewAreaRect +
                "\nmouse: " + (int) mouseX + " " + (int) mouseY;
        infoOut.setText(show);
    }

    /**
     * 计算当前ImageView上给定点在图片上的位置
     *
     * @param px
     * @param py
     * @param out
     */
    private void calculatePointInPic(int px, int py, int out[]) {
        Rect ivRect = new Rect();
        imageView.getGlobalVisibleRect(ivRect);
        float value[] = new float[9];
        matrix.getValues(value);

        float deltaX = px - ivRect.left;
        float deltaY = py - ivRect.top;
        float transX = value[2];
        float transY = value[5];
        float scale = value[0];

        deltaX = (deltaX - transX) / scale;
        deltaY = (deltaY - transY) / scale;

        out[0] = (int) deltaX;
        out[1] = (int) deltaY;
    }

    /**
     * 计算屏幕上一个Rect对应图片中Rect
     *
     * @param rect
     * @return
     */
    private Rect calculateRectInPic(Rect rect) {
        Rect ivRect = new Rect();
        imageView.getGlobalVisibleRect(ivRect);

        float value[] = new float[9];
        matrix.getValues(value);
        float transX = value[2];
        float transY = value[5];
        float scale = value[0];

        Rect irect = new Rect();
        irect.left = (int) ((rect.left - ivRect.left - transX) / scale);
        irect.right = (int) ((rect.right - ivRect.left - transX) / scale);
        irect.top = (int) ((rect.top - ivRect.top - transY) / scale);
        irect.bottom = (int) ((rect.bottom - ivRect.top - transY) / scale);

        return irect;
    }

    /**
     * 调整matrix，让图片至少覆盖preview area
     */
    private void adjustToArea() {
        Rect previewRect = new Rect();
        previewArea.getGlobalVisibleRect(previewRect);

        float[] value = new float[9];
        matrix.getValues(value);

        float nowScale = value[0];
        //调整scale，使得图片的长宽>=预览窗口长宽
        float scaleX = previewRect.width() / (bitmap.getWidth() * nowScale);
        float scaleY = previewRect.height() / (bitmap.getHeight() * nowScale);
        if (scaleX > 1 || scaleY > 1) {
            float shouldScale = Math.max(scaleX, scaleY);
            matrix.postScale(shouldScale, shouldScale);
        }

        //调整平移值，使得图片左右/上下边移动到最近的preview窗口左右/上下边上。
    }

    /**
     * 获取缩放矩阵，放大0.5倍，平移x:100 y:-50时矩阵如下：
     * 坐标轴：x 向右为正方向    y向下为正方向
     * 0:0.5      1:0       2:100
     * 3:0        4:0.5     5:-50
     * 6:0        7:0       8:1
     * scale: value[0]
     * transX: value[2]
     * transY: value[5]
     *
     * @param view
     */
    public void getInfo(View view) {
        Rect previewRect = new Rect();
        previewArea.getGlobalVisibleRect(previewRect);
        Rect imgRect = calculateRectInPic(previewRect);
        Bitmap changedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(changedBitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(imgRect, paint);
        imageView.setImageBitmap(changedBitmap);

        Bitmap toSave = Bitmap.createBitmap(this.bitmap, imgRect.left, imgRect.top, imgRect.width(), imgRect.height());
        saveBitmapToSdcard(toSave, "Cut.png");
    }

    private void saveBitmapToSdcard(final Bitmap bitmap, final String name) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String filepath = Environment.getExternalStorageDirectory() + File.separator + name;
                File f = new File(filepath);
                if (f.exists()) f.delete();
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    Log.d(TAG, "save success");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "save failed");
                }
                return null;
            }
        }.execute();

    }


//     imageView.setOnTouchListener(new View.OnTouchListener() {
//        private float px, py;
//        private float px1, px2, py1, py2;
//        private boolean isRoom = false;
//        @Override
//        public boolean onTouch(View view, MotionEvent e) {
//            Log.d(TAG, "onTouch: " + e.getAction() + "  " + e.getPointerCount());
//            scaleGestureDetector.onTouchEvent(e);
////                if(!scaleGestureDetector.onTouchEvent(e))
//            if(e.getPointerCount() == 2){
//                isRoom = true;
//            }
////                if(e.getPointerCount() == 1) {
//            switch (e.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//
//                    px = e.getX();
//                    py = e.getY();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    float dx = e.getX() - px;
//                    float dy = e.getY() - py;
//                    px = e.getX();
//                    py = e.getY();
//                    if(!isRoom)
//                        matrix.postTranslate(dx, dy);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    if(e.getPointerCount() == 1){
//                        isRoom = false;
//                    }
//                    break;
//            }

//                }
//                if(e.getPointerCount() == 2){
//                    switch (e.getAction()){
//                        case MotionEvent.ACTION_DOWN:
//                            px1 = e.getX(0);
//                            px2 = e.getX(1);
//                            py1 = e.getY(0);
//                            py2 = e.getY(1);
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//
//                    }
//                }
//            imageView.setImageMatrix(matrix);
//            return true;
//        }
//
//    });
}