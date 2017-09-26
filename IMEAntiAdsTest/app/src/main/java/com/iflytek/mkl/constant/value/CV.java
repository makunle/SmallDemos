package com.iflytek.mkl.constant.value;

/**
 * Created by makunle on 2017/9/26.
 * 常量
 */

public class CV {
    public static final int SHORT_DURATION_THRESHOLD = 2000;         //持续时间长短阈值，超过此阈值表示持续时间长
    public static final int START_INPUT_TIME_THRESHOLD = 1000;   //是否正准备输入时间阈值，从开始输入到此阈值内都算正准备输入，超过则为正在输入
    public static final int LONG_DURATION_THRESHOLD = 8000;   //长时间逗留
    public static final int APP_STATE_MSG = 1;
}
