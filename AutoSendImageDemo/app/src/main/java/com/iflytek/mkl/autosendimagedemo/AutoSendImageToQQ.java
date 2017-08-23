package com.iflytek.mkl.autosendimagedemo;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
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

    private static final String TAG = "AutoSendImageToQQ";

    /**
     * state change：
     * TYPE_VIEW_FOCUSED
     * TYPE_WINDOW_STATE_CHANGED -> isChatWindow() -> VIEW_CLICK -> isPictureSendWindowNow() -> VIEW_CLICK -> isPictureSendDialog() -> autoclick
     * state:                                      1                                          2            3
     */
    private int nowState = 0;
    private String listenerName = null;

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d(TAG, "onKeyEvent: " + event.getKeyCode());
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_MENU:
                nowState = 0;
        }
        return false;
    }




    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
            Log.d(TAG, event.getPackageName() + "  " + AccessibilityEvent.eventTypeToString(event.getEventType())
                    + "   isChatWindow " + isChatWindowNow()
                    + "   isPictureSendWindowNow " + isPictureSendWindowNow()
                    + "   isPictureSendDialogNow " + isPictureSendDialogNow());
        switch (nowState) {
            case 0:
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED
                        || event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    listenerName = isChatWindowNow();
                    if (listenerName != null) {
                        nowState = 1;
                    } else {
                        nowState = 0;
                    }
                }
                Log.d(TAG, "0 state:" + nowState + "  " + listenerName);
                break;
            case 1:
                if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED
                        || event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    if (isPictureSendWindowNow()) {
                        nowState = 2;
                    } else {
                        if (isChatWindowNow() == null) {
                            nowState = 0;
                        }
                    }
                }
                Log.d(TAG, "1 state:" + nowState + "  " + listenerName);
                break;
            case 2:
                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    boolean res = autoClickPersonWithName(listenerName, AccessibilityNodeInfo.ACTION_CLICK);
                    if (res) {
                        nowState = 3;
                    } else {
                        nowState = 0;
                    }
                }
                Log.d(TAG, "2 state:" + nowState + "  " + listenerName);
                break;
            case 3:
                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    autoClickYesInSendDialog();
                    nowState = 0;
                    listenerName = null;
                }
                Log.d(TAG, "3 state:" + nowState + "  " + listenerName);
                break;
        }
        event.recycle();
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

        if (root == null) return null;

        List<AccessibilityNodeInfo> input = root.findAccessibilityNodeInfosByViewId(QQ_CHAT_INPUT_ID);
        List<AccessibilityNodeInfo> send = root.findAccessibilityNodeInfosByViewId(QQ_CHAT_SEND_ID);
        List<AccessibilityNodeInfo> chaterNames = null;

        if (input.size() == 1 && send.size() == 1) {
            //个人、讨论组、群
            chaterNames = root.findAccessibilityNodeInfosByViewId(QQ_CHATER_NAME_ID);
        } else if (input.size() == 1 && send.size() == 0) {
            //我的电脑
            chaterNames = root.findAccessibilityNodeInfosByViewId(QQ_MY_PC_NAME_ID);
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
     * 判断当前是否在表情发送联系人选择页面
     *
     * @return
     */
    private boolean isPictureSendWindowNow() {
        boolean toReturn = false;
        AccessibilityNodeInfo root = getRootInActiveWindow();

        if (root == null) return false;

        List<AccessibilityNodeInfo> search = root.findAccessibilityNodeInfosByViewId(QQ_SEARCH_INPUT_ID);
        List<AccessibilityNodeInfo> tabs = root.findAccessibilityNodeInfosByViewId(QQ_TABS_ID);
        if (search.size() == 1 && tabs.size() == 0) {
            toReturn = true;
        }

        root.recycle();
        clean(search);
        clean(tabs);

        return toReturn;
    }


    /**
     * 判断当前是否为表情发送确认页面（对话框）
     *
     * @return
     */
    private boolean isPictureSendDialogNow() {
        boolean toReturn = false;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;

        List<AccessibilityNodeInfo> dialog = root.findAccessibilityNodeInfosByViewId(QQ_COMMIT_DIALOG_ID);
        if (dialog.size() == 1) {
            toReturn = true;
        }

        root.recycle();
        clean(dialog);

        return toReturn;
    }

    /**
     * 在发送列表中，点击具有给定名字的条目
     *
     * @param name   指定的text
     * @param action 要执行的操作
     * @return
     */
    private boolean autoClickPersonWithName(String name, int action) {
        boolean toReturn = false;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;
        List<AccessibilityNodeInfo> viewNode = root.findAccessibilityNodeInfosByText(name);
        for (AccessibilityNodeInfo node : viewNode) {
            if (node.getText().equals(name)) {
                AccessibilityNodeInfo parent = node.getParent();
                toReturn = parent.performAction(action);
                parent.recycle();
                Log.d(TAG, "autoClickPersonWithName: clicked: " + name);
            }
        }

        clean(viewNode);
        root.recycle();

        return toReturn;
    }

    /**
     * 自动点击发送确认对话框yes按钮
     *
     * @return
     */
    private boolean autoClickYesInSendDialog() {
        boolean toReturn = false;
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;
        List<AccessibilityNodeInfo> yes = root.findAccessibilityNodeInfosByViewId(QQ_COMMIT_DIALOG_YES_ID);
        if (yes.size() == 1) {
            toReturn = yes.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        root.recycle();
        clean(yes);
        return toReturn;
    }

    /**
     * recycle()
     *
     * @param nodes
     */
    private void clean(List<AccessibilityNodeInfo> nodes) {
        if (nodes == null) return;
        for (AccessibilityNodeInfo node : nodes) {
            node.recycle();
        }
    }
}
