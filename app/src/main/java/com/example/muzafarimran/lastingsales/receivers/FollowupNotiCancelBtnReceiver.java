package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ibtisam on 12/6/2016.
 */

public class FollowupNotiCancelBtnReceiver extends BroadcastReceiver{
    public static final String TAG = "TAG";
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("CalledReceiver");
            int notificationId = intent.getIntExtra("notificationId", 0);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
    }
}
