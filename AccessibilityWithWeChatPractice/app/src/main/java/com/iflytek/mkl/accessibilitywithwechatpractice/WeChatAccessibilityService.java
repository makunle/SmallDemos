package com.iflytek.mkl.accessibilitywithwechatpractice;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
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

    /***
     * qq群
     * ico com.tencent.mobileqq:id/chat_item_head_icon
     * msg com.tencent.mobileqq:id/chat_item_content_layout
     * nam com.tencent.mobileqq:id/chat_item_nick_name
     *
     * 个人
     * ico com.tencent.mobileqq:id/chat_item_head_icon
     * msg com.tencent.mobileqq:id/chat_item_content_layout  android.widget.TextView
     * voc com.tencent.mobileqq:id/qq_aio_ptt_time_tv
     * nam com.tencent.mobileqq:id/title
     * @param event
     */

    private void catchWeChatMsg(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        AccessibilityNodeInfo parent = source.getParent();
        if (parent != null) {

            List<AccessibilityNodeInfo> txt = parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ij");
            List<AccessibilityNodeInfo> img = parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ih");
            List<AccessibilityNodeInfo> audio = parent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a9u");

            String man = null;
            String say = null;

            int msgX = 0, imgX = 0;
            Rect rect = new Rect();

            if (txt != null && img != null && audio == null || audio.size() == 0) {

                if (txt.size() == 1) {
                    AccessibilityNodeInfo msg = txt.get(0);
                    if (msg.getClassName().equals("android.widget.TextView")) {
                        say = "" + msg.getText();
                        msg.getBoundsInScreen(rect);
                        msgX = rect.centerX();
                        msg.recycle();
                    }
                }

                if (img.size() == 1) {
                    AccessibilityNodeInfo face = img.get(0);
                    if (face.getClassName().equals("android.widget.ImageView")) {
                        String dsp = (String) face.getContentDescription();
                        if (dsp.endsWith("头像")) {
                            man = dsp.substring(0, dsp.length() - 2);
                        }
                        face.getBoundsInScreen(rect);
                        imgX = rect.centerX();
                        face.recycle();
                    }
                }


                if (man != null && say != null) {
                    if (msgX < imgX) {
                        man = "我";
                    }
                    ShowWindowActivity.startMe(this, man, say);
                }
            }
            parent.recycle();
        }

        source.recycle();
    }

    @Override
    public void onInterrupt() {

    }
}
