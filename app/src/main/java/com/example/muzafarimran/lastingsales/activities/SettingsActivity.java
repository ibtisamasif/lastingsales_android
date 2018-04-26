package com.example.muzafarimran.lastingsales.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SettingsManager;
import com.example.muzafarimran.lastingsales.receivers.HourlyAlarmReceiver;
import com.example.muzafarimran.lastingsales.utils.HuaweiProtectedAppsModule;

import java.util.Calendar;

/**
 * Created by ibtisam on 7/23/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    private SettingsManager settingsManager;
    private Toolbar toolbar;
    private Switch swFlyer;
    private Switch swTagDialogPopup;
    private Switch swHourlyAlarmNotification;
    private Switch swDefaultLead;
    private TextView tvAddToProtectedApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.ic_notification_small);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Settings");
        settingsManager = new SettingsManager(this);
        swFlyer = (Switch) findViewById(R.id.swFlyer);
        swTagDialogPopup = (Switch) findViewById(R.id.swTagDialogPopup);
        swHourlyAlarmNotification = (Switch) findViewById(R.id.swHourlyAlarmNotification);
        swDefaultLead = (Switch) findViewById(R.id.swDefaultLead);
        TextView tvAddToProtectedApp = (TextView) findViewById(R.id.tvAddToProtectedApp);
        tvAddToProtectedApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HuaweiProtectedAppsModule(SettingsActivity.this).AlertIfHuaweiDevice("Huawei Protected Apps","This app requires to be enables in 'Protected Apps' to work in background","Dont show again","PROTECTED APPS","CANCEL");
            }
        });

        if (settingsManager.getKeyStateFlyer()) {
            swFlyer.setChecked(true);
        } else {
            swFlyer.setChecked(false);
        }

        //attach a listener to check for changes in state
        swFlyer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    settingsManager.setKeyStateFlyer(true);
                    Toast.makeText(SettingsActivity.this, "Flyer Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    settingsManager.setKeyStateFlyer(false);
                    Toast.makeText(SettingsActivity.this, "Flyer Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (settingsManager.getKeyStateCallEndDialog()) {
            swTagDialogPopup.setChecked(true);
        } else {
            swTagDialogPopup.setChecked(false);
        }

        //attach a listener to check for changes in state
        swTagDialogPopup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    settingsManager.setKeyStateCallEndDialog(true);
                    Toast.makeText(SettingsActivity.this, "Tag Dialog Popup Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    settingsManager.setKeyStateCallEndDialog(false);
                    Toast.makeText(SettingsActivity.this, "Tag Dialog Popup Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (settingsManager.getKeyStateHourlyNotification()) {
            swHourlyAlarmNotification.setChecked(true);
        } else {
            swHourlyAlarmNotification.setChecked(false);
        }

        //attach a listener to check for changes in state
        swHourlyAlarmNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    settingsManager.setKeyStateHourlyNotification(true);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 10); // For 10am
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SettingsActivity.this, HourlyAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR * 2, pi);
                    }
                    Toast.makeText(SettingsActivity.this, "Hourly inquiry notification Enabled Start App for changes to take effect", Toast.LENGTH_SHORT).show();
                } else {
                    settingsManager.setKeyStateHourlyNotification(false);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SettingsActivity.this, HourlyAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE);
                    if (pendingIntent != null) {
                        Log.d("myAlarmLog", "Hourly inquiry notification Disabled");
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                        Intent myIntent = new Intent(SettingsActivity.this, HourlyAlarmReceiver.class);
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0 , myIntent, 0);
                        if (alarmManager != null) {
                            alarmManager.cancel(pendingIntent);
                        }
                    }
                    Toast.makeText(SettingsActivity.this, "Hourly inquiry notification Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (settingsManager.getKeyStateDefaultLead()) {
            swDefaultLead.setChecked(true);
        } else {
            swDefaultLead.setChecked(false);
        }

        //attach a listener to check for changes in state
        swDefaultLead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    settingsManager.setKeyStateDefaultLead(true);
                    Toast.makeText(SettingsActivity.this, "Default will be Lead", Toast.LENGTH_SHORT).show();
                } else {
                    settingsManager.setKeyStateDefaultLead(false);
                    Toast.makeText(SettingsActivity.this, "Default will be unlabeled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
