package com.example.muzafarimran.lastingsales.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 3/4/2017.
 */

public class NotificationBuilder {
     public static void showTagNumberPopup(Context ctx, String number) {
        NotificationManager mNotificationManager;
        String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
        LSContact tempContact = LSContact.getContactFromNumber(intlNumber);

        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            String name = tempContact.getContactName();
            Long contact_id = tempContact.getId();
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createFollowUpNotification(ctx, intlNumber, contact_id));

        } else if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_UNTAGGED)) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));
        } else if (tempContact == null) {
            Log.d(ctx.getClass().getName(), "showTagNumberPopupIfNeeded: tempContact is NULL");
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createTagNotification(ctx, intlNumber));
        }
    }
}
