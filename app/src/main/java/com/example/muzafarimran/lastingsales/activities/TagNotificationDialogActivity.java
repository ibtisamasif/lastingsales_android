package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.utils.IgnoredContact;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.Calendar;

public class TagNotificationDialogActivity extends Activity {
    public static final String TAG = "TagNotificationDialog";
    //    Constants
    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";

    public static final String LAUNCH_MODE_ADD_NEW_CONTACT = "launch_mode_add_new_contact";
    public static final String LAUNCH_MODE_EDIT_EXISTING_CONTACT = "launch_mode_edit_existing_contact";
    public static final String LAUNCH_MODE_TAG_PHONE_NUMBER = "launch_mode_tag_phone_number";

    public static final String TAG_LAUNCH_MODE_PHONE_NUMBER = "phone_number";
    public static final String TAG_LAUNCH_MODE_CONTACT_NAME = "contact_name";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    public static final String TAG_LAUNCH_MODE_CONTACT_TYPE = "contact_type";

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    String launchMode = LAUNCH_MODE_ADD_NEW_CONTACT;
    String selectedContactType = LSContact.CONTACT_TYPE_BUSINESS;
    boolean editingMode = false;
    String phoneNumberFromLastActivity;
    String contactNameFromLastActivity;
    String preSelectedContactType = LSContact.CONTACT_TYPE_BUSINESS;
    private Uri uriContact;
    private String contactID;
    private EditText etContactName;
    private TextView etContactPhone;
    private Button bSave;
    private ImageButton bClose;
    private CheckBox cbIgnore;
    private LinearLayout llContactDetailsFollowupScreen;
    private LinearLayout llContactType;
    private LayoutInflater inflater;
    private Button bColleagueRadio;
    private Button bSalesRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        Log.d("testlog", "onCreate: DIALOG BOX SHOWN");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_notification_dialog);
        inflater = getLayoutInflater();
        etContactName = (EditText) findViewById(R.id.etContactName);
        etContactPhone = (TextView) findViewById(R.id.etContactPhone);
        bSave = (Button) findViewById(R.id.bSaveFollowupPopup);
        bClose = (ImageButton) findViewById(R.id.bClose);
        cbIgnore = (CheckBox) findViewById(R.id.cbIgnore);
        bSalesRadio = (Button) findViewById(R.id.bSalesRadio);
        bColleagueRadio = (Button) findViewById(R.id.bCollegueRadio);
        llContactDetailsFollowupScreen = (LinearLayout) findViewById(R.id.llContactDetailsAddContactScreen);
        llContactType = (LinearLayout) findViewById(R.id.llContactType);
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
//        if launch mode is tag number then number is gotten out of bundle so it can be searched in
//        phonebook and the number can be populated in the editText Field
        if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
            Log.d(TAG, "onCreate: Tag Number");
            phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
            contactNameFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_CONTACT_NAME);
            selectedContactType = bundle.getString(TAG_LAUNCH_MODE_CONTACT_TYPE);
            preSelectedContactType = selectedContactType;
            editingMode = false;
            String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(TagNotificationDialogActivity.this, phoneNumberFromLastActivity);
            String nameFromPhoneBook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), internationalNumber);
            etContactPhone.setText(internationalNumber);
            if (contactNameFromLastActivity != null) {
                etContactName.setText(contactNameFromLastActivity);
            } else if (nameFromPhoneBook != null) {
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
        etContactName.setSelection(etContactName.getText().length());

        cbIgnore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                if (isChecked) {
                    bSalesRadio.setEnabled(false);
//                    bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
                    bSalesRadio.setTextColor(Color.GRAY);
                    bColleagueRadio.setEnabled(false);
//                    bColleagueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
                    bColleagueRadio.setTextColor(Color.GRAY);
                } else {
                    bSalesRadio.setEnabled(true);
                    bColleagueRadio.setEnabled(true);
                    selectRadioButton(selectedContactType);
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
                if (cbIgnore.isChecked()) {
                    Log.d(TAG, "onClick: Checked");
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    IgnoredContact.AddAsIgnoredContact(getApplicationContext(), contactPhone, contactName); //TODO centralize convertion in one class
                    String projectToken = MixpanelConfig.projectToken;
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                    try {
                        JSONObject props = new JSONObject();
                        props.put("type", "ignored");
                        mixpanel.track("Lead From Dialog - Clicked", props);
                    } catch (Exception e) {
                        Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                    }
                    finish();
                } else {
                    Log.d(TAG, "onClick: Not Checked");
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
                            String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(TagNotificationDialogActivity.this, contactPhone);
                            LSContact checkContact;
                            checkContact = LSContact.getContactFromNumber(intlNum);
                            if (checkContact != null) {
                                if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                    checkContact.setContactName(contactName);
                                    checkContact.setPhoneOne(intlNum);
                                    checkContact.setContactType(selectedContactType);
                                    checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                    checkContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                                    if (checkContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || checkContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                        checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                    }
                                    checkContact.save();
                                    String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                                    if (checkContactInLocalPhonebook == null) {
                                        //Saving contact in native phonebook as well
                                        PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), checkContact.getContactName(), checkContact.getPhoneOne());
                                    }
                                    String projectToken = MixpanelConfig.projectToken;
                                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                                    try {
                                        JSONObject props = new JSONObject();
                                        props.put("type", "track");
                                        mixpanel.track("Lead From Dialog - Clicked", props);
                                        mixpanel.track("Lead From Dialog");
                                    } catch (Exception e) {
                                        Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                                    }
                                    finish();
                                }
                                Toast.makeText(TagNotificationDialogActivity.this, "Already Exists Converted Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                LSContact tempContact = new LSContact();
                                tempContact.setContactName(contactName);
                                tempContact.setPhoneOne(intlNum);
                                tempContact.setContactType(selectedContactType);
                                tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                                if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                                    tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                                }
                                tempContact.save();
                                String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                                if (checkContactInLocalPhonebook == null) {
                                    //Saving contact in native phonebook as well
                                    PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), checkContact.getContactName(), checkContact.getPhoneOne());
                                }
                                String projectToken = MixpanelConfig.projectToken;
                                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                                try {
                                    JSONObject props = new JSONObject();
                                    props.put("type", "track");
                                    mixpanel.track("Lead From Dialog - Clicked", props);
                                    mixpanel.track("Lead From Dialog");
                                } catch (Exception e) {
                                    Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                                }
                                finish();
                            }
                        }
                    }
                }
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
                selectRadioButton(LSContact.CONTACT_TYPE_BUSINESS);
            }
        });
    }

    private void selectRadioButton(String button) {
        if (button.equals(LSContact.CONTACT_TYPE_SALES)) {
            selectedContactType = LSContact.CONTACT_TYPE_SALES;
//            bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
//            bColleagueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
            bSalesRadio.setTextColor(Color.WHITE);
            bColleagueRadio.setTextColor(Color.BLACK);
        } else if (button.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            selectedContactType = LSContact.CONTACT_TYPE_BUSINESS;
//            bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//            bColleagueRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            bSalesRadio.setTextColor(Color.BLACK);
            bColleagueRadio.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            String name = retrieveContactName();
            String number = retrieveContactNumber();
            etContactName.setText(name);
            etContactPhone.setText(number);
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

//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause: TagNotificationDialogActivity");
//        Log.d("testlog", "onPause: TagNotificationDialogActivity");
//        if(hasWindowFocus()){
//            finish();
//            Log.d("testlog", "onPause: Finish() TagNotificationDialogActivity");
//        }
//    }
}