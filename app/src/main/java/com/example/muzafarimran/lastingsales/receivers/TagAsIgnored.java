package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ibtisam on 12/14/2016.
 */

public class TagAsIgnored extends BroadcastReceiver {
    public static final String TAG = "TagAsIgnored";

    @Override
    public void onReceive(Context context, Intent intent) {
        String contactPhone = intent.getStringExtra("number");
        String contactName = intent.getStringExtra("name");
        IgnoredContact.AddAsIgnoredContact(contactPhone, contactName);
        int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}
