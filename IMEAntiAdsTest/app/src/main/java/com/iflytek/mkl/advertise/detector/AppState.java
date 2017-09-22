package com.iflytek.mkl.advertise.detector;

import android.os.SystemClock;

/**
 * Created by makunle on 2017/9/21.
 */

public class AppState {
    public static final int NOT_INPUT = 0;
    public static final int STARTING_INPUT = 1;
    public static final int IN_INPUT = 2;

    public int adValue = 1;

    public String packageName;

    private int state;  //1 not input ,    2 going to input,    3 input ing

    private long startTime = 0;
    private long endTime = 0;

    public void setStartTime() {
        startTime = SystemClock.currentThreadTimeMillis();
    }

    public void setEndTime() {
        endTime = SystemClock.currentThreadTimeMillis();
    }

    public long getDurTime() {
        if (endTime == 0) setEndTime();
        return endTime - startTime;
    }

    public AppState(String packageName) {
        this.packageName = packageName;
        state = NOT_INPUT;
        setStartTime();
    }

    public void startInput() {
        this.state = STARTING_INPUT;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (endTime == 0)
                        state = IN_INPUT;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
