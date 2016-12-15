package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 12/14/2016.
 */

public class TagNonBusiness extends BroadcastReceiver{
    public static final String TAG = "TagNonBusiness";
    public static final String TAG_NUMBER = "number";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive:");

        String num = intent.getStringExtra("TAG_NUMBER");
        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(context, num);

        LSContact tempContact = new LSContact();
        tempContact.setPhoneOne(num);
        if(name!=null){
            tempContact.setContactName(name);
        }
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
