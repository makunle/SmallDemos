package com.iflytek.mkl.accessibilityservicetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Util.show("KEY: " + event.getKeyCode() + " " + event.getAction());
        if (event.getKeyCode() == 24) {
            MagnificationController controller = getMagnificationController();
            controller.setScale(controller.getScale() * 1.1f, true);
            return true;
        }
        if (event.getKeyCode() == 25) {
            MagnificationController controller = getMagnificationController();
            controller.setScale(controller.getScale() * 0.9f, true);
            return true;
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
         /*Util.show("\n\n" + accessibilityEvent.getPackageName()+ "  "
                + accessibilityEvent.getSource().getClassName() + " "
                + accessibilityEvent.getSource().getText());*/
        int eventType = event.getEventType();

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if(Util.showall(this)){
                    Util.showAll(getRootInActiveWindow(), this);
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                AccessibilityNodeInfo source = event.getSource();
                source.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            default:
                Util.show("EVENT TYPE: " + AccessibilityEvent.eventTypeToString(eventType) + "  "
                        + event.getPackageName() + " sx:" + event.getScrollX() + " sy:" + event.getScrollY());
                break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        Util.show("feedtype:" + AccessibilityServiceInfo.feedbackTypeToString(serviceInfo.feedbackType));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected boolean onGesture(int gestureId) {
        Util.show("Gesture: " + gestureId);
        if(gestureId == GESTURE_SWIPE_DOWN_AND_LEFT){
            disableSelf();
        }
        return super.onGesture(gestureId);
    }

    @Override
    public void onInterrupt() {

    }

}
