package com.example.muzafarimran.lastingsales.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.example.muzafarimran.lastingsales.service.DemoSyncJob;

/**
 * Created by ibtisam on 10/13/2017.
 */

public class DemoJobCreator implements JobCreator {

    @Override
    @Nullable
    public Job create(@NonNull String tag) {
        switch (tag) {
            case DemoSyncJob.TAG:
                return new DemoSyncJob();
            default:
                return null;
        }
    }
}
