package com.iflytek.mkl.advertise.detect;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iflytek.mkl.constant.value.CV;
import com.iflytek.mkl.db.DBUtil;
import com.iflytek.mkl.list.check.AdListUtil;
import com.iflytek.mkl.list.check.RuleCalcUtil;
import com.iflytek.mkl.list.check.SensitiveListUtil;
import com.iflytek.mkl.list.check.WhiteListUtil;

import java.util.ArrayList;

/**
 * Created by makunle on 2017/9/26.
 * 维护一个AppState队列，并执行detect操作
 */

public class AdDetectorHandler extends Handler {

    private static final String TAG = "AdDetectorHandler";

    private static final int MAX_SWITCH_SIZE = 4;    //假设跳转次数最多的跳转为A -> B -> C  -> D(current)
    private static final int MIN_SWITCH_SIZE = 3;    //一个可供判断的完整流程至少为A -> B -> D(current)
    private ArrayList<AppState> stateList = new ArrayList<>();

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == CV.APP_STATE_MSG) {
            AppState state = (AppState) msg.obj;
            addState(state);
        }
    }

    public void addState(AppState appState) {
        stateList.add(appState);
        String list = "";
        for (AppState state : stateList) {
            list += state.packageName + "   ";
        }
        Log.d(TAG, "now list: " + list);
        detect();
    }

    private void detect() {
        if (stateList.size() < MIN_SWITCH_SIZE) return;
        //找到一个循环
        int currentIndex = stateList.size() - 1;
        AppState currentAppState = stateList.get(currentIndex);
        //找到循环的开始A B A 或 A B C A
        int preIndex = currentIndex - 1;
        while (preIndex >= 0 && !stateList.get(preIndex).packageName.equals(currentAppState.packageName)) {
            preIndex--;
        }

        Log.d(TAG, "detect: " + stateList.size() + "  " + preIndex);
        if (preIndex != -1 && stateList.get(preIndex).packageName.equals(currentAppState.packageName)) {
            AppState A, B, C;
            A = stateList.get(preIndex);
            B = stateList.get(preIndex + 1);
            C = stateList.get(preIndex + 2);
            if (C == currentAppState) C = null;
            detectScoreOfB(A, B, C);
            RuleCalcUtil.calculate(A, B, C);
        }
//        for (int i = 0; i < preIndex; i++) {
//            stateList.remove(0);
//        }
        while (stateList.size() > MAX_SWITCH_SIZE) {
            stateList.remove(0);
        }
    }

    /***
     * 根据规则评分，因为一些列表查询操作可能耗时较多,不过是在HandlerThread中运行
     * @param A
     * @param B
     * @param C
     */
    private void detectScoreOfB(final AppState A, final AppState B, final AppState C) {
        float Add = 0, Multi = 1;
        String rule = "";
        //规则1 A状态判断
        if (A.getState() == AppState.STARTING_INPUT) {
            Add += 5;
            rule += "1:+5 ";
        } else if (A.getState() == AppState.IN_INPUT) {
            Add += 3;
            rule += "1:+3 ";
        } else if (A.getState() == AppState.NOT_INPUT) {
            Add += 2;
            rule += "1:+2 ";
        }
        //规则2 A是否包含输入操作
        if (DBUtil.getContainInput(A.packageName)) {
            Add += 1;
            rule += "2:+1 ";
        }
        //规则3 B是否在白名单
        if (WhiteListUtil.isInWhiteList(B.packageName)) {
            Multi *= 0;
            rule += "3:x0 ";
        } else {
            rule += "3:x1 ";
        }
        //规则4 B是否包含广告SDK
        if (AdListUtil.isInAdList(B.packageName)) {
            Add += 1;
            rule += "4:+1 ";
        }
        //规则5 B持续时间长短
        if (B.getDurTime() < CV.SHORT_DURATION_THRESHOLD) {
            Add += 1;
            rule += "5:+1 ";
        }
        //规则6 B持续时间为3,5s
        if (Math.abs(B.getDurTime() - 3) < 0.001f || Math.abs(B.getDurTime() - 5) < 0.001f) {
            Add += 1;
            rule += "6:+1 ";
        }
        //规则7 B进行了输入操作
        if (B.isInputed()) {
            Multi *= 0;
            rule += "7:x0 ";
        }
        //规则8 弹出了C
        if (C != null) {
            Add += 1;
            rule += "8:+1 ";
            //规则9 C持续时间
            if (C.getDurTime() < CV.SHORT_DURATION_THRESHOLD) {
                Add += 0.5;
                rule += "9:+0.5 ";
            } else {
                Add -= 1;
                rule += "9:-1 ";
            }
            //规则10 C为敏感应用
            if (SensitiveListUtil.isSensitive(C.packageName)) {
                Add += 1;
                rule += "10:+1 ";
            }
            //规则11 C中有输入操作
            if (C.isInputed()) {
                Multi *= 0;
                rule += "11:x0 ";
            }
        }
        //规则12 从C还是B回到了A
        if (C != null) {
            Add += 0.5;
            rule += "12:+0.5 ";
        } else {
            Add += 1;
            rule += "12:+1 ";
        }
        //规则13 B是否长时间逗留
        if (B.getDurTime() > CV.LONG_DURATION_THRESHOLD) {
            Multi *= 0;
            rule += "13:x0 ";
        }
        //规则14 C是否长时间逗留
        if(C != null && C.getDurTime() > CV.LONG_DURATION_THRESHOLD){
            Multi *= 0;
            rule += "14:x0 ";
        }

        float total = Add * Multi;
        DBUtil.setDetectResult(B.packageName, total, rule);
    }

}
