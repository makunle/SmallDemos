package com.iflytek.mkl.getwindowviewsinfo;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

/**
 * Created by makunle on 2017/8/31.
 */

public class MASService extends AccessibilityService {

    private static final String TAG = "ViewTools";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        for (AccessibilityWindowInfo info : getWindows()) {
                            AccessibilityNodeInfo root = info.getRoot();
                            show("========================" + root.getPackageName() + "========================");
                            show(root, 0);
                        }
                    } else {
                        show("========================" + getPackageName() + "========================");
                        show(getRootInActiveWindow(), 0);
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }

    private void show(String str) {
        Log.d(TAG, str);
    }

    private void show(AccessibilityNodeInfo root, int level) {
        if (root == null) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level - 1; i++) {
            sb.append("    ");
        }
        if (level > 0) sb.append("┗");
        sb.append("class:").append(root.getClassName());
        sb.append(" text:").append(root.getText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sb.append(" id:").append(root.getViewIdResourceName());
        }
        show(sb.toString());
        for (int i = 0; i < root.getChildCount(); i++) {
            show(root.getChild(i), level + 1);
        }
    }
}
