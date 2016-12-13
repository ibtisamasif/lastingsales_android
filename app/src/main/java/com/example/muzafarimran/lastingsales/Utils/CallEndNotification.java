package com.example.muzafarimran.lastingsales.Utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.MainActivity;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.receivers.FollowupNotiCancelBtnReceiver;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class CallEndNotification {
    public static final int NOTIFICATION_ID = 1;
    public static Notification createNotification(Context ctx, String name) {
        //Create an Intent for the BroadcastReceiver
        Intent cancelIntent = new Intent(ctx, FollowupNotiCancelBtnReceiver.class);
        cancelIntent.putExtra("notificationId", NOTIFICATION_ID);
        //Create the PendingIntent
        PendingIntent cancelpIntent = PendingIntent.getBroadcast(ctx, 0, cancelIntent, 0);
        Intent intent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
        intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE,TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
        //TODO New launch Mode to be created
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
    public static Notification createFollowUpNotification(Context ctx, String intlNumber) {
        String number_or_name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, intlNumber);
        if (number_or_name == null) {
            number_or_name = intlNumber;
        }
        if (intlNumber.equals("") || intlNumber == null){
            intlNumber = "0";
        }
        //MainIntent
        Intent homeIntent = new Intent(ctx, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), homeIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        //NonBusinessIntent
        Intent nonBusinessIntent = new Intent(ctx, FollowupNotiCancelBtnReceiver.class);
        //TODO
        nonBusinessIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
        nonBusinessIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
        PendingIntent pIntentNonBusiness = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), nonBusinessIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //CollegueIntent
        Intent collegueIntent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
        PendingIntent pIntentCollegue = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), collegueIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //SalesIntent
        Intent salesIntent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
        salesIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
        salesIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
        salesIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
        PendingIntent pIntentSales = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), salesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Notification.Builder builder = new Notification.Builder(ctx);
            Notification notification;
            notification = builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.menu_icon_home_selected)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.menu_icon_home_selected))
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(number_or_name)
                    .setTicker("Lasting Sales")
                    .setStyle(new Notification.BigTextStyle().bigText(number_or_name))
                    .setAutoCancel(true)
                    .addAction(R.drawable.non_business_icon, "", pIntentNonBusiness)
                    .addAction(R.drawable.colleague_icon, "", pIntentCollegue)
                    .addAction(R.drawable.sales_icon, "", pIntentSales)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentText("Please Specify Your Business Call?")
                    .getNotification();

            return notification;
        } else {
            return null;
        }
    }
}
