package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class FollowupNotiCancelBtnReceiver extends BroadcastReceiver{
    public static final String NUMBER_TO_TAG = "phone_number";
        @Override
        public void onReceive(Context context, Intent intent) {
//TODO
            LSContact tempContact = new LSContact();
            tempContact.setPhoneOne(NUMBER_TO_TAG);
            tempContact.setContactType(LSContact.CONTACT_TYPE_PERSONAL);
            tempContact.save();
            //Update Previous Record of User.
            PhoneNumberAndCallUtils.updateAllCallsOfThisContact(tempContact);

            int notificationId = intent.getIntExtra("notificationId", 0);
            // if you want cancel notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
    }
}
