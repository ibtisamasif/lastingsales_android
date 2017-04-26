package com.example.muzafarimran.lastingsales.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;

/**
 * Created by ibtisam on 2/18/2017.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    public static final String TAG = "NetworkStateReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Network connectivity change");
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Log.d(TAG, "Network " + ni.getTypeName() + " connected");
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(context);
                dataSenderAsync.run();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.d(TAG, "There's no network connectivity");
            }
        }
    }
}