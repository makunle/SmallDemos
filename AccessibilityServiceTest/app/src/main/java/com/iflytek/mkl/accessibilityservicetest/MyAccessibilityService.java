package com.iflytek.mkl.accessibilityservicetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SCROLLED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

/**
 * Created by Administrator on 2017/8/14.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    private AccessibilityNodeInfo preWindowInfo1, preWindowInfo2;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                MagnificationController controller = getMagnificationController();
                controller.setScale(controller.getScale() * 1.1f, true);
                return true;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                MagnificationController controller = getMagnificationController();
                controller.setScale(controller.getScale() * 0.9f, true);
                return true;
            }
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
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Util.show(AccessibilityEvent.eventTypeToString(eventType) + " " + event.getText() + "   pre window");
                Util.autoInputCode(event, getRootInActiveWindow(), this);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    GestureDescription.Builder builder = new GestureDescription.Builder();
                    Path path = new Path();
                    path.moveTo(400, 400);
                    path.lineTo(400, 1000);
                    builder.addStroke(new GestureDescription.StrokeDescription(path, 500, 500));
                    GestureDescription ges = builder.build();
                    dispatchGesture(ges, new GestureResultCallback() {
                        @Override
                        public void onCompleted(GestureDescription gestureDescription) {
                            super.onCompleted(gestureDescription);
                            Util.show("gesture completed");
                        }

                        @Override
                        public void onCancelled(GestureDescription gestureDescription) {
                            super.onCancelled(gestureDescription);
                            Util.show("gesture cancelled");
                        }
                    }, null);
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Util.show("VIEW_CLICKED " + event.getClassName() + " " + event.getText());
                event.getSource().performAction(AccessibilityNodeInfo.ACTION_DISMISS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    event.getSource().getViewIdResourceName();
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                Util.show("ANNOUNCEMENT: " + event);
                break;
            default:

                break;
        }

        if (eventType != TYPE_WINDOW_CONTENT_CHANGED && eventType != TYPE_VIEW_SCROLLED) {
            Util.show(AccessibilityEvent.eventTypeToString(eventType) + " " + event.getPackageName() + " " + event.getMovementGranularity());
        }


    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        Util.show("feedtype:" + AccessibilityServiceInfo.feedbackTypeToString(serviceInfo.feedbackType));

//        AccessibilityServiceInfo info = getServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED;
//        setServiceInfo(info);

    }

    @Override
    protected boolean onGesture(int gestureId) {
        Util.show("Gesture: " + gestureId);
        if (gestureId == GESTURE_SWIPE_DOWN_AND_LEFT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                disableSelf();
            }
        }
        else if(gestureId == GESTURE_SWIPE_LEFT_AND_RIGHT) {

        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            AccessibilityServiceInfo info = getServiceInfo();
            info.notificationTimeout = 500;
            setServiceInfo(info);
        }

        return false;
    }

    @Override
    public void onInterrupt() {
        Util.show("on interrupt");
    }
}
