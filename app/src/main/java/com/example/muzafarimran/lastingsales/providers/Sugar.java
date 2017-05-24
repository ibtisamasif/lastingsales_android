package com.example.muzafarimran.lastingsales.providers;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.orm.SugarApp;
import com.orm.SugarContext;

/**
 * Created by ibtisam on 5/17/2017.
 */

public class Sugar extends SugarApp {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}