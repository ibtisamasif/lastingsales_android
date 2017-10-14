package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.utils.HourlyAlarmNotification;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.ArrayList;
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

        int count = intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, -1);
        Log.d(TAG, "onReceive: Count: " + count);
//        if (count < 2) {
            List<LSInquiry> lsInquiry = LSInquiry.getAllPendingInquiriesInDescendingOrder();
            if (lsInquiry != null && lsInquiry.size() > 0) {
                List<String> nameFromApp = new ArrayList<>();
                List<String> nameFromPhonebook = new ArrayList<>();
                List<String> numberFromApp = new ArrayList<>();
                List<String> nameOrNumber = new ArrayList<>();
                List<String> timeAgo = new ArrayList<>();
                List<String> messageList = new ArrayList<>();

                for (int i = 0; i < lsInquiry.size(); i++) {
                    Log.d(TAG, "onReceive: lsinquiry.size(): " + lsInquiry.size());
                    nameFromApp.add(lsInquiry.get(i).getContactName());
                    nameFromPhonebook.add(PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, lsInquiry.get(i).getContactNumber()));
                    numberFromApp.add(lsInquiry.get(i).getContactNumber());
                    if (nameFromApp.get(i) != null) {
                        nameOrNumber.add(nameFromApp.get(i));
                    } else if (nameFromPhonebook.get(i) != null) {
                        nameOrNumber.add(nameFromPhonebook.get(i));
                    } else {
                        nameOrNumber.add(numberFromApp.get(i));
                    }
                    messageList.add(nameOrNumber.get(i));
//                    messageList.add(nameOrNumber.get(i) + "                                     (" +  PhoneNumberAndCallUtils.getTimeAgo(lsInquiry.get(i).getBeginTime(), context) + ")");
                }

                if (lsInquiry.size() == 1) {
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(HourlyAlarmNotification.NOTIFICATION_ID, HourlyAlarmNotification.createHourlyAlarmNotificationForSingle(context, "Lets Callback", messageList.get(0), numberFromApp.get(0)));
                } else if (lsInquiry.size() > 1) {
                    String numberOfContactHavingImage = "";
                    for (int i = 0; i < lsInquiry.size(); i++) {
                        if (lsInquiry.get(i).getContactProfile() !=null )
                            if(lsInquiry.get(i).getContactProfile().getSocial_image() != null) {
                                numberOfContactHavingImage = lsInquiry.get(i).getContactNumber();
                                break;
                        }
                    }
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(HourlyAlarmNotification.NOTIFICATION_ID, HourlyAlarmNotification.createHourlyAlarmNotificationForList(context, "Lets Callback", messageList, numberOfContactHavingImage));
                }
            }
            // Refresh Service once daily.
//        context.startService(new Intent(context, CallDetectionService.class));  // TODO is it still needed here as well ?
//        }
    }
}

