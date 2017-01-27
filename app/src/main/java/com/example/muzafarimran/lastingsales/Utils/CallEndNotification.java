package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddLeadActivity;
import com.example.muzafarimran.lastingsales.activities.AddNewFollowUpsActivity;
import com.example.muzafarimran.lastingsales.receivers.FollowupNotiCancelBtnReceiver;
import com.example.muzafarimran.lastingsales.receivers.TagNonBusiness;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class CallEndNotification {
    private static final String TAG = "CallEndNotification";
    public static final int NOTIFICATION_ID = 1;
    public static Notification createFollowUpNotification(Context ctx, String name , Long contact_id) {

        Intent cancelIntent = new Intent(ctx, FollowupNotiCancelBtnReceiver.class);
        cancelIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent cancelpIntent = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), cancelIntent, 0);

        Intent intent = new Intent(ctx, AddNewFollowUpsActivity.class);
        intent.putExtra(AddNewFollowUpsActivity.ACTIVITY_LAUNCH_MODE,AddNewFollowUpsActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
        intent.putExtra(AddNewFollowUpsActivity.TAG_LAUNCH_MODE_CONTACT_ID , contact_id);
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
    public static Notification createTagNotification(Context ctx, String intlNumber) {
        String number_or_name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, intlNumber);
        if (number_or_name == null) {
            number_or_name = intlNumber;
        }
        if (intlNumber.equals("") || intlNumber == null){
            intlNumber = "0";
        }
        //NonBusinessIntent
        Intent nonBusinessIntent = new Intent(ctx, TagNonBusiness.class);
        nonBusinessIntent.putExtra("number", intlNumber);
        nonBusinessIntent.putExtra("name", number_or_name);
        nonBusinessIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pIntentNonBusiness = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), nonBusinessIntent, 0);

//        //CollegueIntent
//        Intent collegueIntent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
//        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
//        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_COLLEAGUE);
//        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
//        nonBusinessIntent.putExtra("notificationId", NOTIFICATION_ID);
//        PendingIntent pIntentCollegue = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), collegueIntent, 0);

        //SalesIntent
        Intent salesIntent = new Intent(ctx, AddLeadActivity.class);
        salesIntent.putExtra(AddLeadActivity.ACTIVITY_LAUNCH_MODE, AddLeadActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
        salesIntent.putExtra(AddLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
        PendingIntent pIntentSales = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), salesIntent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(ctx)
//                    .setContentIntent(pIntentSales)
                    .setSmallIcon(R.drawable.menu_icon_home_selected)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_menu_icon_home_large))
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(number_or_name)
                    .setTicker("Lasting Sales")
                    .setStyle(new Notification.BigTextStyle().bigText("Do You Want To Tag?"))
                    .setAutoCancel(true)
                .addAction(R.drawable.non_business_icon_croped, "Untrack", pIntentNonBusiness)
                .addAction(R.drawable.sales_icon_croped, "Track", pIntentSales)
//                    .addAction(R.drawable.non_business_icon, "", pIntentNonBusiness)
//                    .addAction(R.drawable.colleague_icon, "", pIntentCollegue)
//                    .addAction(R.drawable.sales_icon, "", pIntentSales)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentText("Please Specify Your Business Call?");
//                    .getNotification();
            return notificationBuilder.build();
    }
}
