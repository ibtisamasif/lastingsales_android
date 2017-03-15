package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

/**
 * Created by ibtisam on 12/14/2016.
 */

public class TagNonBusiness extends BroadcastReceiver {
    public static final String TAG = "TagNonBusiness";
    LSContact tempContact;


    @Override
    public void onReceive(Context context, Intent intent) {
        String contactPhone = intent.getStringExtra("number");
        String contactName = intent.getStringExtra("name");

        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
        LSContact checkContact;
        checkContact = LSContact.getContactFromNumber(intlNum);
        if (checkContact != null) {
            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                checkContact.setPhoneOne(contactPhone);
                checkContact.setContactName(contactName);
                checkContact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
                checkContact.save();
                int notificationId = intent.getIntExtra("notificationId", 0);
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notificationId);
            }
        }
        else{
            tempContact = new LSContact();
            tempContact.setPhoneOne(contactPhone);
            tempContact.setContactName(contactName);
            tempContact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
            tempContact.save();
            int notificationId = intent.getIntExtra("notificationId", 0);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
    }
}
