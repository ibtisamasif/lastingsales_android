package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.NavigationDrawerActivity;

import java.util.List;

/**
 * Created by ibtisam on 6/21/2017.
 */

public class HourlyAlarmNotification {
    public static final String TAG = "DayStartHighlightAlarmNotification";
    public static final int NOTIFICATION_ID = 2;

    public static Notification createAlarmNotification(Context context, String title, String message, String number) {

        Intent inquiriesIntent = new Intent(context, NavigationDrawerActivity.class);
        inquiriesIntent.putExtra("SELECTED_TAB", "INQUIRIES_TAB");
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel: " + number));
        PendingIntent pCallBackIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), callIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .addAction(R.drawable.ic_call_green, "Call Back", pCallBackIntent)
                .addAction(R.drawable.ic_call_green, "Others", pContentIntent)
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

    public static Notification createAlarmNotification(Context context, String title, List<String> messageList) {

        Intent inquiriesIntent = new Intent(context, NavigationDrawerActivity.class);
        inquiriesIntent.putExtra("SELECTED_TAB", "INQUIRIES_TAB");
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(messageList.get(0) + " and others")
                .setTicker("Missed Inquiries")
//                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Lets Callback");
        inboxStyle.setBuilder(notificationBuilder);
        int count = 0;
        for (String oneMessage: messageList) {
            inboxStyle.addLine(oneMessage);
            if (messageList.size() > 5 && count == 4) {
                inboxStyle.setSummaryText("+" + (messageList.size()-5) + " more");
                break;
            }
            count++;
        }

        return notificationBuilder.build();
    }
}
