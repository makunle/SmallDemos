package com.iflytek.mkl.inputmethodmonitor;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makunle on 2017/9/11.
 * 输入法悬浮键盘弹起/隐藏检测 工具
 */

public class FloatKeyboardMonitor {

    private static final boolean DEBUG = true;

    private static FloatKeyboardMonitor instance = new FloatKeyboardMonitor();

    private List<KeyboardStateListener> listenerList = new ArrayList<>();
    private MonitorView view;
    private Context context;

    private FloatKeyboardMonitor() {}

    /**
     * 工具初始化
     *
     * @param context
     */
    public static void init(Context context) {
        if (instance.context == null) {
            instance.context = context;
        }
    }

    /**
     * 添加输入法软键盘状态监听器
     *
     * @param listener
     */
    public synchronized static void register(KeyboardStateListener listener) {
        if (instance.context == null) {
            throw new IllegalStateException("monitor haven't initialized");
        }
        if (instance.listenerList.contains(listener)) {
            throw new IllegalStateException("keyboard listener already registered");
        }
        if (instance.view == null) {
            instance.view = instance.new MonitorView(instance.context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            WindowManager manager = (WindowManager) instance.context.getSystemService(Context.WINDOW_SERVICE);
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.format = PixelFormat.RGBA_8888;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                    WindowManager.LayoutParams.FLAG_FULLSCREEN;
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.x = 0;
            params.y = 0;
            params.width = 0;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;

            if (DEBUG) {
                params.width = 10;
                instance.view.setBackgroundColor(Color.RED);
            }

            manager.addView(instance.view, params);
        }
        instance.listenerList.add(listener);
    }

    /**
     * 移除输入法软键盘状态监听器
     *
     * @param listener
     */
    public synchronized static void unregister(KeyboardStateListener listener) {
        if (instance.context == null) {
            throw new IllegalStateException("monitor haven't initialized");
        }
        if (!instance.listenerList.contains(listener)) {
            throw new IllegalStateException("keyboard listener haven't registered or already unregistered");
        }
        instance.listenerList.remove(listener);
        if (instance.listenerList.size() == 0) {
            WindowManager manager = (WindowManager) instance.context.getSystemService(Context.WINDOW_SERVICE);
            manager.removeView(instance.view);
            instance.view = null;
        }
    }

    /**
     * keyboard state call back interface
     */
    public interface KeyboardStateListener {
        void onKeyboardShow();
        void onKeyboardHide();
    }

    private class MonitorView extends View {
        private int maxHeight = 0;

        public MonitorView(Context context) {
            super(context);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (h > maxHeight) maxHeight = h;
            if (h >= maxHeight) {
                for (KeyboardStateListener listener : listenerList) {
                    listener.onKeyboardHide();
                }
            } else {
                for (KeyboardStateListener listener : listenerList) {
                    listener.onKeyboardShow();
                }
            }
        }

        @Override
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            maxHeight = 0;
        }
    }
}
