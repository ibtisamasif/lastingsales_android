package com.example.muzafarimran.lastingsales.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditNewFollowupActivity;
import com.example.muzafarimran.lastingsales.activities.AddEditNoteActivity;
import com.example.muzafarimran.lastingsales.activities.ContactDetailsTabActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.receivers.FollowupNotiCancelBtnReceiver;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class CallEndNotification {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "CallEndNotification";

    public static Notification createFollowUpNotification(Context ctx, String intlNumber, LSContact contact) {
// get from LSCONTACT first

        String number_or_name = "";
        number_or_name = contact.getContactName();
        number_or_name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, intlNumber);
        if (number_or_name == null) {
            number_or_name = intlNumber;
        }
        if (intlNumber == null || intlNumber.equals("")) {
            intlNumber = "0";
        }

        Intent cancelIntent = new Intent(ctx, FollowupNotiCancelBtnReceiver.class);
        cancelIntent.putExtra("notificationId", NOTIFICATION_ID);
        PendingIntent cancelpIntent = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), cancelIntent, 0);

        Intent detailsActivityIntent = new Intent(ctx, ContactDetailsTabActivity.class);
        detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contact.getId() + "");
        PendingIntent pDetailsActivityIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), detailsActivityIntent, 0);

        Intent intentNote = new Intent(ctx, AddEditNoteActivity.class);
        intentNote.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_ADD_NEW_NOTE);
        intentNote.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact.getId());
        PendingIntent pIntentNote = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intentNote, 0);

        Intent intentFollow = new Intent(ctx, AddEditNewFollowupActivity.class);
        intentFollow.putExtra(AddEditNewFollowupActivity.ACTIVITY_LAUNCH_MODE, AddEditNewFollowupActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
        intentFollow.putExtra(AddEditNewFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact.getId() + "");
        PendingIntent pIntentFollow = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), intentFollow, 0);

        Notification.Builder notificationBuilder = new Notification.Builder(ctx)
                .setContentIntent(pDetailsActivityIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_notification))
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(number_or_name)
                .addAction(R.drawable.ic_note_png, "Add Note", pIntentNote)
//                .addAction(R.drawable.cancel, "Cancel", cancelpIntent)
                .addAction(R.drawable.ic_followup_png, "Add Follow Up", pIntentFollow)
                .setContentText("What would you like to do?")
//                .setFullScreenIntent(pIntentFollow, true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        return notificationBuilder.build();
    }


//    public static Notification createTagNotification(Context ctx, String intlNumber) {
//
//        String projectToken = MixpanelConfig.projectToken;
//        MixpanelAPI mixpanel = MixpanelAPI.getInstance(ctx, projectToken);
//        try {
//            mixpanel.track("Lead From Notification - Shown");
//        } catch (Exception e) {
//            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
//        }
//
//        String number_or_name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, intlNumber);
//        if (number_or_name == null) {
//            number_or_name = intlNumber;
//        }
//        if (intlNumber == null || intlNumber.equals("")) {
//            intlNumber = "0";
//        }
//        //NonBusinessIntent
//        Intent IgnoredIntent = new Intent(ctx, TagAsIgnored.class);
//        IgnoredIntent.putExtra("number", intlNumber);
//        IgnoredIntent.putExtra("name", number_or_name);
//        IgnoredIntent.putExtra("notificationId", NOTIFICATION_ID);
//        PendingIntent pIntentNonBusiness = PendingIntent.getBroadcast(ctx, (int) System.currentTimeMillis(), IgnoredIntent, 0);
//
////        //CollegueIntent
////        Intent collegueIntent = new Intent(ctx, TagNumberAndAddFollowupActivity.class);
////        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
////        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_BUSINESS);
////        collegueIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
////        nonBusinessIntent.putExtra("notificationId", NOTIFICATION_ID);
////        PendingIntent pIntentCollegue = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), collegueIntent, 0);
//
//        //SalesIntent
//        Intent leadIntent = new Intent(ctx, AddEditLeadActivity.class);
//        leadIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
//        leadIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
//        leadIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, "");
//        leadIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_NOTIFICATION);
//        PendingIntent pIntentSales = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis(), leadIntent, 0);
//
//        Notification.Builder notificationBuilder = new Notification.Builder(ctx)
////                    .setContentIntent(pIntentSales)
//                .setSmallIcon(R.drawable.ic_notification_small)
//                .setPriority(Notification.PRIORITY_MAX)
//                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_notification))
//                .setWhen(System.currentTimeMillis())
//                .setContentTitle(number_or_name)
//                .setTicker("Lasting Sales")
//                .setStyle(new Notification.BigTextStyle().bigText("Please Specify Your Business Call?"))
//                .setAutoCancel(true)
//                .addAction(R.drawable.ic_ignore_png, "Ignore", pIntentNonBusiness)
//                .addAction(R.drawable.sales_icon_croped, "Track", pIntentSales)
////                    .addAction(R.drawable.non_business_icon, "", pIntentNonBusiness)
////                    .addAction(R.drawable.colleague_icon, "", pIntentCollegue)
////                    .addAction(R.drawable.sales_icon, "", pIntentSales)
//                .setDefaults(Notification.DEFAULT_SOUND)
//                .setDefaults(Notification.DEFAULT_VIBRATE)
//                .setContentText("Please Specify Your Business Call?");
////                    .getNotification();
//        return notificationBuilder.build();
//    }
}
