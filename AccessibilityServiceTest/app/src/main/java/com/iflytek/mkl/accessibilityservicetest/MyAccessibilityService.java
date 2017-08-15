package com.iflytek.mkl.accessibilityservicetest;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/14.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    private List<AccessibilityNodeInfo> editTextList = new ArrayList<>();
    private Pattern pattern = Pattern.compile("com.*:id/.*(code|Code|Pwd|key).*");

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        show("package name: " + accessibilityEvent.getPackageName().toString());
        int eventType = accessibilityEvent.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                String code = "";
                show("TYPE_NOTIFICATION_STATE_CHANGED");
                if (accessibilityEvent.getParcelableData() instanceof Notification) {
                    Notification notification = (Notification) accessibilityEvent.getParcelableData();
                    code = greaterThanAPI18(notification);
                    show("Notification with code: " + code);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    AccessibilityNodeInfo root = null;
                    root = getRootInActiveWindow();
                    editTextList.clear();
                    showAll(root);
                    show("edittext number: " + editTextList.size());
                    if (!TextUtils.isEmpty(code)) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setPrimaryClip(ClipData.newPlainText("verificaiton code", code));
                            for (int i = 0; i < editTextList.size(); i++) {
                                AccessibilityNodeInfo node = editTextList.get(i);
                                String resId = node.getViewIdResourceName();
                                resId = resId == null ? "" : resId;
                                if (pattern.matcher(resId).matches() || i == editTextList.size() - 1) {
                                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                                }
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                show("TYPE_WINDOW_STATE_CHANGED");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    AccessibilityNodeInfo root = null;
                    root = getRootInActiveWindow();
                    editTextList.clear();
                    showAll(root);
                    show("edittext number: " + editTextList.size());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2 && editTextList.size() == 2) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText("phone number", "18788858382"));
                        AccessibilityNodeInfo node = editTextList.get(0);
                        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    }
                }
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                    AccessibilityNodeInfo root = null;
//                    root = getRootInActiveWindow();
//                    showAll(root);
//                }
                break;
            default:
                show("onAccessibilityEvent default: " + eventType);
                show("type: " + eventType);
                break;
        }
    }

    private void showAll(AccessibilityNodeInfo root) {
        if (root == null) return;
        String info = root.getClassName() + " " + root.getText() + "  ";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            info += root.getViewIdResourceName();
            if (root.getClassName().equals("android.widget.EditText")) {
                editTextList.add(root);
            }
        }
        show(info);
        for (int i = 0; i < root.getChildCount(); i++) {
            showAll(root.getChild(i));
        }
    }

    private String greaterThanAPI18(Notification notification) {
        String code = VerificationCodeGetter.getCode(notification.tickerText.toString());
        if (!TextUtils.isEmpty(code)) return code;
        show("tickerText:" + notification.tickerText.toString());
        if (notification.contentView == null) {
            show("don't have contentView");
            return null;
        }
        try {
            RemoteViews views = notification.contentView;
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);
            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) continue;
                // View ID
                parcel.readInt();
                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    code = VerificationCodeGetter.getCode(t);
                    if (!TextUtils.isEmpty(code)) return code;
//                    show(t);
                }
                parcel.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void show(String msg) {
        EventBus.getDefault().post(msg);
        Log.d(TAG, msg);
    }

    @Override
    public void onInterrupt() {

    }
}
