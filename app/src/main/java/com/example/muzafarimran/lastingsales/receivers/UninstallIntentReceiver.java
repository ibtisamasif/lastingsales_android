package com.example.muzafarimran.lastingsales.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ibtisam on 10/18/2017.
 */

//Didnt use this thing
    @Deprecated
public class UninstallIntentReceiver extends BroadcastReceiver {
    public static final String TAG = "ntent";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: ");
        // fetching package names from extras
        String[] packageNames = intent.getStringArrayExtra("android.intent.extra.PACKAGES");

        if (packageNames != null) {
            for (String packageName : packageNames) {
                if (packageName != null && packageName.equals("com.example.muzafarimran.lastingsales")) {
                    // User has selected our application under the Manage Apps settings
                    // now initiating background thread to watch for activity
                    new ListenActivities(context).start();

                }
            }
        }
    }

}