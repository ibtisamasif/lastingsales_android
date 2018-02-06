package com.example.muzafarimran.lastingsales.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.example.muzafarimran.lastingsales.sync.AllContactProfilesFetchingEngineAsync;

import java.util.concurrent.TimeUnit;

/**
 * Created by ibtisam on 10/13/2017.
 */

public class DemoSyncJob extends Job {

    public static final String TAG = "job_demo_tag";

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        Log.d(TAG, "onRunJob: ");
        // run your job here
        AllContactProfilesFetchingEngineAsync allContactProfilesFetchingEngineAsync = new AllContactProfilesFetchingEngineAsync(getContext());
        allContactProfilesFetchingEngineAsync.getBatchContactsProfiles();
        return Result.SUCCESS;
    }

    public static void schedulePeriodic() {
        new JobRequest.Builder(DemoSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(120), TimeUnit.MINUTES.toMillis(10))
//                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
//                .setPersisted(true) //onReboot //after upgrading it is not supported anymore all jobs are persisted now in 1.2.0 previous version was 1.1.7
                .setRequiresCharging(true)
//                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }
}