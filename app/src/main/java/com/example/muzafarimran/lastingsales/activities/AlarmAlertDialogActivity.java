package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

/**
 * Created by ibtisam on 6/15/2017.
 */

public class AlarmAlertDialogActivity extends Activity{
    public static final String TAG = "AlarmAlertDialogActivit";
    private static NotificationCompat.Builder buildNotificationCommon(Context _context) {
        Log.d(TAG, "InquiryNotificaionBuilder");
        Log.d("InquiryManager", "InquiryNotificaionBuilder");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(_context);
        builder.setWhen(System.currentTimeMillis());
        //Vibration
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        //LED
        builder.setLights(Color.RED, 3000, 3000);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Ton
        builder.setSound(alarmSound);
        builder.setSmallIcon(R.drawable.call_icon)
                .setContentTitle("Missed Call Reminder")
                .setContentText("There is a pending inquiry");
        return builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: AlarmAlertDialogActivity");
        Log.d("InquiryManager", "onCreate: AlarmAlertDialogActivity");
        Bundle extras = getIntent().getExtras();
        Long inquiryId = 0l;
        if (extras != null) {
            inquiryId = Long.parseLong(extras.getString("inquiryId"));
        }

        LSInquiry tempInquiry = LSInquiry.findById(LSInquiry.class, inquiryId);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        View view = inflater.inflate(R.layout.popup_alert_dialog_layout, null);
        TextView message = (TextView) view.findViewById(R.id.followup_popup_note_text);
        try {
            TextView contactName = (TextView) view.findViewById(R.id.followup_alert_popup_name);
            if(tempInquiry.getContactName()!=null){
                contactName.setText(tempInquiry.getContactName());
            }else {
                contactName.setText(tempInquiry.getContactNumber());
            }
            ImageButton callButton = (ImageButton) view.findViewById(R.id.followup_alert_call_button);
            callButton.setTag(tempInquiry.getContactNumber());
            callButton.setOnClickListener(new CallClickListener(this));
            builder.setTitle("Inquiry Pending");
            builder.setCancelable(false);
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    AlarmAlertDialogActivity.this.finish();
                                }
                            }
                    ).setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    AlarmAlertDialogActivity.this.finish();
                }
            });
            builder.create();
            builder.show();
            NotificationCompat.Builder notificationCommon = buildNotificationCommon(getApplicationContext());
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(001, notificationCommon.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
