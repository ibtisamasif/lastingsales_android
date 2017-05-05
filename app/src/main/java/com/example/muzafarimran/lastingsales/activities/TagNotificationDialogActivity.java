package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.ContactTaggedFromUntaggedContactEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.receivers.AlarmReceiver;
import com.example.muzafarimran.lastingsales.receivers.IgnoredContact;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.MixpanelConfig;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;

import de.halfbit.tinybus.TinyBus;

public class TagNotificationDialogActivity extends Activity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final String TAG = "TagNotificationDialog";
    //    Constants
    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";

    public static final String LAUNCH_MODE_IMPORT_CONTACT = "launch_mode_import_contact_from_phonebook";
    public static final String LAUNCH_MODE_ADD_NEW_CONTACT = "launch_mode_add_new_contact";
    public static final String LAUNCH_MODE_EDIT_EXISTING_CONTACT = "launch_mode_edit_existing_contact";
    public static final String LAUNCH_MODE_TAG_PHONE_NUMBER = "launch_mode_tag_phone_number";
    public static final String LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP = "launch_mode_edit_existing_followup";
    public static final String LAUNCH_MODE_ADD_NEW_FOLLOWUP = "launch_mode_add_new_followup";
    public static final String LAUNCH_MODE_TAG_UNTAGGED_CONTACT = "launch_mode_tag_untagged_contact";

    public static final String TAG_LAUNCH_MODE_PHONE_NUMBER = "phone_number";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    public static final String TAG_LAUNCH_MODE_CONTACT_TYPE = "contact_type";
    public static final String TAG_LAUNCH_MODE_FOLLOWUP_ID = "followup_id";

    private static final String TITLE_IMPORT_CONTACT = "Import Contact";
    private static final String TITLE_ADD_NEW_CONTACT = "Add Contact";
    private static final String TITLE_EDIT_CONTACT = "Edit Contact";
    private static final String TITLE_ADD_FOLLOWUP = "Add Followup";
    private static final String TITLE_EDIT_FOLLOWUP = "Edit Followup";
    private static final String TITLE_TAG_NUMBER = "Tag Number";
    private static final String TITLE_TAG_UNTAGGED_CONTACT = "Tag Untagged";

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    String launchMode = LAUNCH_MODE_ADD_NEW_CONTACT;
    String selectedContactType = LSContact.CONTACT_TYPE_SALES;
    boolean editingMode = false;
    long contactIdLong = -1;
    long followupIdLong = -1;
    String phoneNumberFromLastActivity;
    String preSelectedContactType;
    int year, month, day, hour, minute;
    private Uri uriContact;
    private String contactID;
    private EditText etContactName;
    private TextView etContactPhone;
    private Button bSave;
    private ImageButton bClose;
    //    private ImageView ibAddNote;
//    private ImageView ibAddFollowup;
//    private RelativeLayout addNoteActionLayout;
//    private RelativeLayout addFollowupActionLayout;
//    private LinearLayout noteContainerLayout;
//    private LinearLayout followupContainerLayout;
    private LinearLayout llContactDetailsFollowupScreen;
    private LinearLayout llContactType;
    private LayoutInflater inflater;
    private EditText etNoteText;
    private EditText etFollowupTitleText;
    private Button bOneWeek;
    private Button bThreeDays;
    private Button bTomorrow;
    private Button bDate;
    private Button bTime;
    private Button bColleagueRadio;
    private Button bSalesRadio;
    private LSContact selectedContact = null;
    private TempFollowUp selectedFollowup = null;
//    Toolbar toolbar;
//    ActionBar actionBar;
//    private TextView tvTitleFollowupPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_number_and_add_followup);
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle("Add Contact");
//        setSupportActionBar(toolbar);
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        inflater = getLayoutInflater();
        year = month = day = hour = minute = 0;
//        tvTitleFollowupPopup = (TextView) findViewById(R.id.tvTitleFollowupPopup);
        etContactName = (EditText) findViewById(R.id.etNameFollowupPopup);
        etContactPhone = (TextView) findViewById(R.id.etNumberFollowupPopup);
//        ibAddNote = (ImageView) findViewById(R.id.ivAddNote);
//        ibAddFollowup = (ImageView) findViewById(R.id.ivAddFollowup);
//        addNoteActionLayout = (RelativeLayout) findViewById(R.id.addNoteActionLayout);
//        addFollowupActionLayout = (RelativeLayout) findViewById(R.id.addFollowupActionLayout);
//        noteContainerLayout = (LinearLayout) findViewById(R.id.noteContainerLayout);
//        followupContainerLayout = (LinearLayout) findViewById(R.id.followupContainer);
        Button bIgnore = (Button) findViewById(R.id.bCancelFollowupPopup);
        bSave = (Button) findViewById(R.id.bSaveFollowupPopup);
        bClose = (ImageButton) findViewById(R.id.bClose);
        bSalesRadio = (Button) findViewById(R.id.bSalesRadio);
        bColleagueRadio = (Button) findViewById(R.id.bCollegueRadio);
        llContactDetailsFollowupScreen = (LinearLayout) findViewById(R.id.llContactDetailsAddContactScreen);
        llContactType = (LinearLayout) findViewById(R.id.llContactType);
//        bClose.setBackgroundResource(R.drawable.ic_close_white);
        // Cancel the Notifitcation appeared on call ending
        int notificationId = getIntent().getIntExtra("notificationId", 1);
        if (notificationId != 0) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
        try {
            mixpanel.track("Lead From Dialog - Shown");
        } catch (Exception e) {
            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getString(ACTIVITY_LAUNCH_MODE);
        }
//        if launch mode is edit existing contact then geting id of contact so its data can be populated
        if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
            Log.d(TAG, "onCreate: Existing");
//            tvTitleFollowupPopup.setText(TITLE_EDIT_CONTACT);
            editingMode = true;
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
            }
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            String tempContactType = bundle.getString(TAG_LAUNCH_MODE_CONTACT_TYPE);
            if (tempContactType == null || tempContactType.equals("")) {
                selectedContactType = selectedContact.getContactType();
            } else {
                selectedContactType = bundle.getString(TAG_LAUNCH_MODE_CONTACT_TYPE);
            }
            preSelectedContactType = selectedContactType;
            if (selectedContact.getContactName() != null && !selectedContact.getContactName().equals("")) {
                etContactName.setText(selectedContact.getContactName());
            } else {
                etContactName.setText("UNKNOWN");
            }
            etContactPhone.setText(selectedContact.getPhoneOne());
        }
//        if launch mode is tag number then number is gotten out of bundle so it can be searched in
//        phonebook and the number can be populated in the editText Field
        else if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
            Log.d(TAG, "onCreate: Tag Number");
//            tvTitleFollowupPopup.setText(TITLE_TAG_NUMBER);
            phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
            selectedContactType = bundle.getString(TAG_LAUNCH_MODE_CONTACT_TYPE);
            preSelectedContactType = selectedContactType;
            editingMode = false;
            phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
            String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(phoneNumberFromLastActivity);
            String nameFromPhoneBook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), internationalNumber);
            etContactPhone.setText(internationalNumber);
            if (nameFromPhoneBook != null) {
                etContactName.setText(nameFromPhoneBook);
            }
//            populating name and phone number below after findVieByIDs have been called
        }
//      updating selected Radio button on UI
        selectRadioButton(selectedContactType);
        if (preSelectedContactType != null) {
            selectRadioButton(preSelectedContactType);
        } else {
            selectRadioButton(LSContact.CONTACT_TYPE_SALES);
        }
        bIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    IgnoredContact.AddAsIgnoredContact(contactPhone, contactName);
                    String projectToken = MixpanelConfig.projectToken;
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                    try {
                        JSONObject props = new JSONObject();
                        props.put("type", "ignored");
                        mixpanel.track("Lead From Dialog - Clicked",props);
                    } catch (Exception e) {
                        Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                    }
                    finish();
                }
            }
        });
        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
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
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
                        LSContact checkContact;
                        checkContact = LSContact.getContactFromNumber(intlNum);
                        if (checkContact != null) {
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                String titleText = null;
                                String noteText = null;
                                TempFollowUp TempFollowUp = new TempFollowUp();
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(selectedContactType);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                if (checkContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || checkContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                    checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                }
                                checkContact.save();
                                String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                                if (checkContactInLocalPhonebook == null) {
                                    //Saving contact in native phonebook as well
                                    PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), checkContact.getContactName(), checkContact.getPhoneOne());
                                }
                                if (etNoteText != null) {
                                    noteText = etNoteText.getText().toString();
                                    if (!noteText.isEmpty() && !noteText.equals("")) {
                                        LSNote tempNote = new LSNote();
                                        tempNote.setNoteText(noteText);
                                        tempNote.setContactOfNote(checkContact);
                                        tempNote.save();
                                    }
                                }
                                if (etFollowupTitleText != null) {
                                    titleText = etFollowupTitleText.getText().toString();
                                    TempFollowUp.setTitle(titleText);
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
                                    TempFollowUp.setContact(checkContact);
                                    TempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                                    TempFollowUp.save();
                                    setAlarm(getApplicationContext(), TempFollowUp);
                                }
                                String projectToken = MixpanelConfig.projectToken;
                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                                try {
                                    JSONObject props = new JSONObject();
                                    props.put("type", "track");
                                    mixpanel.track("Lead From Dialog - Clicked",props);
                                    mixpanel.track("Lead From Dialog");
                                } catch (Exception e) {
                                    Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                                }
                                finish();
                            }
                            Toast.makeText(TagNotificationDialogActivity.this, "Already Exists Converted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String titleText = null;
                            String noteText = null;
                            TempFollowUp TempFollowUp = new TempFollowUp();
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(selectedContactType);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                            if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                            }
                            tempContact.save();
                            String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                            if (checkContactInLocalPhonebook == null) {
                                //Saving contact in native phonebook as well
                                PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), checkContact.getContactName(), checkContact.getPhoneOne());
                            }
                            if (etNoteText != null) {
                                noteText = etNoteText.getText().toString();
                                if (!noteText.isEmpty() && !noteText.equals("")) {
                                    LSNote tempNote = new LSNote();
                                    tempNote.setNoteText(noteText);
                                    tempNote.setContactOfNote(tempContact);
                                    tempNote.save();
                                }
                            }
                            if (etFollowupTitleText != null) {
                                titleText = etFollowupTitleText.getText().toString();
                                TempFollowUp.setTitle(titleText);
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
                                TempFollowUp.setContact(tempContact);
                                TempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                                TempFollowUp.save();
                                setAlarm(getApplicationContext(), TempFollowUp);
                            }
                            String projectToken = MixpanelConfig.projectToken;
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                            try {
                                JSONObject props = new JSONObject();
                                props.put("type", "track");
                                mixpanel.track("Lead From Dialog - Clicked",props);
                                mixpanel.track("Lead From Dialog");
                            } catch (Exception e) {
                                Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                            }
                            finish();
                        }
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
                        //modified by ibtisam
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
                        String titleText = null;
                        String noteText = null;
                        TempFollowUp TempFollowUp = new TempFollowUp();
                        LSContact tempContact = selectedContact;
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(intlNum);
                        tempContact.save();
                        String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                        if (checkContactInLocalPhonebook == null) {
                            //Saving contact in native phonebook as well
                            PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), tempContact.getContactName(), tempContact.getPhoneOne());
                        }
                        if (etNoteText != null) {
                            noteText = etNoteText.getText().toString();
                            if (!noteText.isEmpty() && !noteText.equals("")) {
                                LSNote tempNote = new LSNote();
                                tempNote.setNoteText(noteText);
                                tempNote.setContactOfNote(tempContact);
                                tempNote.save();
                            }
                        }
                        if (etFollowupTitleText != null) {
                            titleText = etFollowupTitleText.getText().toString();
                            TempFollowUp.setTitle(titleText);
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
                            TempFollowUp.setContact(tempContact);
                            TempFollowUp.setDateTimeForFollowup(dateTimeForFollowup.getTimeInMillis());
                            TempFollowUp.save();
                            setAlarm(getApplicationContext(), TempFollowUp);
                        }
                        String projectToken = MixpanelConfig.projectToken;
                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                        try {
                            JSONObject props = new JSONObject();
                            props.put("type", "track");
                            mixpanel.track("Lead From Dialog - Clicked",props);
                            mixpanel.track("Lead From Dialog");
                        } catch (Exception e) {
                            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                        }
                        finish();
                    }
                }
            }
        });
//        ibAddNote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showAddNoteLayout();
//            }
//        });
//        ibAddFollowup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showAddFollowupLayout();
//            }
//        });
        bSalesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRadioButton(LSContact.CONTACT_TYPE_SALES);
            }
        });
        bColleagueRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRadioButton(LSContact.CONTACT_TYPE_BUSINESS);
            }
        });
    }

//    private void showAddNoteLayout() {
//        scaleView(addNoteActionLayout, 1f, 0f);
//        addNoteActionLayout.setVisibility(View.GONE);
//        LinearLayout addNoteLayout = (LinearLayout) inflater.inflate(R.layout.section_note_body_layout_followup_screen, noteContainerLayout, false);
//        scaleView(addNoteLayout, 0f, 1f);
//        noteContainerLayout.addView(addNoteLayout);
//        etNoteText = (EditText) findViewById(R.id.etNoteTextFollowupPopup);
//    }
//
//    private void showAddFollowupLayout() {
//        addFollowupActionLayout.setVisibility(View.GONE);
//        LinearLayout addFollowupLayout = (LinearLayout) inflater.inflate(R.layout.section_followup_body_followup_screen, followupContainerLayout, false);
//        followupContainerLayout.addView(addFollowupLayout);
//        bOneWeek = (Button) findViewById(R.id.bOneWeekFollowupPopup);
//        bThreeDays = (Button) findViewById(R.id.bThreeDaysFollowupPopup);
//        bTomorrow = (Button) findViewById(R.id.bTomorrowFollowupPopup);
//        bDate = (Button) findViewById(R.id.bDateFollowupPopup);
//        bTime = (Button) findViewById(R.id.bTimeFollowupPopup);
//        etFollowupTitleText = (EditText) findViewById(R.id.etTitleTextFollowupPopup);
//        Calendar now = Calendar.getInstance();
//
//        if (bTomorrow != null) {
//            bTomorrow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Calendar now = Calendar.getInstance();
//                    now.add(Calendar.DAY_OF_MONTH, 1);
//                    setDateTimeFromMiliseconds(now.getTimeInMillis());
//                    day = now.get(Calendar.DAY_OF_MONTH);
//                    month = (now.get(Calendar.MONTH));
//                    year = now.get(Calendar.YEAR);
//                }
//            });
//        }
//        if (bThreeDays != null) {
//            bThreeDays.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Calendar now = Calendar.getInstance();
//                    now.add(Calendar.DAY_OF_MONTH, 3);
//                    setDateTimeFromMiliseconds(now.getTimeInMillis());
//                    day = now.get(Calendar.DAY_OF_MONTH);
//                    month = (now.get(Calendar.MONTH));
//                    year = now.get(Calendar.YEAR);
//                }
//            });
//        }
//        if (bOneWeek != null) {
//            bOneWeek.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Calendar now = Calendar.getInstance();
//                    now.add(Calendar.DAY_OF_MONTH, 7);
//                    setDateTimeFromMiliseconds(now.getTimeInMillis());
//                    day = now.get(Calendar.DAY_OF_MONTH);
//                    month = (now.get(Calendar.MONTH));
//                    year = now.get(Calendar.YEAR);
//                }
//            });
//        }
//        day = now.get(Calendar.DAY_OF_MONTH);
//        month = (now.get(Calendar.MONTH));
//        year = now.get(Calendar.YEAR);
//        hour = now.get(Calendar.HOUR_OF_DAY);
//        minute = now.get(Calendar.MINUTE);
//        setDateTimeFromMiliseconds(now.getTimeInMillis());
//        bDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar now = Calendar.getInstance();
//                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(TagNotificationDialogActivity.this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
//                datePickerDialog.setCancelable(false);
//                datePickerDialog.setTitle("Select Date for Followup");
//                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");
//            }
//        });
//        bTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Calendar now = Calendar.getInstance();
//                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(TagNotificationDialogActivity.this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
//                timePickerDialog.setCancelable(false);
//                timePickerDialog.setTitle("Select Time for Followup");
//                timePickerDialog.setMinTime(now.HOUR_OF_DAY, now.MINUTE, now.SECOND);
//                timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
//            }
//        });
//    }

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
        } else if (button.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            selectedContactType = LSContact.CONTACT_TYPE_BUSINESS;
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

    public void setAlarm(Context context, TempFollowUp TempFollowUp) {
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
        dateAndTimeForAlarm.set(year, month, day, hour, minute, 0);
        Intent aint = new Intent(context, AlarmReceiver.class);
//        String note = etNote.getText().toString();
//        String note = "Hello";
        aint.putExtra("followupid", TempFollowUp.getId() + "");
//        aint.putExtra("message","This is message from followup");
        pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(TempFollowUp.getId().toString()), aint, PendingIntent.FLAG_UPDATE_CURRENT);
                         /* Retrieve a PendingIntent that will perform a broadcast */
//        Intent alarmIntent = new Intent(activity, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);
//        manager.setExact(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, dateAndTimeForAlarm.getTimeInMillis(), pendingIntent);
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.close_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_action_close:
                finish();
                break;
//            case android.R.id.home:
//                onBackPressed();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}