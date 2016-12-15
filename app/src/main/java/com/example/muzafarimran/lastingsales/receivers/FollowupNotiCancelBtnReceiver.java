package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class FollowupNotiCancelBtnReceiver extends BroadcastReceiver{
    public static final String NUMBER_TO_TAG = "phone_number";
        @Override
        public void onReceive(Context context, Intent intent) {

            int notificationId = intent.getIntExtra("notificationId", 0);
            // if you want cancel notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
    }
}
