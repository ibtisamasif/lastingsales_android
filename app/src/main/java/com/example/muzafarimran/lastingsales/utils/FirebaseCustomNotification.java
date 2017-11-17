package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.FrameActivity;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;

/**
 * Created by ibtisam on 10/9/2017.
 */

public class FirebaseCustomNotification {
    public static final int NOTIFICATION_ID = 3;

    public static Notification createFirebaseInquiriesNotification(Context context, String message) {

        Intent inquiriesIntent = new Intent(context, NavigationBottomMainActivity.class);
        inquiriesIntent.putExtra("KEY_SELECTED_TAB", "INQUIRIES_TAB");
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Lasting Sales")
                .setTicker("Lasting Sales")
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(message);
        return notificationBuilder.build();
    }

    public static Notification createFirebaseUnlabeledNotification(Context context, String message) {

        Intent unlabeledIntent;
        Bundle bundle = new Bundle();
//        bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, UnlabeledFragment.class.getName());
        bundle.putString(FrameActivity.ACTIVITY_TITLE, "Unlabeled Leads");
        bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
        unlabeledIntent = new Intent(context, FrameActivity.class);
        unlabeledIntent.putExtras(bundle);

        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), unlabeledIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Lasting Sales")
                .setTicker("Lasting Sales")
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(message);
        return notificationBuilder.build();
    }

    public static Notification createFirebaseHomescreenNotification(Context context, String message) {

        Intent homescreenIntent = new Intent(context, NavigationBottomMainActivity.class);
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), homescreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Lasting Sales")
                .setTicker("Lasting Sales")
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(message);
        return notificationBuilder.build();
    }
}
