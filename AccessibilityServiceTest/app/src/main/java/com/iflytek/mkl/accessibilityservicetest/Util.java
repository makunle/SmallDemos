package com.iflytek.mkl.accessibilityservicetest;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/17.
 */

public class Util {

    private static final String TAG = "Util";

    private static Pattern pattern = Pattern.compile("com.*:id/.*(code|Code|Pwd|key).*");

    private static List<AccessibilityNodeInfo> editTextList = new ArrayList<>();

    private TextToSpeech textToSpeech;

    public static void autoInputCode(AccessibilityEvent event, AccessibilityNodeInfo root, Context context) {
        String code = "";
        if (event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            code = greaterThanAPI18(notification);
        }
        showAll(root, context);
        if (!TextUtils.isEmpty(code)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("verification code", code));
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

    public static boolean isAccessibilityServiceRunning(Context context) {
        AccessibilityManager service = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> infoList = service.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo info : infoList) {
            if (info.getId().contains(context.getPackageName())) return true;
        }
        return false;
    }


    public static boolean needShowAll(Context context) {
        boolean needShowAll = context.getSharedPreferences("tempset", Context.MODE_APPEND).getBoolean("show_all", true);
        return needShowAll;
    }

    public static void showAll(AccessibilityNodeInfo root, Context context) {
        if (root == null) {
            show("root is null");
            return;
        }

        String info = root.getClassName() + " " + root.getText() + "  ";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            info += root.getViewIdResourceName();
            if (root.getClassName().equals("android.widget.EditText")) {
                editTextList.add(root);
            }
        }
        if (needShowAll(context)) show(info);
        for (int i = 0; i < root.getChildCount(); i++) {
            showAll(root.getChild(i), context);
        }
    }

    @Nullable
    public static String greaterThanAPI18(Notification notification) {
        String code = null;
        if (notification.tickerText != null) {
            code = VerificationCodeGetter.getCode(notification.tickerText.toString());
            if (!TextUtils.isEmpty(code)) return code;
        }
        show("tickerText:" + notification.tickerText);
        if (notification.contentView == null) {
            show("don't have contentView");
            return null;
        }

        try {
            RemoteViews views = notification.contentView;
            Class secretClass = views.getClass();
            Field mActions = secretClass.getDeclaredField("mActions");
            mActions.setAccessible(true);
            ArrayList<Object> actions = (ArrayList<Object>) mActions.get(views);
            for (Object action : actions) {
                Field innerFields[] = action.getClass().getDeclaredFields();
                show("action: " + action.getClass().getName());
                for (Field field : innerFields) {
                    if (field.getName().equals("value")) {
                        field.setAccessible(true);
                        show("action:" + field.get(action).toString() + "  " + field.get(action).getClass().getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static void show(String msg) {
        EventBus.getDefault().post(msg);
        Log.d(TAG, msg);
    }
}
