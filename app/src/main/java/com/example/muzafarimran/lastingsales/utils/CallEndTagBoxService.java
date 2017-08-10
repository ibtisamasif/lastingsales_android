package com.example.muzafarimran.lastingsales.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.activities.TagNotificationDialogActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.service.AddEditLeadService;

/**
 * Created by ibtisam on 7/22/2017.
 */

public class CallEndTagBoxService {
    private static final String TAG = "CallEndTagBoxService";

    public static void checkShowCallPopupNew(Context ctx, String name, String number) {
        SettingsManager settingsManager = new SettingsManager(ctx);
        NotificationManager mNotificationManager;
        String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, number);
        LSContact tempContact = LSContact.getContactFromNumber(intlNumber);

        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createFollowUpNotification(ctx, intlNumber, tempContact));

        } else if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
            Log.d(TAG, "showTagNumberPopupIfNeeded: tempContact is UNLABELED");
            if (settingsManager.getKeyStateCallEndDialog()) {
                String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, number);
//        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
                Intent intent = new Intent(ctx, AddEditLeadService.class);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, internationalNumber);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_NAME, name);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_ID, ""); //backward compatibility
                ctx.startService(intent);
            }
        } else if (tempContact == null) {
            Log.d(TAG, "showTagNumberPopupIfNeeded: tempContact is NULL");
            if (settingsManager.getKeyStateCallEndDialog()) {
                String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, number);
//        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(ctx, internationalNumber);
                Intent intent = new Intent(ctx, AddEditLeadService.class);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, internationalNumber);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_NAME, name);
                intent.putExtra(TagNotificationDialogActivity.TAG_LAUNCH_MODE_CONTACT_ID, ""); //backward compatibility
                ctx.startService(intent);
            }
        }
    }
}
