package com.iflytek.mkl.advertise.detect;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.iflytek.mkl.constant.value.CV;
import com.iflytek.mkl.db.DBUtil;
import com.iflytek.mkl.list.check.AdListUtil;
import com.iflytek.mkl.list.check.RuleCalcUtil;
import com.iflytek.mkl.list.check.SensitiveListUtil;
import com.iflytek.mkl.list.check.WhiteListUtil;
import com.iflytek.mkl.log.Log;

import java.util.ArrayList;

/**
 * 维护一个AppState队列，并执行detect操作
 */

public class AdDetectorHandler extends Handler {

    private static final String TAG = "AdDetectorHandler";

    private static final int MAX_SWITCH_SIZE = 4;    //假设跳转次数最多的跳转为A -> B -> C  -> D(current)
    private static final int MIN_SWITCH_SIZE = 3;    //一个可供判断的完整流程至少为A -> B -> D(current)
    private ArrayList<AppState> stateList = new ArrayList<>();

    public AdDetectorHandler(Looper looper) {
        super(looper);
    }

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

        //找到 A B A 或 A B C A 后计算B的得分
        Log.d(TAG, "detect: " + stateList.size() + "  " + preIndex);
        if (preIndex != -1 && stateList.get(preIndex).packageName.equals(currentAppState.packageName)) {
            AppState A, B, C;
            A = stateList.get(preIndex);
            B = stateList.get(preIndex + 1);
            C = stateList.get(preIndex + 2);
            if (C == currentAppState) C = null;
            RuleCalcUtil.calculate(A, B, C);
        }

        //清理列表，列表内元素个数只需确保能存下一个最长的循环即可
        while (stateList.size() > MAX_SWITCH_SIZE) {
            stateList.remove(0);
        }
    }
}
