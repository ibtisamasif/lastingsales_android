package com.example.muzafarimran.lastingsales.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.service.CallDetectionService;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;

/**
 * Created by ibtisam on 2/18/2017.
 */
public class AppUpdateReceiver extends BroadcastReceiver {
    public static final String TAG = "AppUpdateReceiver";

    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "AppUpdateReceiver onReceive");

        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isUserSignedIn()) {
            context.startService(new Intent(context, CallDetectionService.class));
            Log.d(TAG, "CallDetectionService: Service Started");
        }

        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
        dataSenderAsync.run();

//        Toast.makeText(context, "Connectivity changed", Toast.LENGTH_SHORT).show();
    }
}