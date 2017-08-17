package com.iflytek.mkl.accessibilitywithwechatpractice;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Administrator on 2017/8/17.
 */

public class IApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(this, "60f736fd3f", false);
    }
}
