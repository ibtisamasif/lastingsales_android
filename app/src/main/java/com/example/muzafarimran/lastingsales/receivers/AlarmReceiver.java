package com.example.muzafarimran.lastingsales.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.muzafarimran.lastingsales.activities.AlertDialogActivity;

@Deprecated
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
//        Notify(context, intent, "Alarm", "Alarm has been raised");
    }

    private void Notify(Context context, Intent intent, String notificationTitle, String notificationMessage) {
        Intent i = new Intent(context, AlertDialogActivity.class);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Long followupId = Long.parseLong(extras.getString("followupid"));
            i.putExtra("followupid", followupId + "");
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}