package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.service.CallDetectionService;
import com.example.muzafarimran.lastingsales.utils.DayEndAlarmNotification;

import java.util.List;

/**
 * Created by ibtisam on 6/20/2017.
 */

public class InquiriesDayEndAlarmReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "InquiriesDayEndAlarmRec";
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Alarm Fired");
        Log.d("Inquiry", "onReceive: Alarm Fired");

        List<LSInquiry> lsInquiry = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        if (lsInquiry != null && lsInquiry.size() > 0) {
            String message = "You might have pending inquiries";
            String nameOrNumber;
            String name = lsInquiry.get(0).getContactName();
            if (name != null) {
                nameOrNumber = name;
            } else {
                String number = lsInquiry.get(0).getContactNumber();
                nameOrNumber = number;
            }
            if(lsInquiry.size() == 1){
                message = nameOrNumber;
            }
            else if (lsInquiry.size() == 2) {
                message = nameOrNumber + " and 1 others";
            } else if (lsInquiry.size() > 2) {
                message = nameOrNumber + " and 2 others";
            }
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(DayEndAlarmNotification.NOTIFICATION_ID, DayEndAlarmNotification.createAlarmNotification(context, "Pending Inquiries", message));
        }

        // Refresh Service once daily.
        context.startService(new Intent(context, CallDetectionService.class));
    }
}
