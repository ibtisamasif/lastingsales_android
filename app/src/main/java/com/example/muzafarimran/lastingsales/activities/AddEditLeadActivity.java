package com.example.muzafarimran.lastingsales.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utils.TypeManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import java.util.Calendar;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/19/2017.
 */

public class AddEditLeadActivity extends AppCompatActivity {

    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";
    public static final String LAUNCH_MODE_IMPORT_CONTACT = "launch_mode_import_contact_from_phonebook";
    public static final String LAUNCH_MODE_ADD_NEW_CONTACT = "launch_mode_add_new_contact";
    public static final String LAUNCH_MODE_EDIT_EXISTING_CONTACT = "launch_mode_edit_existing_contact";
    //    public static final String LAUNCH_MODE_TAG_PHONE_NUMBER = "launch_mode_tag_phone_number";
//    public static final String LAUNCH_MODE_CONVERT_IGNORED = "launch_mode_convert_non_business";
    public static final String TAG_LAUNCH_MODE_PHONE_NUMBER = "phone_number";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    private static final String TAG = "AddEditLeadActivity";
    private static final String TITLE_IMPORT_CONTACT = "Import Contact";
    private static final String TITLE_ADD_NEW_CONTACT = "Add Contact";
    private static final String TITLE_EDIT_CONTACT = "Edit Contact";
    private static final String TITLE_TAG_NUMBER = "Tag Number";

    public static final String MIXPANEL_SOURCE = "mixpanel_source";

    public static final String MIXPANEL_SOURCE_CARD = "Card";
    public static final String MIXPANEL_SOURCE_NOTIFICATION = "Notification";
    public static final String MIXPANEL_SOURCE_UNLABELED = "Unlabeled";
    public static final String MIXPANEL_SOURCE_IGNORE = "Ignored";
    public static final String MIXPANEL_SOURCE_COLLEAGUE = "Colleague";
    public static final String MIXPANEL_SOURCE_INQUIRY = "Inquiry";


    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    String launchMode = LAUNCH_MODE_ADD_NEW_CONTACT;
    String selectedContactType = LSContact.CONTACT_TYPE_BUSINESS;
    String phoneNumberFromLastActivity;
    boolean editingMode = false;
    long contactIdLong = -1;
    //    TextView tvTitleAddContact;
    EditText etContactName;
    EditText etContactPhone;
    EditText etContactEmail;
    LinearLayout llEmailAddress;
    LinearLayout llContactType;
    Button bCancel;
    Button bSave;
    private Button bColleagueRadio;
    private Button bSalesRadio;
    private Uri uriContact;
    //    private String contactID;
    private LSContact selectedContact = null;
    private String contactPhone;
    private String contactName;
    private String contactEmail;
    private String mixpanelSource = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_lead);
        this.setFinishOnTouchOutside(false);
//        tvTitleAddContact = (TextView) findViewById(R.id.tvTitleAddContact);
        etContactName = (EditText) findViewById(R.id.etNameAddLead);
        etContactPhone = (EditText) findViewById(R.id.etNumberAddLead);
        llEmailAddress = (LinearLayout) findViewById(R.id.llEmailAddress);
        etContactEmail = (EditText) findViewById(R.id.etEmailAddLead);
        llContactType = (LinearLayout) findViewById(R.id.llContactType);
        bSave = (Button) findViewById(R.id.bSaveAddLead);
        bCancel = (Button) findViewById(R.id.bCancelAddLead);
        bSalesRadio = (Button) findViewById(R.id.bSalesRadio);
        bColleagueRadio = (Button) findViewById(R.id.bCollegueRadio);
        int notificationId = getIntent().getIntExtra("notificationId", 1);
        if (notificationId != 0) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(notificationId);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getString(ACTIVITY_LAUNCH_MODE);
        }
//        If launch mode is import contact then starting contact import activity so contact data can be brought in
        if (launchMode.equals(LAUNCH_MODE_IMPORT_CONTACT)) {
            llEmailAddress.setVisibility(View.GONE);
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
//            tvTitleAddContact.setText(TITLE_IMPORT_CONTACT);
            editingMode = false;
            // Redirected Import Contact to LAUNCH_MODE_ADD_NEW_CONTACT
            launchMode = AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT;
            mixpanelSource = bundle.getString(MIXPANEL_SOURCE);
        }
        if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
            populateCreateContactView();
            String num = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
            if (num != null) {
                etContactPhone.setText(num);
                String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(this, num);
                if (name != null) {
                    etContactName.setText(name);
                }
            }
            mixpanelSource = bundle.getString(MIXPANEL_SOURCE);
        } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
            populateUpdateContactView(bundle);
            mixpanelSource = bundle.getString(MIXPANEL_SOURCE);
        }

        try {
            if (mixpanelSource.equals(MIXPANEL_SOURCE_NOTIFICATION)) {
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                try {
                    JSONObject props = new JSONObject();
                    props.put("type", "track");
                    mixpanel.track("Lead From Notification - Clicked", props);
                } catch (Exception e) {
                    Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactName = etContactName.getText().toString();
                contactPhone = etContactPhone.getText().toString();
                contactEmail = etContactEmail.getText().toString();

                String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(AddEditLeadActivity.this, contactPhone);
                LSContact checkContact;
                checkContact = LSContact.getContactFromNumber(intlNum);
                if (checkContact == null) {
                    launchMode = LAUNCH_MODE_ADD_NEW_CONTACT;
                } else {
                    launchMode = LAUNCH_MODE_EDIT_EXISTING_CONTACT;
                    selectedContact = checkContact;
                }
                if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
                    if (isValid(contactName, contactPhone, contactEmail)) {
                        LSContact tempContact = new LSContact();
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(intlNum);
                        tempContact.setContactType(selectedContactType);
                        tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                        tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                        tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                        tempContact.save();
                        Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                        finish();
                        //Saving contact in native phonebook as well
                        PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), tempContact.getContactName(), tempContact.getPhoneOne());
                        moveToContactDetailScreenIfNeeded(tempContact);
                        String projectToken = MixpanelConfig.projectToken;
                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                        mixpanel.track("Create lead dialog - created lead");
                    }
                } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
                    if (isValid(contactName, contactPhone, contactEmail)) {

                        LSContact tempContact = selectedContact;
                        String oldType = selectedContact.getContactType();
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(intlNum);
                        tempContact.setContactType(selectedContactType);
                        tempContact.setContactEmail(contactEmail);
                        tempContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
                        if (tempContact.getContactSalesStatus() == null) {
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                        }
                        if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                        }
                        tempContact.save();
                        String newType = selectedContact.getContactType();
                        // The contact will never be saved again in the flow.
                        TypeManager.ConvertTo(getApplicationContext(), selectedContact, oldType, newType);
                        String checkContactInLocalPhonebook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), intlNum);
                        if (checkContactInLocalPhonebook == null) {
                            //Saving contact in native phonebook as well
                            PhoneNumberAndCallUtils.addContactInNativePhonebook(getApplicationContext(), tempContact.getContactName(), tempContact.getPhoneOne());
                        }
                        finish();
                        moveToContactDetailScreenIfNeeded(tempContact);
                    }
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
                if (mixpanelSource != null) {
                    String projectToken = MixpanelConfig.projectToken;
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                    mixpanel.track("Lead From " + mixpanelSource);
                    Log.d(TAG, "mixpanelSource: " + mixpanelSource);
                }
                LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
                TinyBus bus = TinyBus.from(getApplicationContext());
                bus.post(mCallEvent);
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                dataSenderAsync.run();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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


    private void populateCreateContactView() {
        llEmailAddress.setVisibility(View.GONE);
//        tvTitleAddContact.setText(TITLE_ADD_NEW_CONTACT);
        editingMode = false;
        selectRadioButton(LSContact.CONTACT_TYPE_BUSINESS);
    }

    private void populateUpdateContactView(Bundle bundle) {
//        tvTitleAddContact.setText(TITLE_EDIT_CONTACT);
        editingMode = true;
        String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
        String num = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
        if (id != null && !id.equals("")) {
            contactIdLong = Long.parseLong(id);
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
        } else if (num != null && !num.equals("")) {
            selectedContact = LSContact.getContactFromNumber(num);
        }
        if (selectedContact.getContactName() != null) {
            if (selectedContact.getContactName().equals("Unlabeled Contact") || selectedContact.getContactName().equals("Ignored Contact")) {
                String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), selectedContact.getPhoneOne());
                if (name != null) {
                    etContactName.setText(name);
                } else {
                    etContactName.setText("");
                }
            } else {
                etContactName.setText(selectedContact.getContactName());
            }
            selectRadioButton(selectedContact.getContactType());
        } else {
            etContactName.setText("");
        }
        if (selectedContact.getContactEmail() != null && !selectedContact.getContactEmail().equals("")) {
            etContactEmail.setText(selectedContact.getContactEmail());
        } else {
            etContactEmail.setText("");
        }
        etContactPhone.setText(selectedContact.getPhoneOne());
//            etContactPhone.setFocusable(false);
    }

    private boolean isValid(String contactName, String contactPhone, String contactEmail) {
        etContactName.setError(null);
        etContactPhone.setError(null);
        etContactEmail.setError(null);
        boolean validation = true;
        if (contactName.equals("") || contactName.length() < 3) {
            validation = false;
            etContactName.setError("Invalid Name!");
            return false;
        }
        if (contactPhone.equals("") || contactPhone.length() < 3) {
            validation = false;
            etContactPhone.setError("Invalid Number!");
            return false;
        }
//                    if (contactEmail.equals("") || contactEmail.length() < 3) {
//                        validation = false;
//                        etContactEmail.setError("Invalid Email!");
//                    }
        return true;
    }

    private void moveToContactDetailScreenIfNeeded(LSContact contact) {
        if (contact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
            Intent detailsActivityIntent = new Intent(AddEditLeadActivity.this, ContactDetailsTabActivity.class);
            long contactId = contact.getId();
            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
            startActivity(detailsActivityIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        String email = "";
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            String name = retrieveContactName();
            String number = retrieveContactNumber();
            try {
//                email = retrieveContactEmail();
//                email = getContactDetailsEmail();
            } catch (Exception e) {
                e.printStackTrace();
//                email = "";
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
        String contactID = null;
        // getting contacts ID
        Cursor cursorID = getContentResolver().query(uriContact, new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }
        cursorID.close();
        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                new String[]{contactID},
                null);
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        if (contactNumber == null || contactNumber.equals("")) {
            cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
                    ,
                    new String[]{contactID},
                    null);
        }
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        if (contactNumber == null || contactNumber.equals("")) {
            cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_WORK
                    ,
                    new String[]{contactID},
                    null);
        }
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        if (contactNumber == null || contactNumber.equals("")) {
            cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_HOME
                    ,
                    new String[]{contactID},
                    null);
        }
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        if (contactNumber == null || contactNumber.equals("")) {
            cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_OTHER
                    ,
                    new String[]{contactID},
                    null);
        }
        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        return contactNumber;
    }

    private void selectRadioButton(String button) {
        if (button.equals(LSContact.CONTACT_TYPE_SALES)) {
            selectedContactType = LSContact.CONTACT_TYPE_SALES;
            bSalesRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            bColleagueRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//            bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
//            bColleagueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
            bSalesRadio.setTextColor(Color.WHITE);
            bColleagueRadio.setTextColor(Color.BLACK);
        } else if (button.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
            selectedContactType = LSContact.CONTACT_TYPE_BUSINESS;
            bSalesRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            bColleagueRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//            bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//            bColleagueRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            bSalesRadio.setTextColor(Color.BLACK);
            bColleagueRadio.setTextColor(Color.WHITE);
        }
    }
}