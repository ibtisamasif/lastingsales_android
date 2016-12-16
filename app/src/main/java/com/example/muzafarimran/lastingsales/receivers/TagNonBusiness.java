package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

/**
 * Created by ibtisam on 12/14/2016.
 */

public class TagNonBusiness extends BroadcastReceiver {
    public static final String TAG = "TagNonBusiness";
    LSContact tempContact;


    @Override
    public void onReceive(Context context, Intent intent) {
        String num = intent.getStringExtra("number");
        String name = intent.getStringExtra("name");
        tempContact = new LSContact();
        tempContact.setPhoneOne(num);
        tempContact.setContactName(name);
        tempContact.setContactType(LSContact.CONTACT_TYPE_PERSONAL);
        tempContact.save();
        int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
