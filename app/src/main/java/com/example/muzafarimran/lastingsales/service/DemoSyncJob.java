package com.example.muzafarimran.lastingsales.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
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
                .setPeriodic(TimeUnit.MINUTES.toMillis(240), TimeUnit.MINUTES.toMillis(10))
                .setUpdateCurrent(true)
                .setRequiresCharging(true)
//                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

    public static void cancelThisJob(){
        JobManager.instance().cancelAllForTag(DemoSyncJob.TAG);
    }
}