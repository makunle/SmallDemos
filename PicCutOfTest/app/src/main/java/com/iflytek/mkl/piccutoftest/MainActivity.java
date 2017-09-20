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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
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
    private float oldRotate, rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matrix = new Matrix();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                safeScale(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
                return true;
            }
        });

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                safeTranslation(-distanceX, -distanceY);
                return true;
            }
        });


        imageView = (ImageView) findViewById(R.id.iv);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);

                if (event.getPointerCount() == 2) {
                    float dx = event.getX(0) - event.getX(1);
                    float dy = event.getY(0) - event.getY(1);
                    float radians = (float) Math.toDegrees(Math.atan2(dy, dx));
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        rotate = radians - oldRotate;
                        if (Math.abs(rotate) < 5) {
                            matrix.postRotate(rotate,
                                    (event.getX(0) + event.getX(1)) / 2,
                                    (event.getY(0) + event.getY(1)) / 2);
//                            Log.d(TAG, "rotate: " + rotate);
                        }
                        oldRotate = radians;
                    }
                }


                imageView.setImageMatrix(matrix);

                mouseX = event.getX();
                mouseY = event.getY();

//                adjustToArea();
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

        int height = getWindowManager().getDefaultDisplay().getHeight() - 260;
        float scaleH = height / 3.0f / (float) bitmap.getHeight();

        int width = getWindowManager().getDefaultDisplay().getWidth();
        float scaleW = width / (float) bitmap.getWidth();

        float scale = Math.max(scaleH, scaleW);
        float transX = width / 2 - bitmap.getWidth() * scale / 2;
        float transY = height / 2 - bitmap.getHeight() * scale / 2;
//        matrix.preScale(scale, scale);
//        matrix.preTranslate(transX, transY);
        imageView.setImageMatrix(matrix);
    }

    private float getNowScale() {
        float[] value = new float[9];
        matrix.getValues(value);
        return value[0];
    }

    private float[] getNowTranslate() {
        float[] value = new float[9];
        matrix.getValues(value);
        return new float[]{value[2], value[5]};
    }

    private Rect getImageAreaRect() {
        Rect rect = new Rect();
        imageView.getGlobalVisibleRect(rect);
        return rect;
    }

    private Rect getPreviewAreaRect() {
        Rect rect = new Rect();
        previewArea.getGlobalVisibleRect(rect);
        return rect;
    }

    private void showInfo() {
        previewArea.getGlobalVisibleRect(previewAreaRect);
        imageView.getGlobalVisibleRect(imageAreaRect);

        float value[] = new float[9];
        matrix.getValues(value);
        String show = "image: " + imageX + " " + imageY +
//                "\nscale: " + value[0] + "\ntranslate x: " + value[2] + "\ntranslate y: " + value[5] +
                "\nimageview: " + imageAreaRect + "\npreview: " + previewAreaRect
                + "\nbitmap: " + bitmap.getWidth() + " - " + bitmap.getHeight()

//               + "\nmouse: " + (int) mouseX + " " + (int) mouseY
                ;

        for (int i = 0; i < 3; i++) {
            show += "\n";
            for (int j = 0; j < 3; j++) {
                show += value[j + i * 3] + "\r              ";
            }
        }

        infoOut.setText(show);

        drawPreViewRectInImg();
    }


    /**
     * 绘制preview rect在img上的投影
     */
    private void drawPreViewRectInImg() {
        float org[] = new float[]{previewAreaRect.left - imageAreaRect.left
                , previewAreaRect.top - imageAreaRect.top};
        float lt[] = new float[2];

        Matrix invertMatrix = new Matrix();
        matrix.invert(invertMatrix);

        invertMatrix.mapPoints(lt, org);

        org = new float[]{previewAreaRect.right - imageAreaRect.left,
                previewAreaRect.bottom - imageAreaRect.top};
        float rd[] = new float[2];
        invertMatrix.mapPoints(rd, org);

        org = new float[]{previewAreaRect.left - imageAreaRect.left,
                previewAreaRect.bottom - imageAreaRect.top};

        float ld[] = new float[2];
        invertMatrix.mapPoints(ld, org);

        org = new float[]{previewAreaRect.right - imageAreaRect.left,
                previewAreaRect.top - imageAreaRect.top};
        float rt[] = new float[2];
        invertMatrix.mapPoints(rt, org);


        Bitmap changedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(changedBitmap);
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawLine(0, 0, lt[0], lt[1], paint);
//        canvas.drawLine(imageAreaRect.right, imageAreaRect.bottom,
//                rd[0], rd[1], paint);

        canvas.drawLine(lt[0], lt[1], rt[0], rt[1], paint);
        canvas.drawLine(rt[0], rt[1], rd[0], rd[1], paint);
        canvas.drawLine(rd[0], rd[1], ld[0], ld[1], paint);
        canvas.drawLine(ld[0], ld[1], lt[0], lt[1], paint);

        imageView.setImageBitmap(changedBitmap);

//        Log.d(TAG, "image rect: " + imageAreaRect +
//                "\nproject rect: \n" + lt[0] + ", " + lt[1] + "               " + rt[0] + "," + rt[1]
//                + "\n" + ld[0] + ", " + ld[1] + "               " + rd[0] + ", " + rd[1]);

        String prinfo =  "\nproject rect: \n" + lt[0] + ",  " + lt[1] + "               " + rt[0] + ",  " + rt[1]
                + "\n" + ld[0] + ",  " + ld[1] + "               " + rd[0] + ",  " + rd[1];
        infoOut.append(prinfo);
    }

    /**
     * 安全平移，确保平移后图片还能完全覆盖preview area
     *
     * @param transX
     * @param transY
     */
    private void safeTranslation(float transX, float transY) {
//        float nowTransX = getNowTranslate()[0];
//        float nowTransY = getNowTranslate()[1];
//
//        float nowScale = getNowScale();
//
//        Rect ivRect = getImageAreaRect();
//        Rect previewRect = getPreviewAreaRect();
//
//        float imgUp = nowTransY + transY + ivRect.top - previewRect.top;
//        float imgDown = nowTransY + transY + ivRect.top + bitmap.getHeight() * nowScale - previewRect.bottom;
//        float imgLeft = nowTransX + transX + ivRect.left - previewRect.left;
//        float imgRight = nowTransX + transX + ivRect.left + bitmap.getWidth() * nowScale - previewRect.right;
//
//        if (imgUp > 0) {
//            transY -= imgUp;
//        } else if (imgDown < 0) {
//            transY -= imgDown;
//        }
//
//        if (imgLeft > 0) {
//            transX -= imgLeft;
//        } else if (imgRight < 0) {
//            transX -= imgRight;
//        }

//                Log.d(TAG, "move  up:" + imgUp + "  down:" + imgDown + "    left:" + imgLeft + "  right:" + imgRight);

        matrix.postTranslate(transX, transY);
    }

    /**
     * 安全缩放，确保缩放后图片还能完全覆盖preview area
     *
     * @param scale
     * @param focusX
     * @param focusY
     */
    private void safeScale(float scale, float focusX, float focusY) {
//        float nowScale = getNowScale();
//
//        int previewWidth = getPreviewAreaRect().width();
//        if (bitmap.getWidth() * nowScale * scale < previewWidth) {
//            scale = previewWidth / nowScale / bitmap.getWidth();
//        }
//
//        int previewHeight = getPreviewAreaRect().height();
//        if (bitmap.getHeight() * nowScale * scale < previewHeight) {
//            scale = previewHeight / nowScale / bitmap.getHeight();
//        }

//                Log.d(TAG, "onScale after width: " + bitmap.getWidth() * nowScale * canScale + "    view:" + previewWidth);

        matrix.postScale(scale, scale, focusX, focusY);
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
        Rect ivRect = new Rect();
        imageView.getGlobalVisibleRect(ivRect);

        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        previewRect = new Rect(0, 0, width, height);
        ivRect = new Rect(0, height / 4, width, height / 4 * 3);

        float[] value = new float[9];
        matrix.getValues(value);

        float nowScale = value[0];
        //调整scale，使得图片的长宽>=预览窗口长宽
        float needScaleX = previewRect.width() / (bitmap.getWidth() * nowScale);
        float needScaleY = previewRect.height() / (bitmap.getHeight() * nowScale);
        float needScale = nowScale;
        if (needScaleX > 1 || needScaleY > 1) {
            needScale = Math.max(needScaleX, needScaleY);
            matrix.postScale(needScale, needScale);
        }

        //调整平移值，使得图片左右/上下边移动到最近的preview窗口左右/上下边上。
        float transX = value[2];
        float transY = value[5];
        float imgUp = transY + ivRect.top - previewRect.top;
        float imgDown = transY + ivRect.top + bitmap.getHeight() * needScale - previewRect.bottom;
        float imgLeft = transX + ivRect.left - previewRect.left;
        float imgRight = transX + ivRect.left + bitmap.getWidth() * needScale - previewRect.right;
//        Log.d(TAG, "adjustToArea  up:" + imgUp + "  down:" + imgDown + "    left:" + imgLeft + "  right:" + imgRight);
        float needTransX = 0, needTransY = 0;
        if (imgLeft > 0) needTransX = -imgLeft;
        else if (imgRight < 0) needTransX = -imgRight;
        if (imgUp > 0) needTransY = -imgUp;
        else if (imgDown < 0) needTransY = -imgDown;
        matrix.postTranslate(needTransX, needTransY);

        Log.d(TAG, "adjust done");
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

    public void rotate(View view) {
        matrix.preRotate(45);
        imageView.setImageMatrix(matrix);
        showInfo();
    }

    //scale之后，如果使用postTranslate,则translate的值不受scale影响
    //如果使用preTranslate，则translate值受影响，scale 2倍的话translate的值也会x2
    public void translate(View view) {
        matrix.postTranslate(5, 5);
        imageView.setImageMatrix(matrix);
        showInfo();
    }

    //translate之后，如果使用preScale,则translate的值不改变，但设置center工作不正常
    //如果使用postScale，则translate值受影响，scale 2倍的话translate的值也会x2,但postScale设置的center正常工作
    public void scale(View view) {
        matrix.postScale(1.1f, 1.1f, 1080 / 2, 1860 / 2);
        imageView.setImageMatrix(matrix);
        showInfo();
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
