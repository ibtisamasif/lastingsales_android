package com.example.muzafarimran.lastingsales.providers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.example.muzafarimran.lastingsales.utils.ProfileEngineAsync;

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
        ProfileEngineAsync profileEngineAsync = new ProfileEngineAsync(getContext());
        profileEngineAsync.run();
        return Result.SUCCESS;
    }

    public static void schedulePeriodic() {
        new JobRequest.Builder(DemoSyncJob.TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(10), TimeUnit.MINUTES.toMillis(5))
                .setUpdateCurrent(true)
                .setPersisted(true)
                .setRequiresCharging(true)
//                .setRequiresDeviceIdle(false)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }
}