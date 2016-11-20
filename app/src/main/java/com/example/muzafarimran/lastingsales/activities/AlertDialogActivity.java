package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

/**
 * Created by ahmad on 13-Nov-16.
 */

public class AlertDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        Long followupId = 0l;
        if (extras != null) {
            followupId = Long.parseLong(extras.getString("followupid"));
        }

        TempFollowUp tempFollowUp = TempFollowUp.findById(TempFollowUp.class, followupId);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout

        View view = inflater.inflate(R.layout.popup_alert_dialog_layout, null);

        TextView message = (TextView) view.findViewById(R.id.followup_popup_note_text);
        message.setText(tempFollowUp.getNote());
        TextView contactName = (TextView) view.findViewById(R.id.followup_alert_popup_name);
        contactName.setText(tempFollowUp.getContact().getContactName());
        ImageButton callButton = (ImageButton) view.findViewById(R.id.followup_alert_call_button);
        callButton.setTag(tempFollowUp.getContact().getPhoneOne());
        callButton.setOnClickListener(new CallClickListener(this));


        builder.setTitle("Followup Due");
        builder.setCancelable(false);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialogActivity.this.finish();
                            }
                        }
                ).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                AlertDialogActivity.this.finish();
            }
        });
        builder.create();
        builder.show();

/*

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        NotificationCompat.Builder notificationCommon = buildNotificationCommon(getApplicationContext());

//        notificationCommon.notify();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(001, notificationCommon.build());



    }

    private static NotificationCompat.Builder buildNotificationCommon(Context _context) {
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
        .setContentTitle("Lastingsales Notification")
        .setContentText("this is content text");


        return builder;
    }


}