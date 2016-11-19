package com.example.muzafarimran.lastingsales.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AlertDialogActivity;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends WakefulBroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        Notify(context, intent,"Alarm", "Alarm has been raised");
    }


    private void Notify(Context context, Intent intent, String notificationTitle, String notificationMessage) {

        /*
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        @SuppressWarnings("deprecation")

        Notification notification = new Notification(R.drawable.abc,"New Message", System.currentTimeMillis());
        Intent notificationIntent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

        notification.setLatestEventInfo(context, notificationTitle,notificationMessage, pendingIntent);
        notificationManager.notify(9999, notification);

*/

//        Commented below code to code popup
/*


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification myNotication;




            Intent notificationIntent = new Intent("com.rj.notitfications.SECACTIVITY");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(context);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("Lasting sales Notification");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.user_avatar_icon_gray_border);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(false);
        builder.setSubText("This is subtext...");   //API level 16
        builder.setNumber(100);
        builder.build();

        myNotication = builder.getNotification();
        notificationManager.notify(11, myNotication);

*/




        Intent i = new Intent(context, AlertDialogActivity.class);

        Bundle extras = intent.getExtras();
        if (extras!= null)
        {
            Long followupId = Long.parseLong(extras.getString("followupid"));
            i.putExtra("followupid", followupId+"");
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

    }


}
