package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.PopUpWindowAddNewFollowUp;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.receivers.AlarmReceiver;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;


public class TagNumberAndAddFollowupActivity extends Activity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    //    Constants
    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";
    public static final String LAUNCH_MODE_IMPORT_CONTACT = "launch_mode_import_contact_from_phonebook";
    public static final String LAUNCH_MODE_ADD_NEW_CONTACT = "launch_mode_add_new_contact";
    public static final String LAUNCH_MODE_EDIT_EXISTING_CONTACT = "launch_mode_edit_existing_contact";
    public static final String LAUNCH_MODE_TAG_PHONE_NUMBER = "launch_mode_tag_phone_number";
    public static final String TAG_LAUNCH_MODE_PHONE_NUMBER = "phone_number";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    public static final String TAG_LAUNCH_MODE_CONTACT_TYPE = "contact_type";

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    String launchMode = LAUNCH_MODE_ADD_NEW_CONTACT;
    String selectedContactType = LSContact.CONTACT_TYPE_SALES;
    boolean editingMode = false;
    long contactIdLong=-1;
    String phoneNumberFromLastActivity;
    String preSelectedContactType;
    int year, month, day, hour, minute;
    private Uri uriContact;
    private String contactID;
    private EditText etContactName;
    private EditText etContactPhone;
    private Button bSave;
    private ImageView ibAddNote;
    private ImageView ibAddFollowup;
    private RelativeLayout addNoteActionLayout;
    private RelativeLayout addFollowupActionLayout;
    private LinearLayout noteContainerLayout;
    private LinearLayout followupContainerLayout;
    private LayoutInflater inflater;
    private EditText etNoteText;
    private Button bOneWeek;
    private Button bThreeDays;
    private Button bTomorrow;
    private Button bDate;
    private Button bTime;
    private Button bColleagueRadio;
    private Button bSalesRadio;
    private LSContact selectedContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_number_and_add_followup);
        inflater = getLayoutInflater();
        year = month = day = hour = minute = 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getString(ACTIVITY_LAUNCH_MODE);
        }
        if (launchMode.equals(LAUNCH_MODE_IMPORT_CONTACT)) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            editingMode = false;
        } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
            editingMode = true;
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
            }
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
        } else if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
            phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
            selectedContactType = bundle.getString(TAG_LAUNCH_MODE_CONTACT_TYPE);
            selectRadioButton(selectedContactType);
            editingMode = false;
//            populating name and phone number below after findVieByIDs have been called
        } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
            editingMode = false;
        }
        etContactName = (EditText) findViewById(R.id.etNameFollowupPopup);
        etContactPhone = (EditText) findViewById(R.id.etNumberFollowupPopup);
        ibAddNote = (ImageView) findViewById(R.id.ivAddNote);
        ibAddFollowup = (ImageView) findViewById(R.id.ivAddFollowup);
        addNoteActionLayout = (RelativeLayout) findViewById(R.id.addNoteActionLayout);
        addFollowupActionLayout = (RelativeLayout) findViewById(R.id.addFollowupActionLayout);
        noteContainerLayout = (LinearLayout) findViewById(R.id.noteContainerLayout);
        followupContainerLayout = (LinearLayout) findViewById(R.id.followupContainer);
        Button bCancel = (Button) findViewById(R.id.bCancelFollowupPopup);
        bSave = (Button) findViewById(R.id.bSaveFollowupPopup);
        bSalesRadio = (Button) findViewById(R.id.bSalesRadio);
        bColleagueRadio = (Button) findViewById(R.id.bCollegueRadio);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        populating data number and name in edit texts when taging a number
//        number is being picked up from phone book if it exists there
        if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
            phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
            String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(phoneNumberFromLastActivity);
            String nameFromPhoneBook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), internationalNumber);
            etContactPhone.setText(internationalNumber);
            if (nameFromPhoneBook != null) {
                etContactName.setText(nameFromPhoneBook);
            }
        }
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (launchMode.equals(LAUNCH_MODE_IMPORT_CONTACT)) {
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                    }
                    if (contactPhone.equals("") || contactPhone.length() < 3) {
                        validation = false;
                        etContactPhone.setError("Invalid Number!");
                    }
                    if (validation && !editingMode) {
                        String noteText = null;
                        TempFollowUp tempFollowUp = new TempFollowUp();
                        LSContact tempContact = new LSContact();
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone));
                        tempContact.setContactType(selectedContactType);
                        tempContact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                        tempContact.save();
//  First checking if note is enabled so note can be gotten and passed in followup
                        if (etNoteText != null) {
                            noteText = etNoteText.getText().toString();
                            tempFollowUp.setNote(noteText);
                        } else {
                            noteText = "Empty";
                        }
                        if (year != 0 && month != 0 && day != 0 && hour != 0 && minute != 0) {
                            Calendar dateTimeForFollowup = Calendar.getInstance();
                            dateTimeForFollowup.set(Calendar.YEAR, year);
                            dateTimeForFollowup.set(Calendar.MONTH, month);
                            dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, day);
                            dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, hour);
                            dateTimeForFollowup.set(Calendar.MINUTE, minute);

//                TempFollowUp tempFollowUp = new TempFollowUp(note, dateAndTimeForAlarm.getTimeInMillis(), selectedLSContact);
                            tempFollowUp.setContact(tempContact);
                            tempFollowUp.setNote(noteText);
                            tempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                            tempFollowUp.save();
                            setAlarm(getApplicationContext(), tempFollowUp);
                        }
                        finish();
                    }
                } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                    }
                    if (contactPhone.equals("") || contactPhone.length() < 3) {
                        validation = false;
                        etContactPhone.setError("Invalid Number!");
                    }
                    if (validation && !editingMode) {
                        String noteText = null;
                        TempFollowUp tempFollowUp = new TempFollowUp();
                        LSContact tempContact = new LSContact();
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone));
                        tempContact.setContactType(selectedContactType);
                        tempContact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                        tempContact.save();
//  First checking if note is enabled so note can be gotten and passed in followup
                        if (etNoteText != null) {
                            noteText = etNoteText.getText().toString();
                            tempFollowUp.setNote(noteText);
                        } else {
                            noteText = "Empty";
                        }
                        if (year != 0 && month != 0 && day != 0 && hour != 0 && minute != 0) {
                            Calendar dateTimeForFollowup = Calendar.getInstance();
                            dateTimeForFollowup.set(Calendar.YEAR, year);
                            dateTimeForFollowup.set(Calendar.MONTH, month);
                            dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, day);
                            dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, hour);
                            dateTimeForFollowup.set(Calendar.MINUTE, minute);

//                TempFollowUp tempFollowUp = new TempFollowUp(note, dateAndTimeForAlarm.getTimeInMillis(), selectedLSContact);
                            tempFollowUp.setContact(tempContact);
                            tempFollowUp.setNote(noteText);
                            tempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                            tempFollowUp.save();
                            setAlarm(getApplicationContext(), tempFollowUp);
                        }
                        finish();
                    }
                } else if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                    }
                    if (contactPhone.equals("") || contactPhone.length() < 3) {
                        validation = false;
                        etContactPhone.setError("Invalid Number!");
                    }
                    if (validation && !editingMode) {
                        String noteText = null;
                        TempFollowUp tempFollowUp = new TempFollowUp();
                        LSContact tempContact = new LSContact();
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone));
                        tempContact.setContactType(selectedContactType);
                        tempContact.setContactSalesStatus(LSContact.SALES_STATUS_LEAD);
                        tempContact.save();
//  First checking if note is enabled so note can be gotten and passed in followup
                        if (etNoteText != null) {
                            noteText = etNoteText.getText().toString();
                            tempFollowUp.setNote(noteText);
                        } else {
                            noteText = "Empty";
                        }
                        if (year != 0 && month != 0 && day != 0 && hour != 0 && minute != 0) {
                            Calendar dateTimeForFollowup = Calendar.getInstance();
                            dateTimeForFollowup.set(Calendar.YEAR, year);
                            dateTimeForFollowup.set(Calendar.MONTH, month);
                            dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, day);
                            dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, hour);
                            dateTimeForFollowup.set(Calendar.MINUTE, minute);

//                TempFollowUp tempFollowUp = new TempFollowUp(note, dateAndTimeForAlarm.getTimeInMillis(), selectedLSContact);
                            tempFollowUp.setContact(tempContact);
                            tempFollowUp.setNote(noteText);
                            tempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                            tempFollowUp.save();
                            setAlarm(getApplicationContext(), tempFollowUp);
                        }
                        finish();
                    }
                } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                    }
                    if (contactPhone.equals("") || contactPhone.length() < 3) {
                        validation = false;
                        etContactPhone.setError("Invalid Number!");
                    }
                    if (validation && editingMode) {
                        String noteText = null;
                        TempFollowUp tempFollowUp = new TempFollowUp();
                        LSContact tempContact =selectedContact;
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone));
                        tempContact.setContactType(selectedContactType);
                        tempContact.setContactSalesStatus(LSContact.SALES_STATUS_LEAD);
                        tempContact.save();
//  First checking if note is enabled so note can be gotten and passed in followup
                        if (etNoteText != null) {
                            noteText = etNoteText.getText().toString();
                            tempFollowUp.setNote(noteText);
                        } else {
                            noteText = "Empty";
                        }
                        if (year != 0 && month != 0 && day != 0 && hour != 0 && minute != 0) {
                            Calendar dateTimeForFollowup = Calendar.getInstance();
                            dateTimeForFollowup.set(Calendar.YEAR, year);
                            dateTimeForFollowup.set(Calendar.MONTH, month);
                            dateTimeForFollowup.set(Calendar.DAY_OF_MONTH, day);
                            dateTimeForFollowup.set(Calendar.HOUR_OF_DAY, hour);
                            dateTimeForFollowup.set(Calendar.MINUTE, minute);

//                TempFollowUp tempFollowUp = new TempFollowUp(note, dateAndTimeForAlarm.getTimeInMillis(), selectedLSContact);
                            tempFollowUp.setContact(tempContact);
                            tempFollowUp.setNote(noteText);
                            tempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                            tempFollowUp.save();
                            setAlarm(getApplicationContext(), tempFollowUp);
                        }
                        finish();
                    }
                }
            }
        });
        if (preSelectedContactType != null) {
            if (preSelectedContactType.equals(LSContact.CONTACT_TYPE_SALES)) {
                selectRadioButton(LSContact.CONTACT_TYPE_SALES);
            } else if (preSelectedContactType.equals(LSContact.CONTACT_TYPE_COLLEAGUE)) {
                selectRadioButton(LSContact.CONTACT_TYPE_COLLEAGUE);
            }
        } else {
            selectRadioButton(LSContact.CONTACT_TYPE_SALES);
        }
        ibAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scaleView(addNoteActionLayout, 1f, 0f);
                addNoteActionLayout.setVisibility(View.GONE);
                LinearLayout addNoteLayout = (LinearLayout) inflater.inflate(R.layout.note_body_layout_followup_screen, noteContainerLayout, false);
                scaleView(addNoteLayout, 0f, 1f);
                noteContainerLayout.addView(addNoteLayout);
                etNoteText = (EditText) findViewById(R.id.etNoteTextFollowupPopup);
            }
        });
        ibAddFollowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFollowupActionLayout.setVisibility(View.GONE);
                LinearLayout addFollowupLayout = (LinearLayout) inflater.inflate(R.layout.followup_body_followup_screen, followupContainerLayout, false);
                followupContainerLayout.addView(addFollowupLayout);
                bOneWeek = (Button) findViewById(R.id.bOneWeekFollowupPopup);
                bThreeDays = (Button) findViewById(R.id.bThreeDaysFollowupPopup);
                bTomorrow = (Button) findViewById(R.id.bTomorrowFollowupPopup);
                bDate = (Button) findViewById(R.id.bDateFollowupPopup);
                bTime = (Button) findViewById(R.id.bTimeFollowupPopup);
                Calendar now = Calendar.getInstance();

                if (bTomorrow != null) {
                    bTomorrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.DAY_OF_MONTH, 1);
                            if (bDate != null) {
                                String date = now.get(Calendar.DAY_OF_MONTH) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.YEAR);
                                bDate.setText(date);
                                day = now.get(Calendar.DAY_OF_MONTH);
                                month = (now.get(Calendar.MONTH));
                                year = now.get(Calendar.YEAR);
                            }
                        }
                    });
                }
                if (bThreeDays != null) {
                    bThreeDays.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.DAY_OF_MONTH, 3);
                            if (bDate != null) {
                                String date = now.get(Calendar.DAY_OF_MONTH) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.YEAR);
                                bDate.setText(date);
                                day = now.get(Calendar.DAY_OF_MONTH);
                                month = (now.get(Calendar.MONTH));
                                year = now.get(Calendar.YEAR);
                            }
                        }
                    });
                }
                if (bOneWeek != null) {
                    bOneWeek.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.DAY_OF_MONTH, 7);
                            if (bDate != null) {
                                String date = now.get(Calendar.DAY_OF_MONTH) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.YEAR);
                                bDate.setText(date);
                                day = now.get(Calendar.DAY_OF_MONTH);
                                month = (now.get(Calendar.MONTH));
                                year = now.get(Calendar.YEAR);
                            }
                        }
                    });
                }
                if (bDate != null) {
                    bDate.setText(now.get(Calendar.DAY_OF_MONTH) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.YEAR));
                    day = now.get(Calendar.DAY_OF_MONTH);
                    month = (now.get(Calendar.MONTH));
                    year = now.get(Calendar.YEAR);
                }
                if (bTime != null) {
                    bTime.setText(now.get(Calendar.HOUR_OF_DAY) + " : " + now.get(Calendar.MINUTE));
                    hour = now.get(Calendar.HOUR_OF_DAY);
                    minute = now.get(Calendar.MINUTE);
                }
                bDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(TagNumberAndAddFollowupActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.setCancelable(false);
                        datePickerDialog.setTitle("Select Date for Followup");
                        datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
                    }
                });
                bTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar now = Calendar.getInstance();
                        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(TagNumberAndAddFollowupActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                        timePickerDialog.setCancelable(false);
                        timePickerDialog.setTitle("Select Time for Followup");
                        timePickerDialog.setMinTime(now.HOUR_OF_DAY, now.MINUTE, now.SECOND);
                        timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
                    }
                });
            }
        });
        bSalesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRadioButton(LSContact.CONTACT_TYPE_SALES);
            }
        });
        bColleagueRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRadioButton(LSContact.CONTACT_TYPE_COLLEAGUE);
            }
        });
    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }

    private void selectRadioButton(String button) {
        if (button.equals(LSContact.CONTACT_TYPE_SALES)) {
            selectedContactType = LSContact.CONTACT_TYPE_SALES;
            bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            bColleagueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
            bSalesRadio.setTextColor(Color.WHITE);
            bColleagueRadio.setTextColor(Color.BLACK);
        } else if (button.equals(LSContact.CONTACT_TYPE_COLLEAGUE)) {
            selectedContactType = LSContact.CONTACT_TYPE_COLLEAGUE;
            bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
            bColleagueRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            bSalesRadio.setTextColor(Color.BLACK);
            bColleagueRadio.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String email = "";
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            String name = retrieveContactName();
            String number = retrieveContactNumber();
            try {
//                email = retrieveContactEmail();
//                email = getContactDetailsEmail();
            } catch (Exception e) {
                e.printStackTrace();
                email = "";
            }
//            retrieveContactPhoto();
            etContactName.setText(name);
            etContactPhone.setText(number);
//            etContactEmail.setText(email);
            if (PhoneNumberAndCallUtils.isNumeric(name)) {
                etContactPhone.setText(number);
                etContactName.setText("");
            }
        }
    }

    private String retrieveContactName() {
        String contactName = null;
        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            etContactEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        cursor.close();
        // Log.d(TAG, "Contact Name: " + etContactName);
        return contactName;
    }

    private String retrieveContactNumber() {
        String contactNumber = null;
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();
        //Log.d(TAG, "Contact ID: " + contactID);
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        //Log.d(TAG, "Contact Phone Number: " + contactNumber);
        return contactNumber;
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
//        manager.setExact(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }
}