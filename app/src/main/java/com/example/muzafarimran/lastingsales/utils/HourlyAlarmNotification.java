package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.NavigationDrawerActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by ibtisam on 6/21/2017.
 */

@Deprecated
public class HourlyAlarmNotification {
    public static final String TAG = "DayStartHighlightAlarmNotification";
    public static final int NOTIFICATION_ID = 2;
    private static Bitmap bitmap;

    public static Notification createHourlyAlarmNotificationForSingle(Context context, String title, String message, String number) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small);

        LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(number);
        if (lsContactProfile != null ){
            if(lsContactProfile.getSocial_image() != null){
                String profileURL = lsContactProfile.getSocial_image();
                bitmap = getBitmapFromURL(profileURL);
//                Bitmap bitmap = getBitmapFromURL("https://scontent.flhe2-1.fna.fbcdn.net/v/t1.0-0/s526x395/12799304_1313184535374322_1645561860173890315_n.jpg?_nc_eui2=v1%3AAeGcfSfKWSlL7BRp5sLlH1dLm0pTNKM_iT0PuJSj1vcZgn1KCHpuuaca9RprkJSxrnUfT0nlPSvUSeT62H1U9TisbswqEFXMtnBs5PV3LEu6XMdLpDYXFwZyPjIMLHMjxXw&oh=791a0360c622be5caee86ddb60dfce7c&oe=59FFB8F1");
            }
        }

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
                .setLargeIcon(bitmap)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
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

    public static Notification createHourlyAlarmNotificationForList(Context context, String title, List<String> messageList, String numberOfContactHavingImage) {

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small);

        if (numberOfContactHavingImage !=null && !numberOfContactHavingImage.equals("")){
            LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(numberOfContactHavingImage);
            if (lsContactProfile != null ){
                if(lsContactProfile.getSocial_image() != null){
                    String profileURL = lsContactProfile.getSocial_image();
                    bitmap = getBitmapFromURL(profileURL);
//                Bitmap bitmap = getBitmapFromURL("https://scontent.flhe2-1.fna.fbcdn.net/v/t1.0-0/s526x395/12799304_1313184535374322_1645561860173890315_n.jpg?_nc_eui2=v1%3AAeGcfSfKWSlL7BRp5sLlH1dLm0pTNKM_iT0PuJSj1vcZgn1KCHpuuaca9RprkJSxrnUfT0nlPSvUSeT62H1U9TisbswqEFXMtnBs5PV3LEu6XMdLpDYXFwZyPjIMLHMjxXw&oh=791a0360c622be5caee86ddb60dfce7c&oe=59FFB8F1");
                }
            }
        }

        Intent inquiriesIntent = new Intent(context, NavigationDrawerActivity.class);
        inquiriesIntent.putExtra("SELECTED_TAB", "INQUIRIES_TAB");
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), inquiriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(bitmap)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
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

    private static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
