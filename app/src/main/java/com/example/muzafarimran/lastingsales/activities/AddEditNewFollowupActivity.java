package com.example.muzafarimran.lastingsales.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.calendar.MyCalendarEvent;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.receivers.AlarmReceiver;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * Created by ibtisam on 1/23/2017.
 */

public class AddEditNewFollowupActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "AddEditFollowUps";
    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";
    public static final String LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP = "launch_mode_edit_existing_followup";
    public static final String LAUNCH_MODE_ADD_NEW_FOLLOWUP = "launch_mode_add_new_followup";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    public static final String TAG_LAUNCH_MODE_FOLLOWUP_ID = "followup_id";
    private static final String TITLE_ADD_FOLLOWUP = "Add Reminder";
    private static final String TITLE_EDIT_FOLLOWUP = "Edit Reminder";

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
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    private LSContact selectedContact = null;
    private TempFollowUp selectedFollowup = null;
    //    private TextView tvTitleFollowupPopup;
    private String lastDayOfMonth;
    private String lastDayOfYear;
    private String otherEditText;
    long myCurrentDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_new_followup);
//        tvTitleFollowupPopup = (TextView) findViewById(R.id.tvTitleFollowupPopup);
        bOneWeek = findViewById(R.id.bOneWeekFollowupPopup);
        bThreeDays = findViewById(R.id.bThreeDaysFollowupPopup);
        bTomorrow = findViewById(R.id.bTomorrowFollowupPopup);
        bDate = findViewById(R.id.bDateFollowupPopup);
        bTime = findViewById(R.id.bTimeFollowupPopup);
        etFollowupTitleText = findViewById(R.id.etFollowupTitleText);
        bCancel = findViewById(R.id.bCancelFollowup);
        bSave = findViewById(R.id.bSaveFollowup);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getString(ACTIVITY_LAUNCH_MODE);
        }

        if (launchMode.equals(LAUNCH_MODE_ADD_NEW_FOLLOWUP)) {
//            Toast.makeText(this, "Add new Followup", Toast.LENGTH_SHORT).show();
//            tvTitleFollowupPopup.setText(TITLE_ADD_FOLLOWUP);
            editingMode = false;
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
            }
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            if (selectedContact.getContactName() != null && !selectedContact.getContactName().equals("")) {
                etFollowupTitleText.setText("Followup " + selectedContact.getContactName());
            } else {
                etFollowupTitleText.setText("Followup " + selectedContact.getPhoneOne());
            }
            showAddFollowupLayout();

        } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP)) {

//            tvTitleFollowupPopup.setText(TITLE_EDIT_FOLLOWUP);
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
                if (launchMode.equals(AddEditNewFollowupActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP)) {

                    String titleText = null;
                    String noteText = null;
                    TempFollowUp TempFollowUp = new TempFollowUp();
                    if (etFollowupTitleText != null) {
                        titleText = etFollowupTitleText.getText().toString();
                        TempFollowUp.setTitle(titleText);
                    } else {
                        titleText = "Empty";
                    }
                    if (mYear != 0 && mDay != 0 && mHour != 0 && mMinute != 0) {
                        Calendar dateTimeForFollowup = Calendar.getInstance();
                        dateTimeForFollowup.set(Calendar.YEAR, mYear);
                        dateTimeForFollowup.set(Calendar.MONTH, mMonth);
                        dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, mDay);
                        dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, mHour);
                        dateTimeForFollowup.set(Calendar.MINUTE, mMinute);
                        Log.d(TAG, "New Alarm func: Year=" + mYear + " Month=" + mMonth + " DAY=" + mDay + " Hour=" + mHour + " Minute=" + mMinute);
                        TempFollowUp.setContact(selectedContact);
                        TempFollowUp.setTitle(titleText);
                        TempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                        TempFollowUp.setSyncStatus(SyncStatus.SYNC_STATUS_FOLLOWUP_ADDED_NOT_SYNCED);
                        TempFollowUp.save();
//                        setAlarm(getApplicationContext(), TempFollowUp);

                        long selected_year = (long) (mYear);
                        long selected_month = (long) (mMonth + 1);
                        long selected_day = (long) (mDay);
                        long selected_hour = (long) (mHour);
                        long selected_minute = (long) (mMinute);

                        String mySelectedDateTime = selected_year
                                + String.format("-%02d", selected_month) + String.format("-%02d", selected_day)
                                + String.format(" %02d", selected_hour) + String.format(":%02d", selected_minute);

                        String endDatetime = selected_year
                                + String.format("-%02d", selected_month) + String.format("-%02d", selected_day)
                                + String.format(" %02d", selected_hour) + String.format(":%02d", selected_minute + 30) + ":00";

                        Log.d(TAG, "mySelectedDateTime: " + mySelectedDateTime);
                        long selectedDateTime = MyDateTimeStamp.dateTimeToLong(mySelectedDateTime + ":00");
                        Log.d(TAG, "selectedDateTime:  " + selectedDateTime);
                        myCurrentDateTime = Calendar.getInstance().getTimeInMillis();
                        Log.d(TAG, "myCurrentDateTime: " + myCurrentDateTime);

                        if (!mySelectedDateTime.equals(myCurrentDateTime) && selectedDateTime > myCurrentDateTime) {
                            String title = "LastingSales " + selectedContact.getContactName() + "(" + selectedContact.getPhoneOne() + ")";
                            String location = "";
                            String startDatetime = mySelectedDateTime + ":00";
                            String description = titleText;
                            try {

                                int calendarID = (int) contactIdLong;
                                MyCalendarEvent.setGoogleEvent(AddEditNewFollowupActivity.this, startDatetime, endDatetime, title, description, location, calendarID);

                                Log.d(TAG, "ID: " + calendarID);
                                Log.d(TAG, "Title: " + title);
                                Log.d(TAG, "Descr: " + description);
                                Log.d(TAG, "Start: " + startDatetime);
                                Log.d(TAG, "End: " + endDatetime);
                                Log.d(TAG, "Location: " + location);
                            } catch (Exception e) {
                                Log.d(TAG, "" + e);
                            }
                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                            dataSenderAsync.run();
                        } else {
                            Toast.makeText(AddEditNewFollowupActivity.this, "Selected Date/Time has passed already", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP)) {
                    String titleText = null;
                    TempFollowUp TempFollowUp = selectedFollowup;
                    String noteText = null;
                    if (etFollowupTitleText != null) {
                        titleText = etFollowupTitleText.getText().toString();
                        TempFollowUp.setTitle(titleText);
                    } else {
                        titleText = "Empty";
                    }
                    if (mYear != 0 && mDay != 0 && mHour != 0 && mMinute != 0) {
                        Calendar dateTimeForFollowup = Calendar.getInstance();
                        dateTimeForFollowup.set(Calendar.YEAR, mYear);
                        dateTimeForFollowup.set(Calendar.MONTH, mMonth);
                        dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, mDay);
                        dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, mHour);
                        dateTimeForFollowup.set(Calendar.MINUTE, mMinute);
                        TempFollowUp.setContact(selectedContact);
                        TempFollowUp.setTitle(titleText);
                        TempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                        TempFollowUp.setSyncStatus(SyncStatus.SYNC_STATUS_FOLLOWUP_EDIT_NOT_SYNCED);
                        TempFollowUp.save();
//                        setAlarm(getApplicationContext(), TempFollowUp);

                        long selected_year = (long) (mYear);
                        long selected_month = (long) (mMonth + 1);
                        long selected_day = (long) (mDay);
                        long selected_hour = (long) (mHour);
                        long selected_minute = (long) (mMinute);

                        String mySelectedDateTime = selected_year
                                + String.format("-%02d", selected_month) + String.format("-%02d", selected_day)
                                + String.format(" %02d", selected_hour) + String.format(":%02d", selected_minute);

                        String endDatetime = selected_year
                                + String.format("-%02d", selected_month) + String.format("-%02d", selected_day)
                                + String.format(" %02d", selected_hour) + String.format(":%02d", selected_minute + 30) + ":00";

                        long selectedDateTime = MyDateTimeStamp.dateTimeToLong(mySelectedDateTime + ":00");
                        myCurrentDateTime = Calendar.getInstance().getTimeInMillis();
                        if (!mySelectedDateTime.equals(myCurrentDateTime) && selectedDateTime > myCurrentDateTime) {
                            String title = "LastingSales " + selectedContact.getContactName() + "(" + selectedContact.getPhoneOne() + ")"; //crash reported but now we have disabled followup editing functionality.
                            String location = "";
                            String startDatetime = mySelectedDateTime + ":00";
                            String description = titleText;
                            try {
                                int calendarID = (int) contactIdLong;
                                MyCalendarEvent.setGoogleEvent(AddEditNewFollowupActivity.this, startDatetime, endDatetime, title, description, location, calendarID);

                                Log.d(TAG, "ID: " + calendarID);
                                Log.d(TAG, "Title: " + title);
                                Log.d(TAG, "Descr: " + description);
                                Log.d(TAG, "Start: " + startDatetime);
                                Log.d(TAG, "End: " + endDatetime);
                                Log.d(TAG, "Location: " + location);
                            } catch (Exception e) {
                                Log.d(TAG, "" + e);
                            }
                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                            dataSenderAsync.run();
                        } else {
                            Toast.makeText(AddEditNewFollowupActivity.this, "Selected Date/Time has passed already", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                    Toast.makeText(AddEditNewFollowupActivity.this, "Followup Added", Toast.LENGTH_SHORT).show();
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
                    mDay = now.get(Calendar.DAY_OF_MONTH);
                    mMonth = (now.get(Calendar.MONTH));
                    mYear = now.get(Calendar.YEAR);
                    Toast.makeText(AddEditNewFollowupActivity.this, "Tomorrow", Toast.LENGTH_SHORT).show();
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
                    mDay = now.get(Calendar.DAY_OF_MONTH);
                    mMonth = (now.get(Calendar.MONTH));
                    mYear = now.get(Calendar.YEAR);
                    Toast.makeText(AddEditNewFollowupActivity.this, "3 Days", Toast.LENGTH_SHORT).show();
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
                    mDay = now.get(Calendar.DAY_OF_MONTH);
                    mMonth = (now.get(Calendar.MONTH));
                    mYear = now.get(Calendar.YEAR);
                    Toast.makeText(AddEditNewFollowupActivity.this, "1 Week", Toast.LENGTH_SHORT).show();
                }
            });
        }
        mDay = now.get(Calendar.DAY_OF_MONTH);
        mMonth = (now.get(Calendar.MONTH));
        mYear = now.get(Calendar.YEAR);
        mHour = now.get(Calendar.HOUR_OF_DAY);
        mMinute = now.get(Calendar.MINUTE);
        setDateTimeFromMiliseconds(now.getTimeInMillis());
        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddEditNewFollowupActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setCancelable(true);
                datePickerDialog.setTitle("Select Date for Reminder");
                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
            }
        });
        bTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddEditNewFollowupActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                timePickerDialog.setCancelable(true);
                timePickerDialog.setTitle("Select Time for Reminder");
//                timePickerDialog.setMinTime(now.get(Calendar.HOUR), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
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
//            Log.d(TAG, "setDateTimeFromMiliseconds: (now.get(Calendar.HOUR) + \" : \" + now.get(Calendar.MINUTE): " + now.get(Calendar.HOUR_OF_DAY) + " : " + now.get(Calendar.MINUTE));
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (bTime != null) {
            bTime.setText(hourOfDay + ":" + minute);
//            Log.d(TAG, "onTimeSet: (hourOfDay + \":\" + minute): " + (hourOfDay + ":" + minute));
            mHour = hourOfDay;
            mMinute = minute;
        }
    }

//    @Override
//    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
//        if (bTime != null) {
//            bTime.setText(hourOfDay + ":" + minute);
//            mHour = hourOfDay;
//            mMinute = minute;
//        }
//    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year + "";
        if (bDate != null) {
            bDate.setText(date);
            this.mYear = year;
            this.mMonth = monthOfYear;
            this.mDay = dayOfMonth;
        }
    }

    public void setAlarm(Context context, TempFollowUp tempFollowUp) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent;
        int interval = 8000;
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Calendar dateAndTimeForAlarm = Calendar.getInstance();
        /*int mYear = dateForFollowUp.get(Calendar.YEAR);
        int mMonth = dateForFollowUp.get(Calendar.MONTH);
        int mDay = dateForFollowUp.get(Calendar.DAY_OF_MONTH);
        int mHour = timeForFollowUp.get(Calendar.HOUR_OF_DAY);
        int mMinute = timeForFollowUp.get(Calendar.MINUTE);
        int seconds = 0;*/
        dateAndTimeForAlarm.set(mYear, mMonth, mDay, mHour, mMinute, 0);
        Log.d(TAG, "Set Alarm func: Year=" + mYear + " Month=" + mMonth + " DAY=" + mDay + " Hour=" + mHour + " Minute=" + mMinute);
        Intent aint = new Intent(context, AlarmReceiver.class);

        aint.putExtra("followupid", tempFollowUp.getId() + "");
//        aint.putExtra("message","This is message from followup");
        pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(tempFollowUp.getId().toString()), aint, PendingIntent.FLAG_UPDATE_CURRENT);
        /* Retrieve a PendingIntent that will perform a broadcast */
//        Intent alarmIntent = new Intent(activity, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            // Do something for KITKAT and above versions
            manager.set(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
//            manager.setExact(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        } else {
            // do something for phones running an SDK before KITKAT
            manager.set(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        }
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
}