package com.iflytek.mkl.getwindowviewsinfo;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by makunle on 2017/8/31.
 */

public class MASService extends AccessibilityService {

    private static final String TAG = "ViewTools";

    private static final String LOG_FILE = "viewinfo.log";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "event: " + AccessibilityEvent.eventTypeToString(event.getEventType()) + "  " + event.getPackageName());
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    SimpleDateFormat format = new SimpleDateFormat("  HH:mm:ss");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        for (AccessibilityWindowInfo info : getWindows()) {
                            AccessibilityNodeInfo root = info.getRoot();
                            show("======================== " + root.getPackageName()
                                    + format.format(Calendar.getInstance().getTime())
                                    + "  " +info.getLayer() + " ========================");
                            show(root, 0);
                        }
                    } else {
                        show("======================== " + getPackageName() + format.format(Calendar.getInstance().getTime()) + " ========================");
                        show(getRootInActiveWindow(), 0);
                    }
                    show("======================================================\n");
                }

            }
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + LOG_FILE);
                file.delete();
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }

    private void show(String str) {
        Log.d(TAG, str);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + LOG_FILE);
        try {
            FileOutputStream fs = new FileOutputStream(file, true);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fs));
            bw.write(str + "\n");
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void show(AccessibilityNodeInfo root, int level) {
        if (root == null) return;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level - 1; i++) {
            sb.append("    ");
        }
        if (level > 0) sb.append("┗");

        String[] classSplit = root.getClassName().toString().split(".");
//        sb.append(classSplit.length > 0 ? classSplit[classSplit.length - 1] : "null");
        sb.append(root.getClassName());
        sb.append(" ").append(root.getText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sb.append(" ").append(root.getViewIdResourceName());
        }
        show(sb.toString());
        for (int i = 0; i < root.getChildCount(); i++) {
            show(root.getChild(i), level + 1);
        }
    }
}
