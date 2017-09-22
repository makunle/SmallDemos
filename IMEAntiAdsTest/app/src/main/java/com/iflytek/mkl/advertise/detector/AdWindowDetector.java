package com.iflytek.mkl.advertise.detector;

import android.view.inputmethod.EditorInfo;

import java.util.ArrayList;

/**
 * Created by makunle on 2017/9/21.
 */

public class AdWindowDetector {
    private static AdWindowDetector instance = new AdWindowDetector();

    private AdWindowDetector() {
    }

    public static void onStartInput(EditorInfo info) {
        instance.startInput(info);
    }

    public static void onFinishInput() {
        instance.finishInput();
    }

    public static void onComputeInsets() {
        instance.doAnInput();
    }

    public static void onFinishInputView() {
        instance.finishCurrentInput();
    }


    private ArrayList<AppState> tasks = new ArrayList<>();
    private int limit = 5;

    private void storeState(AppState state) {
        if (state == null) return;
        while (tasks.size() > limit - 1) {
            tasks.remove(0);
        }
        tasks.add(state);
    }

    private AppState getLast1() {
        if (tasks.size() >= 1) {
            return tasks.get(tasks.size() - 1);
        } else {
            return null;
        }
    }

    private AppState getLast2() {
        if (tasks.size() >= 2) {
            return tasks.get(tasks.size() - 2);
        } else {
            return null;
        }
    }

    private AppState getLast3() {
        if (tasks.size() >= 3) {
            return tasks.get(tasks.size() - 3);
        } else {
            return null;
        }
    }

    private AppState currentApp;

    /***
     * 开始一次输入
     * @param info
     */
    private void startInput(EditorInfo info) {
        if (currentApp == null) {
            currentApp = new AppState(info.packageName);
        } else {
            if (!currentApp.packageName.equals(info.packageName)) {
                storeState(currentApp);
                currentApp.setEndTime();
                currentApp = new AppState(info.packageName);
                //开始风险评分
            }
        }
    }

    private void valueDetect() {
        int value = 1;
        //A当前状态
        switch (getLast1().getState()) {
            case AppState.NOT_INPUT:
                value *= 1;
                break;
            case AppState.STARTING_INPUT:
                value *= 10;
                break;
            case AppState.IN_INPUT:
                value *= 5;
        }

        //A是否为敏感应用，有输入的应用
        value *= needALotInput(getLast1()) ? 2 : 1;

        //B是否在白名单中
        value *= inWhiteList(currentApp) ? 0 : 1;

        currentApp.adValue = value;

        if(currentApp.packageName.equals(getLast2().packageName)){

        }
    }

    //B应用是否在白名单中
    private boolean inWhiteList(AppState currentApp) {
        return false;
    }


    //A应用为敏感应用，即包含大量输入操作
    private boolean needALotInput(AppState last1) {
        return true;
    }

    /***
     * 结束一个应用
     */
    private void finishInput() {
        currentApp.setState(AppState.NOT_INPUT);
    }

    /***
     * 应用内关闭一次键盘
     */
    private void finishCurrentInput() {
        currentApp.setState(AppState.NOT_INPUT);
    }

    /***
     * 开始执行一段输入
     */
    private void doAnInput() {
        if (currentApp.getState() == AppState.NOT_INPUT) {
            currentApp.startInput();
        }
    }

    public interface DetectResult {
        void onResult(String packageName, int riskValue);
    }

}
