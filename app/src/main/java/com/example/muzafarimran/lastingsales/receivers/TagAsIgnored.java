package com.example.muzafarimran.lastingsales.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.utils.IgnoredContact;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

/**
 * Created by ibtisam on 12/14/2016.
 */

public class TagAsIgnored extends BroadcastReceiver {
    public static final String TAG = "TagAsIgnored";

    @Override
    public void onReceive(Context context, Intent intent) {
        String contactPhone = intent.getStringExtra("number");
        String contactName = intent.getStringExtra("name");
        IgnoredContact.AddAsIgnoredContact(context, contactPhone, contactName);
        int notificationId = intent.getIntExtra("notificationId", 0);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);

        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, projectToken);
        try {
            JSONObject props = new JSONObject();
            props.put("type", "ignored");
            mixpanel.track("Lead From Notification - Clicked", props); // change
        } catch (Exception e) {
            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
        }
    }
}
