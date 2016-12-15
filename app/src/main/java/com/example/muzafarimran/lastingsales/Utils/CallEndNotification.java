package com.example.muzafarimran.lastingsales.Utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.receivers.FollowupNotiCancelBtnReceiver;
import com.example.muzafarimran.lastingsales.receivers.TagNonBusiness;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class CallEndNotification {
    public static final int NOTIFICATION_ID = 1;
    public static Notification createNotification(Context ctx, String name , Long contact_id) {
        //Create an Intent for the BroadcastReceiver
        Intent cancelIntent = new Intent(ctx, FollowupNotiCancelBtnReceiver.class);
        cancelIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent cancelpIntent = PendingIntent.getBroadcast(ctx, 0, cancelIntent, 0);
        Intent intent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
        intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE,TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
        intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID , contact_id);
        //TODO New launch Mode to be created
        PendingIntent pIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intent, 0);
        Notification.Builder notificationBuilder = new Notification.Builder(ctx)
                .setSmallIcon(R.drawable.menu_icon_home_selected)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_menu_icon_home_large))
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
        Intent homeIntent = new Intent(ctx, CallEndNotification.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), homeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //NonBusinessIntent
        Intent nonBusinessIntent = new Intent(ctx, TagNonBusiness.class);
        nonBusinessIntent.putExtra(TagNonBusiness.TAG_NUMBER, intlNumber);
        nonBusinessIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pIntentNonBusiness = PendingIntent.getBroadcast(ctx, 0, nonBusinessIntent, 0);

        //CollegueIntent
        Intent collegueIntent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_COLLEAGUE);
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
            notification = builder.setContentIntent(pIntentSales)
                    .setSmallIcon(R.drawable.menu_icon_home_selected)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_menu_icon_home_large))
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
