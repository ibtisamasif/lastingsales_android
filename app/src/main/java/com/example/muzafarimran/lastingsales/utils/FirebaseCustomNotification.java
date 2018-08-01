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
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.app.ClassManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 10/9/2017.
 */

public class FirebaseCustomNotification {
    public static final int NOTIFICATION_ID = 3;

    public static final String NOTIFICATION_TYPE_NOTE = "type_note";
    public static final String NOTIFICATION_TYPE_CONTACT = "type_contact";
    public static final String NOTIFICATION_TYPE_COMMENT = "type_comment";

    public static Notification createFirebaseAssignedLeadNotification(Context context, String name, String lead_id) {

        Intent intent;
        PendingIntent pContentIntent;
        LSContact lsContact = LSContact.getContactFromServerId(lead_id);
        if (lsContact != null) {
            intent = new Intent(context, ClassManager.getClass(ClassManager.CONTACT_DETAILS_TAB_ACTIVITY));
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ContactDetailsTabActivity.KEY_SOURCE, ContactDetailsTabActivity.KEY_SOURCE_NOTIFICATION);
            intent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, lsContact.getId() + "");
            intent.putExtra(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB, "0");
            pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            intent = new Intent(context, NavigationBottomMainActivity.class);
//        intent.putExtra("KEY_SELECTED_TAB", "LEADS_TAB");
            pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(name)
                .setTicker("New lead assigned")
//                .setStyle(new Notification.BigTextStyle().bigText(name))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText("New lead assigned");
        return notificationBuilder.build();
    }

    public static Notification createFirebaseFacebookLeadNotification(Context context, String name) {

        Intent intent = new Intent(context, NavigationBottomMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("KEY_SELECTED_TAB", "LEADS_TAB");
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(name)
                .setTicker("Lead from facebook")
                .setStyle(new Notification.BigTextStyle().bigText(name))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText("Lead from facebook");
        return notificationBuilder.build();
    }

    public static Notification createFirebaseInquiriesNotification(Context context, String message) {

        Intent intent = new Intent(context, NavigationBottomMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("KEY_SELECTED_TAB", "INQUIRIES_TAB");
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

    public static Notification createFirebaseCommentNotification(Context context, String user_name, String message, String type, String lead_id, String lead_name) {
        Intent intent = new Intent(context, NavigationBottomMainActivity.class);
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (type) {
            case NOTIFICATION_TYPE_COMMENT: {
                LSContact lsContact = LSContact.getContactFromServerId(lead_id);
                if (lsContact != null) {
                    intent = new Intent(context, ClassManager.getClass(ClassManager.CONTACT_DETAILS_TAB_ACTIVITY));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ContactDetailsTabActivity.KEY_SOURCE, ContactDetailsTabActivity.KEY_SOURCE_NOTIFICATION);
                    intent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, lsContact.getId() + "");
                    intent.putExtra(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB, "3");
                    pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
            break;
            default:
                intent = new Intent(context, NavigationBottomMainActivity.class);
                pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
        }
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_account_circle_white_img))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Message on " + lead_name)
                .setTicker("one new comment")
//                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(user_name + " : " + message);
        return notificationBuilder.build();
    }

    public static Notification createFirebaseCustomNotification(Context context, String message, String type, String id) {
        Intent intent = new Intent(context, NavigationBottomMainActivity.class);
        PendingIntent pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (type) {
            case NOTIFICATION_TYPE_NOTE: {
                LSContact lsContact = LSContact.getContactFromServerId(id);
                if (lsContact != null) {
                    intent = new Intent(context, ClassManager.getClass(ClassManager.CONTACT_DETAILS_TAB_ACTIVITY));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ContactDetailsTabActivity.KEY_SOURCE, ContactDetailsTabActivity.KEY_SOURCE_NOTIFICATION);
                    intent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, lsContact.getId() + "");
                    intent.putExtra(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB, "1");
                    pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
            break;
            case NOTIFICATION_TYPE_CONTACT: {
                LSContact lsContact = LSContact.getContactFromServerId(id);
                if (lsContact != null) {
                    if (lsContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                        intent = new Intent(context, ClassManager.getClass(ClassManager.CONTACT_DETAILS_TAB_ACTIVITY));
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(ContactDetailsTabActivity.KEY_SOURCE, ContactDetailsTabActivity.KEY_SOURCE_NOTIFICATION);
                        intent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, lsContact.getId() + "");
                        pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                }
            }
            break;
            case NOTIFICATION_TYPE_COMMENT: {
                LSContact lsContact = LSContact.getContactFromServerId(id);
                if (lsContact != null) {
                    intent = new Intent(context, ClassManager.getClass(ClassManager.CONTACT_DETAILS_TAB_ACTIVITY));
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(ContactDetailsTabActivity.KEY_SOURCE, ContactDetailsTabActivity.KEY_SOURCE_NOTIFICATION);
                    intent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, lsContact.getId() + "");
                    intent.putExtra(ContactDetailsTabActivity.KEY_SET_SELECTED_TAB, "3");
                    pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
            break;
            default:
                intent = new Intent(context, NavigationBottomMainActivity.class);
                pContentIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
        }
        Notification.Builder notificationBuilder = new Notification.Builder(context)
                .setContentIntent(pContentIntent)
                .setSmallIcon(R.drawable.ic_notification_small)
                .setPriority(Notification.PRIORITY_MAX)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification_small))
                .setWhen(System.currentTimeMillis())
                .setContentTitle("LastingSales")
                .setTicker("LastingSales")
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(message);
        return notificationBuilder.build();
    }
}
