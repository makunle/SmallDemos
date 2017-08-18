package com.iflytek.mkl.accessibilitywithwechatpractice;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/8/17.
 */

public class WeChatAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if (event.getPackageName().equals("com.tencent.mm")) {
                    catchWeChatMsg(event);
                }
                break;
            default:
        }
    }

    private void catchWeChatMsg(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        AccessibilityNodeInfo parent = source.getParent();
        if (parent == null) return;

        List<AccessibilityNodeInfo> txt = parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ij");
        List<AccessibilityNodeInfo> img = parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ih");
        List<AccessibilityNodeInfo> audio = parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a9u");

        String man = null;
        String say = null;

        if (txt == null || img == null) return;
        if (audio != null && audio.size() > 0) return;

        for (int i = 0; i < txt.size(); i++) {
            say = "" + txt.get(i).getText();
            txt.get(i).recycle();
        }
        for (int i = 0; i < img.size(); i++) {
            String dsp = "" + img.get(i).getContentDescription();
            if (dsp.endsWith("头像")) {
                man = dsp.substring(0, dsp.length() - 2);
            }
            img.get(i).recycle();
        }
        parent.recycle();
        source.recycle();

        if (man != null && say != null) {
            MainActivity.startMe(this, man, say);
        }
    }

    @Override
    public void onInterrupt() {

    }
}
