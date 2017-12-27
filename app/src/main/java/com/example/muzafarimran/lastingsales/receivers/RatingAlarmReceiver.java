package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.utils.RatingAlarmNotification;

import java.util.Calendar;

/**
 * Created by ibtisam on 9/18/2017.
 */

@Deprecated
public class RatingAlarmReceiver extends WakefulBroadcastReceiver {
    public static final String TAG = "myAlarmLog";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive:Rating Alarm Fired");
        Log.d("Inquiry", "onReceive:Rating Alarm Fired");

        long milisecondsIn1Day = 86400000;

//        long milliSecondsIn1Min = 30000; // 30 seconds for now
//        milisecondsIn1Day = milliSecondsIn1Min; // Comment it

        long now = Calendar.getInstance().getTimeInMillis();
        long oneDayAgoTimestamp = now - milisecondsIn1Day;
        SessionManager sessionManager = new SessionManager(context);
        String lastAppVisitTime = sessionManager.getLastAppVisit();
        Long lastAppVisitTimeLong = Long.parseLong(lastAppVisitTime);
        Log.d(TAG, "onReceive: getLastAppVisit: " + lastAppVisitTimeLong);
        Log.d(TAG, "onReceive: oneDayAgoTimestamp: " + oneDayAgoTimestamp);
        if (lastAppVisitTimeLong > oneDayAgoTimestamp) {
            String message = "We miss you!";
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(RatingAlarmNotification.NOTIFICATION_ID, RatingAlarmNotification.createAlarmNotification(context, "Ratings", message));
        }
        // Refresh Service once daily.
//        context.startService(new Intent(context, CallDetectionService.class));
    }
}
