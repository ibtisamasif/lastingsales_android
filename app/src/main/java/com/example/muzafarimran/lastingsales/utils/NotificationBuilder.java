package com.example.muzafarimran.lastingsales.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.activities.TagNotificationDialogActivity;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class NotificationBuilder {
     public static void showTagNumberPopup(Context ctx, String contactName, String contactNumber) {
        NotificationManager mNotificationManager;
        String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, contactNumber);
        LSContact tempContact = LSContact.getContactFromNumber(intlNumber);

        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            String name = tempContact.getContactName();
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createFollowUpNotification(ctx, intlNumber, tempContact));

        } else if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
            Intent intent = new Intent(ctx, TagNotificationDialogActivity.class);
            intent.putExtra(TagNotificationDialogActivity.ACTIVITY_LAUNCH_MODE, TagNotificationDialogActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_BUSINESS);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_NAME, contactName);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_ID, ""+tempContact.getId()); //backward compatibility & May be needed in future
//            intent.putExtra(TagNotificationDialogActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_NOTIFICATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
//            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));

        } else if (tempContact == null) {
            Intent intent = new Intent(ctx, TagNotificationDialogActivity.class);
            intent.putExtra(TagNotificationDialogActivity.ACTIVITY_LAUNCH_MODE, TagNotificationDialogActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_BUSINESS);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, intlNumber);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_NAME, contactName);
            intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_ID, ""); //backward compatibility
//            intent.putExtra(TagNotificationDialogActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_NOTIFICATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
//            Log.d(ctx.getClass().getName(), "showTagNumberPopupIfNeeded: tempContact is NULL");
//            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));
        }
    }
}
