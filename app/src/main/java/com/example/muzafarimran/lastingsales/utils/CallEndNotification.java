package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.activities.AddEditNoteActivity;
import com.example.muzafarimran.lastingsales.activities.AddNewFollowUpsActivity;
import com.example.muzafarimran.lastingsales.receivers.FollowupNotiCancelBtnReceiver;
import com.example.muzafarimran.lastingsales.receivers.TagAsIgnored;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class CallEndNotification {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "CallEndNotification";

    public static Notification createFollowUpNotification(Context ctx, String intlNumber, Long contact_id) {
//TODO get from LSCONTACT first
        String number_or_name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, intlNumber);
        if (number_or_name == null) {
            number_or_name = intlNumber;
        }
        if (intlNumber == null || intlNumber.equals("")) {
            intlNumber = "0";
        }

        Intent cancelIntent = new Intent(ctx, FollowupNotiCancelBtnReceiver.class);
        cancelIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent cancelpIntent = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), cancelIntent, 0);

        Intent intentNote = new Intent(ctx, AddEditNoteActivity.class);
        intentNote.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_ADD_NEW_NOTE);
        intentNote.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact_id);
        PendingIntent pIntentNote = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intentNote, 0);

        Intent intentFollow = new Intent(ctx, AddNewFollowUpsActivity.class);
        intentFollow.putExtra(AddNewFollowUpsActivity.ACTIVITY_LAUNCH_MODE, AddNewFollowUpsActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
        intentFollow.putExtra(AddNewFollowUpsActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact_id);
        PendingIntent pIntentFollow = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intentFollow, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(ctx)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_notification))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(number_or_name)
                .addAction(R.drawable.ic_notes_black, "Add Note", pIntentNote)
//                .addAction(R.drawable.cancel, "Cancel", cancelpIntent)
                .addAction(R.drawable.ic_followup_black, "Add Follow Up", pIntentFollow)
                .setContentText("What would you like to do?")
//                .setFullScreenIntent(pIntentFollow, true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    public static Notification createTagNotification(Context ctx, String intlNumber) {

        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(ctx, projectToken);
        try {
            mixpanel.track("Lead from notification - Shown");
        } catch (Exception e) {
            Log.e("MYAPP", "Unable to add properties to JSONObject", e);
        }

        String number_or_name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, intlNumber);
        if (number_or_name == null) {
            number_or_name = intlNumber;
        }
        if (intlNumber == null || intlNumber.equals("")) {
            intlNumber = "0";
        }
        //NonBusinessIntent
        Intent IgnoredIntent = new Intent(ctx, TagAsIgnored.class);
        IgnoredIntent.putExtra("number", intlNumber);
        IgnoredIntent.putExtra("name", number_or_name);
        IgnoredIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent pIntentNonBusiness = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), IgnoredIntent, 0);

//        //CollegueIntent
//        Intent collegueIntent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
//        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
//        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_BUSINESS);
//        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
//        nonBusinessIntent.putExtra("notificationId", NOTIFICATION_ID);
//        PendingIntent pIntentCollegue = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), collegueIntent, 0);

        //SalesIntent
        Intent leadIntent = new Intent(ctx, AddEditLeadActivity.class);
        leadIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
        leadIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
        leadIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, "");
        leadIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_NOTIFICATION);
        PendingIntent pIntentSales = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), leadIntent, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(ctx)
//                    .setContentIntent(pIntentSales)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_notification))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(number_or_name)
                .setTicker("Lasting Sales")
                .setStyle(new Notification.BigTextStyle().bigText("Please Specify Your Business Call?"))
                .setAutoCancel(true)
                .addAction(R.drawable.ic_untrack_darkgray, "Ignore", pIntentNonBusiness)
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
