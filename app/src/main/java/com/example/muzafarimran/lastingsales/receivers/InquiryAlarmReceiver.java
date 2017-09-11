package com.example.muzafarimran.lastingsales.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.muzafarimran.lastingsales.activities.AlarmAlertDialogActivity;
import com.example.muzafarimran.lastingsales.activities.AlertDialogActivity;

/**
 * Created by ibtisam on 6/15/2017.
 */

@Deprecated
public class InquiryAlarmReceiver extends WakefulBroadcastReceiver{
    public static final String TAG = "InquiryAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Notify(context, intent, "Alarm", "Alarm has been raised");
        Log.d(TAG, "onReceive: Alarm Fired");
        Log.d("InquiryManager", "onReceive: Alarm Fired");
    }

    private void Notify(Context context, Intent intent, String notificationTitle, String notificationMessage) {
        Intent i = new Intent(context, AlarmAlertDialogActivity.class);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Long inquiryId = Long.parseLong(extras.getString("inquiryId"));
            i.putExtra("inquiryId", inquiryId + "");
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
