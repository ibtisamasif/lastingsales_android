package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.receivers.AlarmReceiver;
import com.example.muzafarimran.lastingsales.sync.DataSenderNew;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by ibtisam on 1/23/2017.
 */

public class AddNewFollowUpsActivity extends Activity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "AddNewFollowUpsActivity";

    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";

    public static final String LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP = "launch_mode_edit_existing_followup";
    public static final String LAUNCH_MODE_ADD_NEW_FOLLOWUP = "launch_mode_add_new_followup";

    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    public static final String TAG_LAUNCH_MODE_FOLLOWUP_ID = "followup_id";

    private static final String TITLE_ADD_FOLLOWUP = "Add Followup";
    private static final String TITLE_EDIT_FOLLOWUP = "Edit Followup";

    String launchMode = LAUNCH_MODE_ADD_NEW_FOLLOWUP;

    boolean editingMode = false;
    long contactIdLong = -1;
    long followupIdLong = -1;

    Button bOneWeek;
    Button bThreeDays;
    Button bTomorrow;
    Button bDate;
    Button bTime;
    Button bCancel;
    Button bSave;
    EditText etFollowupTitleText;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    private LSContact selectedContact = null;
    private TempFollowUp selectedFollowup = null;
    private TextView tvTitleFollowupPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_followups);
        tvTitleFollowupPopup = (TextView) findViewById(R.id.tvTitleFollowupPopup);
        bOneWeek = (Button) findViewById(R.id.bOneWeekFollowupPopup);
        bThreeDays = (Button) findViewById(R.id.bThreeDaysFollowupPopup);
        bTomorrow = (Button) findViewById(R.id.bTomorrowFollowupPopup);
        bDate = (Button) findViewById(R.id.bDateFollowupPopup);
        bTime = (Button) findViewById(R.id.bTimeFollowupPopup);
        etFollowupTitleText = (EditText) findViewById(R.id.etTitleTextFollowupPopup);
        bCancel = (Button) findViewById(R.id.bCancelFollowup);
        bSave = (Button) findViewById(R.id.bSaveFollowup);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getString(ACTIVITY_LAUNCH_MODE);
        }

        if (launchMode.equals(LAUNCH_MODE_ADD_NEW_FOLLOWUP)) {
            Toast.makeText(this, "Add new Followup", Toast.LENGTH_SHORT).show();
            tvTitleFollowupPopup.setText(TITLE_ADD_FOLLOWUP);
            editingMode = false;
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
            }
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            showAddFollowupLayout();

        } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP)) {

            tvTitleFollowupPopup.setText(TITLE_EDIT_FOLLOWUP);
            editingMode = true;
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
            }
            String followupId = bundle.getString(TAG_LAUNCH_MODE_FOLLOWUP_ID);
            if (followupId != null && !followupId.equals("")) {
                followupIdLong = Long.parseLong(followupId);
            }
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            selectedFollowup = TempFollowUp.findById(TempFollowUp.class, followupIdLong);
            showAddFollowupLayout();
            setDateTimeFromMiliseconds(selectedFollowup.getDateTimeForFollowup());
            if (etFollowupTitleText != null) {
                etFollowupTitleText.setText(selectedFollowup.getTitle());
            }
        }

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (launchMode.equals(AddNewFollowUpsActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP)) {

                    String titleText = null;
                    String noteText = null;
                    TempFollowUp tempFollowUp = new TempFollowUp();
                    if (etFollowupTitleText != null) {
                        titleText = etFollowupTitleText.getText().toString();
                        tempFollowUp.setTitle(titleText);
                    } else {
                        titleText = "Empty";
                    }
                    if (year != 0 && day != 0 && hour != 0 && minute != 0) {
                        Calendar dateTimeForFollowup = Calendar.getInstance();
                        dateTimeForFollowup.set(Calendar.YEAR, year);
                        dateTimeForFollowup.set(Calendar.MONTH, month);
                        dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, day);
                        dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, 14);
                        dateTimeForFollowup.set(Calendar.MINUTE, 18);
                        Log.d(TAG, "New Alarm func: Year="+year+" Month="+month+" DAY="+day+" Hour="+hour+" Minute="+minute);
                        tempFollowUp.setContact(selectedContact);
                        tempFollowUp.setTitle(titleText);
                        tempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                        tempFollowUp.setSyncStatus(SyncStatus.SYNC_STATUS_NOT_SYNCED);
                        tempFollowUp.save();
                        setAlarm(getApplicationContext(), tempFollowUp);
                        DataSenderNew dataSenderNew = new DataSenderNew(getApplicationContext());
                        dataSenderNew.execute();
                    }
                    finish();
                } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP)) {
                    String titleText = null;
                    TempFollowUp tempFollowUp = selectedFollowup;
                    String noteText = null;
                    if (etFollowupTitleText != null) {
                        titleText = etFollowupTitleText.getText().toString();
                        tempFollowUp.setTitle(titleText);
                    } else {
                        titleText = "Empty";
                    }
                    if (year != 0 && day != 0 && hour != 0 && minute != 0) {
                        Calendar dateTimeForFollowup = Calendar.getInstance();
                        dateTimeForFollowup.set(Calendar.YEAR, year);
                        dateTimeForFollowup.set(Calendar.MONTH, month);
                        dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, day);
                        dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, hour);
                        dateTimeForFollowup.set(Calendar.MINUTE, minute);
//                TempFollowUp tempFollowUp = new TempFollowUp(note, dateAndTimeForAlarm.getTimeInMillis(), selectedLSContact);
                        tempFollowUp.setContact(selectedContact);
                        tempFollowUp.setTitle(titleText);
                        tempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                        tempFollowUp.setSyncStatus(SyncStatus.SYNC_STATUS_NOT_SYNCED);
                        tempFollowUp.save();
                        setAlarm(getApplicationContext(), tempFollowUp);
                        DataSenderNew dataSenderNew = new DataSenderNew(getApplicationContext());
                        dataSenderNew.execute();
                    }
                    finish();
                    Toast.makeText(AddNewFollowUpsActivity.this, "Followup Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showAddFollowupLayout() {

        Calendar now = Calendar.getInstance();
        if (bTomorrow != null) {
            bTomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.DAY_OF_MONTH, 1);
                    setDateTimeFromMiliseconds(now.getTimeInMillis());
                    day = now.get(Calendar.DAY_OF_MONTH);
                    month = (now.get(Calendar.MONTH));
                    year = now.get(Calendar.YEAR);
                }
            });
        }
        if (bThreeDays != null) {
            bThreeDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.DAY_OF_MONTH, 3);
                    setDateTimeFromMiliseconds(now.getTimeInMillis());
                    day = now.get(Calendar.DAY_OF_MONTH);
                    month = (now.get(Calendar.MONTH));
                    year = now.get(Calendar.YEAR);
                }
            });
        }
        if (bOneWeek != null) {
            bOneWeek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.DAY_OF_MONTH, 7);
                    setDateTimeFromMiliseconds(now.getTimeInMillis());
                    day = now.get(Calendar.DAY_OF_MONTH);
                    month = (now.get(Calendar.MONTH));
                    year = now.get(Calendar.YEAR);
                }
            });
        }
        day = now.get(Calendar.DAY_OF_MONTH);
        month = (now.get(Calendar.MONTH));
        year = now.get(Calendar.YEAR);
        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
        setDateTimeFromMiliseconds(now.getTimeInMillis());
        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddNewFollowUpsActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setCancelable(false);
                datePickerDialog.setTitle("Select Date for Followup");
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        bTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddNewFollowUpsActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                timePickerDialog.setCancelable(false);
                timePickerDialog.setTitle("Select Time for Followup");
                timePickerDialog.setMinTime(now.HOUR_OF_DAY, now.MINUTE, now.SECOND);
                timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
            }
        });
    }

    private void setDateTimeFromMiliseconds(Long miliSeconds) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(miliSeconds);
        if (bDate != null) {
            String date = now.get(Calendar.DAY_OF_MONTH) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.YEAR);
            bDate.setText(date);
        }
        if (bTime != null) {
            bTime.setText(now.get(Calendar.HOUR_OF_DAY) + " : " + now.get(Calendar.MINUTE));
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        if (bTime != null) {
            bTime.setText(hourOfDay + ":" + minute);
            hour = hourOfDay;
            minute = minute;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year + "";
        if (bDate != null) {
            bDate.setText(date);
            this.year = year;
            this.month = monthOfYear;
            this.day = dayOfMonth;
        }
    }

    public void setAlarm(Context context, TempFollowUp tempFollowUp) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent;
        int interval = 8000;
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Calendar dateAndTimeForAlarm = Calendar.getInstance();
        /*int year = dateForFollowUp.get(Calendar.YEAR);
        int month = dateForFollowUp.get(Calendar.MONTH);
        int day = dateForFollowUp.get(Calendar.DAY_OF_MONTH);
        int hour = timeForFollowUp.get(Calendar.HOUR_OF_DAY);
        int minute = timeForFollowUp.get(Calendar.MINUTE);
        int seconds = 0;*/
        dateAndTimeForAlarm.set(year, month, day, hour, minute, 0);
        Intent aint = new Intent(context, AlarmReceiver.class);
//        String note = etNote.getText().toString();
//        String note = "Hello";
        aint.putExtra("followupid", tempFollowUp.getId() + "");
//        aint.putExtra("message","This is message from followup");
        pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(tempFollowUp.getId().toString()), aint, PendingIntent.FLAG_UPDATE_CURRENT);
                         /* Retrieve a PendingIntent that will perform a broadcast */
//        Intent alarmIntent = new Intent(activity, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);
        manager.setExact(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
//        manager.set(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
}