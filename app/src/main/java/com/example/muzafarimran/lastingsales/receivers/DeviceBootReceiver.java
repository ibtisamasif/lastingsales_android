package com.example.muzafarimran.lastingsales.receivers;

/**
 * Created by ahmad on 31-Oct-16.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 16); // For 4 PM or 5 PM
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(context, InquiriesDayEndAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        }
    }
}