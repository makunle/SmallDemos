package com.iflytek.mkl.jobschedulertest;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService2 extends JobService {

    private static final String TAG = "JobSchedulerService2";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "service 2 onStartJob: " + params.getJobId());
        jobFinished(params, true);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
