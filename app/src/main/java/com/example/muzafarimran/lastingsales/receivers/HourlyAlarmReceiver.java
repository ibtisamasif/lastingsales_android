package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.utils.HourlyAlarmNotification;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.List;

/**
 * Created by ibtisam on 6/20/2017.
 */

public class HourlyAlarmReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "myAlarmLog";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive:Hourly Alarm Fired");
        Log.d("Inquiry", "onReceive:Hourly Alarm Fired");

        List<LSInquiry> lsInquiry = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        if (lsInquiry != null && lsInquiry.size() > 0) {
            String nameOrNumber;
            String name = lsInquiry.get(0).getContactName();
            String number = lsInquiry.get(0).getContactNumber();
            String nameFromPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, lsInquiry.get(0).getContactNumber());
            if (name != null) {
                nameOrNumber = name;
            }else if (nameFromPhonebook != null){
                nameOrNumber = nameFromPhonebook;
            }
            else {
                nameOrNumber = number;
            }
            String message = "call back " + nameOrNumber;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(HourlyAlarmNotification.NOTIFICATION_ID, HourlyAlarmNotification.createAlarmNotification(context, "Pending Inquiries", message, number));
        }
        // Refresh Service once daily.
//        context.startService(new Intent(context, CallDetectionService.class));  // TODO is it still needed here as well ?
    }
}
