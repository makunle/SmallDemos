package com.iflytek.mkl.accessibilityservicetest;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2017/8/15.
 */

public class MApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "89f40b542d", false);
    }
}
