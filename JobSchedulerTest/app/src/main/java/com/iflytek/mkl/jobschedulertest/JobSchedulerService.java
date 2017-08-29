package com.iflytek.mkl.jobschedulertest;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class JobSchedulerService extends JobService {
    private static final String TAG = "JobSchedulerService";
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "service 1 onStartJob: " + params.getJobId());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
