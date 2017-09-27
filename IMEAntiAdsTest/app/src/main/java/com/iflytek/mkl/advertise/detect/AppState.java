package com.iflytek.mkl.advertise.detect;

import com.iflytek.mkl.constant.value.CV;

/**
 * Created by makunle on 2017/9/21.
 * 记录应用状态
 */

public class AppState {
    public static final int NOT_INPUT = 0;
    public static final int STARTING_INPUT = 1;
    public static final int IN_INPUT = 2;

    public String packageName;

    private int state;  //1 not input ,    2 going to input,    3 input ing

    private long startTime = 0;
    private long endTime = 0;
    private long inputTime = 0;

    private boolean inputed;

    public boolean isInputed() {
        return inputed;
    }

    public void end() {
        endTime = System.currentTimeMillis();
        if (state == STARTING_INPUT && endTime - inputTime > CV.START_INPUT_TIME_THRESHOLD) {
            state = IN_INPUT;
        }
    }

    public long getDurTime() {
        if (endTime == 0) end();
        return endTime - startTime;
    }

    public AppState(String packageName) {
        this.packageName = packageName;
        state = NOT_INPUT;
        inputed = false;
        startTime = System.currentTimeMillis();
    }

    public void startInput() {
        this.state = STARTING_INPUT;
        inputTime = System.currentTimeMillis();
        inputed = true;
    }

    public int getState() {
        return state;
    }

    public long getStartTime() {
        return startTime;
    }
}
