package com.example.muzafarimran.lastingsales.providers;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.orm.SchemaGenerator;
import com.orm.SugarApp;
import com.orm.SugarContext;
import com.orm.SugarDb;

/**
 * Created by ibtisam on 5/17/2017.
 */

public class Sugar extends SugarApp {
    private static final String TAG = "SugarApplicationClass";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Sugar Class");
        super.onCreate();
        SugarContext.init(getApplicationContext());

        // create table if not exists
        SchemaGenerator schemaGenerator = new SchemaGenerator(this);
        schemaGenerator.createDatabase(new SugarDb(this).getDB());

        //Android Job Library
        JobManager.create(this).addJobCreator(new DemoJobCreator());
        JobManager.instance().getConfig().setAllowSmallerIntervalsForMarshmallow(true); //TODO Don't use this in production


//        // SQUARE memory leakage library
//        Log.d(TAG, "onCreate: SquareLeakLibrary");
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
//        // Normal app init code...

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    @Override
    public void onLowMemory() {
        Log.d(TAG, "onLowMemory: ");
        super.onLowMemory();
    }
}