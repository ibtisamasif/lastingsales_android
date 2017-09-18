package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.NavigationDrawerActivity;

/**
 * Created by ibtisam on 9/18/2017.
 */

public class RatingAlarmNotification {
    public static final String TAG = "myAlarmLog";
    public static final int NOTIFICATION_ID = 2;

    public static Notification createAlarmNotification(Context context, String title, String message) {
        Log.d(TAG, "createAlarmNotification: RatingAlarm");
        Intent inquiriesIntent = new Intent(context, NavigationDrawerActivity.class);
//        inquiriesIntent.putExtra("SELECTED_TAB", "INQUIRIES_TAB");
        PendingIntent pDetailsActivityIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pDetailsActivityIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setTicker("Lasting Sales")
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(message);
        return notificationBuilder.build();
    }
}
