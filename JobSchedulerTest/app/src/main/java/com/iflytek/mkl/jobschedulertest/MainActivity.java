package com.iflytek.mkl.jobschedulertest;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void job1(View view) {
        addJob(1);
    }

    public void job2(View view) {
        addJob(2);
    }

    private void addJob(int id){
        addJob(id, JobSchedulerService.class.getName());
    }

    private void addJob(int id, String className) {
        JobScheduler service = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo info = new JobInfo.Builder(id, new ComponentName(getPackageName(), className))
                .setMinimumLatency(3000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();
        service.schedule(info);
    }

    public void service2Job3(View view) {
        addJob(3, JobSchedulerService2.class.getName());
    }
}
