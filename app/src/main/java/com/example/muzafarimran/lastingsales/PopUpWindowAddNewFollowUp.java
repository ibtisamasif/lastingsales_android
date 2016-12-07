package com.example.muzafarimran.lastingsales;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.activities.LSContactChooserActivity;
import com.example.muzafarimran.lastingsales.adapters.FollowupsListAdapter;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.receivers.AlarmReceiver;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.util.Calendar;


/**
 * Created by ahmad on 28-Oct-16.
 */


public class PopUpWindowAddNewFollowUp implements
        View.OnClickListener,
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    public static final int CONTACT_CHOOSER_REQUEST_CODE = 11;
    Calendar dateForFollowUp, timeForFollowUp;
    FollowupsListAdapter followupsListAdapter;
    private LSContact selectedLSContact = null;
    private Activity activity = null;
    private View addNewFollowupViewForRefrence = null;
    private TextView tvOkButton = null;
    private TextView tvCancelButton = null;
    private Button bChangeButton = null;
    private EditText etNote = null;
    private TextView tvDate, tvTime, tvName, tvNumber;
    private PendingIntent pendingIntent;
    //    Image views for folder icons
    private ImageView ivNormalFolder = null;
    private Contact selectedContact = null;

    public PopUpWindowAddNewFollowUp(Activity activity) {
        this.activity = activity;
    }

    public PopUpWindowAddNewFollowUp(Activity activity, FollowupsListAdapter followupsListAdapter) {
        this.activity = activity;
        this.followupsListAdapter = followupsListAdapter;
    }

    public void setContactName(String name) {
        tvName.setText(name);

    }

    public void displayPopUpWindow() {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addNewFollowupViewForRefrence = layoutInflater.inflate(R.layout.popup_add_new_followup_layout, null);
        final PopupWindow addNewFollowupPopUpWindow = new PopupWindow(addNewFollowupViewForRefrence, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        addNewFollowupPopUpWindow.showAtLocation(activity.findViewById(R.id.ll_main_layout), Gravity.CENTER, 0, 0);
        tvOkButton = (TextView) addNewFollowupViewForRefrence.findViewById(R.id.popup_add_new_followup_b_ok);
        tvCancelButton = (TextView) addNewFollowupViewForRefrence.findViewById(R.id.popup_add_new_followup_b_cancel);
        bChangeButton = (Button) addNewFollowupViewForRefrence.findViewById(R.id.bChangeAddNewFollowupPopup);
        activity.findViewById(R.id.main_screen_main_parent_layout).setAlpha(new Float(0.2));
        etNote = (EditText) addNewFollowupViewForRefrence.findViewById(R.id.et_enter_note);
        tvDate = (TextView) addNewFollowupViewForRefrence.findViewById(R.id.tvDateAddNewFollowup);
        tvTime = (TextView) addNewFollowupViewForRefrence.findViewById(R.id.tvTimeAddNewFollowup);
        tvName = (TextView) addNewFollowupViewForRefrence.findViewById(R.id.tvNameAddNewFollowup);
        tvNumber = (TextView) addNewFollowupViewForRefrence.findViewById(R.id.tvNumberAddNewFollowup);
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                PopUpWindowAddNewFollowUp.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(activity.getFragmentManager(), "Datepickerdialog");
        dateForFollowUp = null;
        timeForFollowUp = null;
        tvOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dateForFollowUp == null || timeForFollowUp == null) {
                    Toast.makeText(activity, "Please select a Date & Time For follow-up first!", Toast.LENGTH_LONG).show();
                    return;
                }
                addNewFollowupPopUpWindow.dismiss();
                activity.findViewById(R.id.main_screen_main_parent_layout).setAlpha(new Float(1));
//                call to setAlarm method activity is being passed
                Calendar dateAndTimeForAlarm = Calendar.getInstance();
                int year = dateForFollowUp.get(Calendar.YEAR);
                int month = dateForFollowUp.get(Calendar.MONTH);
                int day = dateForFollowUp.get(Calendar.DAY_OF_MONTH);
                int hour = timeForFollowUp.get(Calendar.HOUR_OF_DAY);
                int minute = timeForFollowUp.get(Calendar.MINUTE);
                int seconds = 0;
                dateAndTimeForAlarm.set(year, month, day, hour, minute, seconds);
                String note = etNote.getText().toString();
                TempFollowUp tempFollowUp = new TempFollowUp();
//                TempFollowUp tempFollowUp = new TempFollowUp(note, dateAndTimeForAlarm.getTimeInMillis(), selectedLSContact);
                tempFollowUp.setContact(selectedLSContact);
                tempFollowUp.setNote(note);
                tempFollowUp.setDateTimeForFollowup(dateAndTimeForAlarm.getTimeInMillis());
                tempFollowUp.save();
                followupsListAdapter.add(tempFollowUp);
                setAlarm(activity, tempFollowUp);
            }
        });
        tvCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewFollowupPopUpWindow.dismiss();
                activity.findViewById(R.id.main_screen_main_parent_layout).setAlpha(new Float(1));
            }
        });
        bChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        PopUpWindowAddNewFollowUp.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setCancelable(false);
                dpd.setMinDate(now);
                dpd.setTitle("Select Date for Followup");
                dpd.show(activity.getFragmentManager(), "Datepickerdialog");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }


    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        timeForFollowUp = Calendar.getInstance();
        timeForFollowUp.set(dateForFollowUp.get(Calendar.YEAR), dateForFollowUp.get(Calendar.MONTH), dateForFollowUp.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
        tvTime.setText(hourOfDay + " : " + minute);
        activity.startActivityForResult(new Intent(activity, LSContactChooserActivity.class), CONTACT_CHOOSER_REQUEST_CODE);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        tvDate.setText(dayOfMonth + " - " + (monthOfYear + 1) + " - " + year);
        Calendar now = Calendar.getInstance();
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                PopUpWindowAddNewFollowUp.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );

        timePickerDialog.setCancelable(false);
        timePickerDialog.setTitle("Select Time for Followup");
        timePickerDialog.setMinTime(now.HOUR_OF_DAY, now.MINUTE, now.SECOND);
        timePickerDialog.show(activity.getFragmentManager(), "Datepickerdialog");
        dateForFollowUp = Calendar.getInstance();
        dateForFollowUp.set(year, monthOfYear, dayOfMonth);
    }

    public void setAlarm(Context context, TempFollowUp tempFollowUp) {
        AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Calendar dateAndTimeForAlarm = Calendar.getInstance();
        int year = dateForFollowUp.get(Calendar.YEAR);
        int month = dateForFollowUp.get(Calendar.MONTH);
        int day = dateForFollowUp.get(Calendar.DAY_OF_MONTH);
        int hour = timeForFollowUp.get(Calendar.HOUR_OF_DAY);
        int minute = timeForFollowUp.get(Calendar.MINUTE);
        int seconds = 0;
        dateAndTimeForAlarm.set(year, month, day, hour, minute, seconds);
        Intent aint = new Intent(context, AlarmReceiver.class);
        String note = etNote.getText().toString();
        aint.putExtra("followupid", tempFollowUp.getId() + "");
//        aint.putExtra("message","This is message from followup");
        pendingIntent = PendingIntent.getBroadcast(activity, Integer.parseInt(tempFollowUp.getId().toString()), aint, PendingIntent.FLAG_UPDATE_CURRENT);
                         /* Retrieve a PendingIntent that will perform a broadcast */
//        Intent alarmIntent = new Intent(activity, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);
//        manager.setExact(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        Toast.makeText(activity, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void setContactNumber(String contactNumber) {
        tvNumber.setText(contactNumber);
    }

    public void setSelectedLSContact(LSContact contactID) {
        this.selectedLSContact = contactID;
    }
}