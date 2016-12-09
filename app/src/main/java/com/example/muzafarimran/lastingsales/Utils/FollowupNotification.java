package com.example.muzafarimran.lastingsales.Utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.FollowupNotiActivity;
import com.example.muzafarimran.lastingsales.receivers.FollowupNotiCancelBtnReceiver;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class FollowupNotification {
    public static final int NOTIFICATION_ID = 1;
    public static Notification createNotification(Context ctx, String name) {
        //Create an Intent for the BroadcastReceiver
        Intent cancelIntent = new Intent(ctx, FollowupNotiCancelBtnReceiver.class);
        cancelIntent.putExtra("notificationId", NOTIFICATION_ID);
        //Create the PendingIntent
        PendingIntent cancelpIntent = PendingIntent.getBroadcast(ctx, 0, cancelIntent, 0);
        //Ahmad Needs to replace FollowupNotiActivity with new Followup Activity.
        Intent intent = new Intent(ctx, FollowupNotiActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intent, 0);
        Notification.Builder notificationBuilder = new Notification.Builder(ctx)
                .setSmallIcon(R.drawable.follow_ups_today_icon)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("Lasting Sales")
                .addAction(R.drawable.cancel, "Cancel", cancelpIntent)
                .addAction(R.drawable.follow_ip_icon_white, "Follow Up", pIntent)
                .setContentText("Add Follow Up for " + name + "?")
                .setFullScreenIntent(pIntent, true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }
}
