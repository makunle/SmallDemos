package com.iflytek.mkl.advertise.detect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.HandlerThread;
import android.os.Message;
import android.view.inputmethod.EditorInfo;

import com.iflytek.mkl.constant.value.CV;
import com.iflytek.mkl.db.DBUtil;
import com.iflytek.mkl.list.check.AdListUtil;
import com.iflytek.mkl.list.check.RuleCalcUtil;
import com.iflytek.mkl.list.check.SensitiveListUtil;
import com.iflytek.mkl.list.check.WhiteListUtil;
import com.iflytek.mkl.log.Log;

/**
 * Created by makunle on 2017/9/21.
 * 用于与IMEService交互，并设置AppState，并放入AdDetector的队列中
 */

public class AdDetect {

    private static final String TAG = "AdDetect";


    //singleton
    private static AdDetect instance = new AdDetect();

    private AdDetect() {
        detectorThread = new HandlerThread("detector-thread");
        detectorThread.start();
        detectorHandler = new AdDetectorHandler(detectorThread.getLooper());
    }

    private HandlerThread detectorThread;
    private AdDetectorHandler detectorHandler;

    AppState currentApp;

    public static void init(Context context) {
        DBUtil.initDb(context);
        AdListUtil.init(context);
        WhiteListUtil.init(context);
        SensitiveListUtil.init(context);
        RuleCalcUtil.init(context);
    }

    //需由IMEService同名回调函数调用
    public static void onStartInput(EditorInfo info) {
        instance.startApp(info);
    }

    //需由IMEService同名回调函数调用
    public static void onFinishInput() {
        instance.finishInput();
    }

    //需由IMEService同名回调函数调用
    public static void onStartInputView() {
        instance.doAnInput();
    }

    //需由IMEService同名回调函数调用
    public static void onFinishInputView() {
        instance.finishCurrentInput();
    }

    //需由IMEService同名回调函数调用
    public static void onDestroy(){
        SQLiteDatabase db = DBUtil.getDb();
        if(db != null){
            db.close();
        }
    }

    /***
     * 切换了一个应用
     * @param info
     */
    private void startApp(EditorInfo info) {
        if (currentApp != null && !info.packageName.equals(currentApp.packageName)) {
            currentApp.end();

            Message msg = new Message();
            msg.what = CV.APP_STATE_MSG;
            msg.obj = currentApp;
            detectorHandler.sendMessage(msg);

            Log.d(TAG, "add state: " + currentApp.packageName + "  " + currentApp.getDurTime() + "  " + currentApp.getState());
            currentApp = null;
        }
        currentApp = new AppState(info.packageName);
    }

    /***
     * 结束一个应用，一般一定会有startInput,所以这个没用？？？？？？？？
     */
    private void finishInput() {
    }

    /***
     * 应用内关闭一次键盘
     */
    private void finishCurrentInput() {
        //因为应用被关闭时会被调用，如果设置AppState.state==NOT_INPUT会影响starting_input判断
        //一般如果关闭键盘但仍在应用中，时间应该会超过1s，所以此处不设置state为NOT_INPUT
    }

    /***
     * 开始执行一段输入
     */
    private void doAnInput() {
        if (currentApp == null) return;
        if (currentApp.getState() == AppState.NOT_INPUT) {
            currentApp.startInput();
        }
        DBUtil.setContainInput(currentApp.packageName);
    }

}
