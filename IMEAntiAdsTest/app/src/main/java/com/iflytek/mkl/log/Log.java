package com.iflytek.mkl.log;

/**
 * 自定义Log
 */

public class Log {
    public static boolean DEBUG = true;

    public static void e(String TAG, String msg){
        if(DEBUG) {
            android.util.Log.e(TAG, msg);
        }
    }
    public static void d(String TAG, String msg){
        if(DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }
    public static void v(String TAG, String msg){
        if(DEBUG) {
            android.util.Log.v(TAG, msg);
        }
    }
}
