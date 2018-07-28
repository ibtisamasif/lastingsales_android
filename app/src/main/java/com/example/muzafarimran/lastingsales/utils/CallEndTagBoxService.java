package com.example.muzafarimran.lastingsales.utils;

import android.app.NotificationManager;
import android.content.Context;

import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.chatheadbubble.AddEditLeadServiceBubbleHelper;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

/**
 * Created by ibtisam on 7/22/2017.
 */

public class CallEndTagBoxService {

    public static void checkShowCallPopupNew(Context ctx, String name, String number) {
        SettingsManager settingsManager = new SettingsManager(ctx);
        NotificationManager mNotificationManager;
        String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, number);
        LSContact tempContact = LSContact.getContactFromNumber(intlNumber);
        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(CallEndNotification.NOTIFICATION_ID, CallEndNotification.createFollowUpNotification(ctx, intlNumber, tempContact));
        } else if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
            if (settingsManager.getKeyStateCallEndDialog()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, number);
                        AddEditLeadServiceBubbleHelper.getInstance(ctx.getApplicationContext()).hide();
                        AddEditLeadServiceBubbleHelper.getInstance(ctx.getApplicationContext()).show(LSContact.CONTACT_TYPE_SALES, internationalNumber, name);
                    }
                });
            }
        } else if (tempContact == null) {
            if (settingsManager.getKeyStateCallEndDialog()) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(ctx, number);
                        AddEditLeadServiceBubbleHelper.getInstance(ctx.getApplicationContext()).hide();
                        AddEditLeadServiceBubbleHelper.getInstance(ctx.getApplicationContext()).show(LSContact.CONTACT_TYPE_SALES, internationalNumber, name);
                    }
                });
            }
        }
    }
}
