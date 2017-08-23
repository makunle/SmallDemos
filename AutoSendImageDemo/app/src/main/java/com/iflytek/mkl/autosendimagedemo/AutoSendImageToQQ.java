package com.iflytek.mkl.autosendimagedemo;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/8/23.
 */

public class AutoSendImageToQQ extends AccessibilityService {

    //只含有一个输入框，0个或1个发送按钮的为聊天页面 我的电脑为页面1 0，个人、群、讨论组、公众号为 1 1
    private static final String QQ_CHAT_INPUT_ID = "com.tencent.mobileqq:id/input";
    private static final String QQ_CHAT_SEND_ID = "com.tencent.mobileqq:id/fun_btn";
    private static final String QQ_CHATER_NAME_ID = "com.tencent.mobileqq:id/title";        //个人、群、讨论组标题
    private static final String QQ_MY_PC_NAME_ID = "com.tencent.mobileqq:id/ivTitleName";   //我的电脑标题

    //含有搜索框，不含tabs页面为表情发送联系人选择页面
    private static final String QQ_SEARCH_INPUT_ID = "com.tencent.mobileqq:id/et_search_keyword";
    private static final String QQ_TABS_ID = "android:id/tabs";

    private static final String QQ_COMMIT_DIALOG_ID = "com.tencent.mobileqq:id/dialogTitle";
    private static final String QQ_COMMIT_DIALOG_YES_ID = "com.tencent.mobileqq:id/dialogRightBtn";

    private static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
    private static final String IME_PACKAGE_NAME = "com.iflytek.inputmethod";

    private static final String TAG = "AutoSendImageToQQ";

    private int nowState = 0;
    private String chaterName = null;

    /**
     * state change：
     * QQ                                             IME                                       QQ
     * TYPE_VIEW_FOCUSED                                                          autoclick
     * TYPE_WINDOW_STATE_CHANGED -> isChatWindow() -> VIEW_CLICK -> isPictureSendWindowNow() -> VIEW_CLICK -> isPictureSendDialog() -> autoclick
     * state:                             1                                  2                                        3
     */

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.d(TAG, "onAccessibilityEvent: " +
//                AccessibilityEvent.eventTypeToString(event.getEventType()) + " " + event.getPackageName());
//        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
//            Log.d(TAG, event.getPackageName() + "  " + AccessibilityEvent.eventTypeToString(event.getEventType())
//                    + "   isChatWindow " + isChatWindowNow()
//                    + "   isPictureSendWindowNow " + isPictureSendWindowNow()
//                    + "   isPictureSendDialogNow " + isPictureSendDialogNow());
        switch (nowState) {
            case 0:
                if (event.getPackageName().equals(QQ_PACKAGE_NAME)) {
                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED
                            || event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                        chaterName = isChatWindowNow();
                        if (chaterName != null) {
                            nowState = 1;

                        } else {
                            nowState = 0;
                        }

                    }
                }
                Log.d(TAG, "0 state: " + nowState);
                break;
            case 1:
                if (event.getPackageName().equals(IME_PACKAGE_NAME)) {
                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                        if (isPictureSendWindowNow(chaterName)) {
                            //开始执行自动发送操作
                            nowState = 2;
                        } else {
                            nowState = 0;
                        }

                        chaterName = null;
                    }
                }
                Log.d(TAG, "1 state: " + nowState);
                break;
            case 2:
                if (event.getPackageName().equals(QQ_PACKAGE_NAME)) {
                    if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                        isChatWindowNow();
                        nowState = 0;
                        chaterName = null;
                    }

                }
                Log.d(TAG, "2 state: " + nowState);
                break;
        }
    }

    @Override
    public void onInterrupt() {
    }

    /**
     * 判断是否为聊天窗口，如果是，返回聊天对象名称，不是则返回null
     *
     * @return
     */
    private String isChatWindowNow() {
        String chaterName = null;
        AccessibilityNodeInfo root = getRootInActiveWindow();

        List<AccessibilityNodeInfo> input = root.findAccessibilityNodeInfosByViewId(QQ_CHAT_INPUT_ID);
        List<AccessibilityNodeInfo> send = root.findAccessibilityNodeInfosByViewId(QQ_CHAT_SEND_ID);
        List<AccessibilityNodeInfo> chaterNames = null;

        if (input.size() == 1 && send.size() == 1) {
            //个人、讨论组、群
            chaterNames = root.findAccessibilityNodeInfosByViewId(QQ_CHATER_NAME_ID);
        } else if (input.size() == 1 && send.size() == 0) {
            //我的电脑
            chaterNames = root.findAccessibilityNodeInfosByText(QQ_MY_PC_NAME_ID);
        }
        if (chaterNames != null && chaterNames.size() > 0) {
            chaterName = chaterNames.get(0).getText().toString();
        }

        root.recycle();
        clean(input);
        clean(send);
        if (chaterNames != null) clean(chaterNames);

        return chaterName;
    }

    /**
     * 判断当前是否在表情发送联系人选择页面,如果是，找寻给定联系人名称，并点击
     *
     * @return 不在当前页面或未找到联系人返回false
     */
    private boolean isPictureSendWindowNow(String chaterName) {
        boolean toReturn = false;
        AccessibilityNodeInfo root = getRootInActiveWindow();

        List<AccessibilityNodeInfo> search = root.findAccessibilityNodeInfosByViewId(QQ_SEARCH_INPUT_ID);
        List<AccessibilityNodeInfo> tabs = root.findAccessibilityNodeInfosByViewId(QQ_TABS_ID);
        if (search.size() == 1 && tabs.size() == 0 && chaterName != null) {
            List<AccessibilityNodeInfo> people = root.findAccessibilityNodeInfosByText(chaterName);
            if (people.size() == 1) {
                people.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                toReturn = true;
            }
            clean(people);
        }

        root.recycle();
        clean(search);
        clean(tabs);

        return toReturn;
    }


    /**
     * 判断当前是否为表情发送确认页面（对话框），是则点击确认按钮
     *
     * @return 点击了确认按钮为true，否则为false
     */
    private boolean isPictureSendDialogNow() {
        boolean toReturn = false;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        List<AccessibilityNodeInfo> dialog = root.findAccessibilityNodeInfosByViewId(QQ_COMMIT_DIALOG_ID);
        if (dialog.size() == 1) {
            List<AccessibilityNodeInfo> yes = root.findAccessibilityNodeInfosByViewId(QQ_COMMIT_DIALOG_YES_ID);
            if (yes.size() == 1) {
                toReturn = yes.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            clean(yes);
        }

        root.recycle();
        clean(dialog);

        return toReturn;
    }

    /**
     * recycle()
     *
     * @param nodes
     */
    private void clean(List<AccessibilityNodeInfo> nodes) {
        for (AccessibilityNodeInfo node : nodes) {
            node.recycle();
        }
    }
}
