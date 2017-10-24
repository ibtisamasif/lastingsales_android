package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.ContactDetailsTabActivity;
import com.example.muzafarimran.lastingsales.activities.FrameActivity;
import com.example.muzafarimran.lastingsales.activities.NavigationDrawerActivity;
import com.example.muzafarimran.lastingsales.fragments.InquiriesFragment;

/**
 * Created by ibtisam on 6/21/2017.
 */

public class DayStartHighlightAlarmNotification {
    public static final String TAG = "DayStartHighlightAlarmNotification";
    public static final int NOTIFICATION_ID = 1;

    public static Notification createAlarmNotification(Context context, String title, String message) {

        Intent inquiriesIntent = new Intent(context, NavigationDrawerActivity.class);
        inquiriesIntent.putExtra("SELECTED_TAB", "INQUIRIES_TAB");
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
