package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.DataSenderNew;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

/**
 * Created by ibtisam on 1/19/2017.
 */

public class AddLeadActivity extends Activity {
    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";
    public static final String LAUNCH_MODE_IMPORT_CONTACT = "launch_mode_import_contact_from_phonebook";
    public static final String LAUNCH_MODE_ADD_NEW_CONTACT = "launch_mode_add_new_contact";
    public static final String LAUNCH_MODE_EDIT_EXISTING_CONTACT = "launch_mode_edit_existing_contact";
    public static final String LAUNCH_MODE_TAG_PHONE_NUMBER = "launch_mode_tag_phone_number";
    public static final String LAUNCH_MODE_CONVERT_NON_BUSINESS = "launch_mode_convert_non_business";
    public static final String TAG_LAUNCH_MODE_PHONE_NUMBER = "phone_number";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    private static final String TAG = "AddLeadActivity";
    private static final String TITLE_IMPORT_CONTACT = "Import Contact";
    private static final String TITLE_ADD_NEW_CONTACT = "Add Contact";
    private static final String TITLE_EDIT_CONTACT = "Edit Contact";
    private static final String TITLE_TAG_NUMBER = "Tag Number";

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    String launchMode = LAUNCH_MODE_ADD_NEW_CONTACT;

    String phoneNumberFromLastActivity;
    boolean editingMode = false;
    long contactIdLong = -1;
    TextView tvTitleAddContact;
    EditText etContactName;
    EditText etContactPhone;
    EditText etContactEmail;
    LinearLayout llEmailAddress;
    Button bCancel;
    Button bSave;
    private Uri uriContact;
    private String contactID;
    private LSContact selectedContact = null;
//    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lead);
        tvTitleAddContact = (TextView) findViewById(R.id.tvTitleAddContact);
        etContactName = (EditText) findViewById(R.id.etNameAddLead);
        etContactPhone = (EditText) findViewById(R.id.etNumberAddLead);
        llEmailAddress = (LinearLayout) findViewById(R.id.llEmailAddress);
        etContactEmail = (EditText) findViewById(R.id.etEmailAddLead);
        bSave = (Button) findViewById(R.id.bSaveAddLead);
        bCancel = (Button) findViewById(R.id.bCancelAddLead);
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
            tvTitleAddContact.setText(TITLE_IMPORT_CONTACT);
            editingMode = false;
        } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
            llEmailAddress.setVisibility(View.GONE);
            tvTitleAddContact.setText(TITLE_ADD_NEW_CONTACT);
            editingMode = false;
        } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
            tvTitleAddContact.setText(TITLE_EDIT_CONTACT);
            editingMode = true;
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
            }
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            if (selectedContact.getContactName() != null && !selectedContact.getContactName().equals("")) {
                etContactName.setText(selectedContact.getContactName());
            } else {
                etContactName.setText("UNKNOWN");
            }
            if (selectedContact.getContactEmail() != null && !selectedContact.getContactEmail().equals("")) {
                etContactEmail.setText(selectedContact.getContactEmail());
            } else {
                etContactEmail.setText("");
            }
            etContactPhone.setText(selectedContact.getPhoneOne());
        } else if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
            llEmailAddress.setVisibility(View.GONE);
            tvTitleAddContact.setText(TITLE_TAG_NUMBER);
            phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
            editingMode = false;
            String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(phoneNumberFromLastActivity);
            String nameFromPhoneBook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), internationalNumber);
            etContactPhone.setText(internationalNumber);
            if (nameFromPhoneBook != null) {
                etContactName.setText(nameFromPhoneBook);
            }
        } else if (launchMode.equals(LAUNCH_MODE_CONVERT_NON_BUSINESS)) {
            llEmailAddress.setVisibility(View.GONE);
            tvTitleAddContact.setText(TITLE_EDIT_CONTACT);
            editingMode = false;
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
            }
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            if (selectedContact.getContactName() != null && !selectedContact.getContactName().equals("")) {
                etContactName.setText(selectedContact.getContactName());
            } else {
                etContactName.setText("UNKNOWN");
            }
            etContactPhone.setText(selectedContact.getPhoneOne());
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
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
                        LSContact checkContact;
                        checkContact = LSContact.getContactFromNumber(intlNum);

                        if (checkContact != null) {
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNTAGGED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_LEAD);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                finish();
                                Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                                long contactId = checkContact.getId();
                                detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                                startActivity(detailsActivityIntent);
                                Toast.makeText(AddLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                            long contactId = tempContact.getId();
                            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            startActivity(detailsActivityIntent);
                            Toast.makeText(AddLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
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
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
                        LSContact checkContact;
                        checkContact = LSContact.getContactFromNumber(intlNum);
                        if (checkContact != null) {
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNTAGGED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_LEAD);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                finish();
                                Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                                long contactId = checkContact.getId();
                                detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                                startActivity(detailsActivityIntent);
                                Toast.makeText(AddLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            finish();
                            Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                            long contactId = tempContact.getId();
                            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            startActivity(detailsActivityIntent);
                            Toast.makeText(AddLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    etContactEmail.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    String contactEmail = etContactEmail.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                    }
                    if (contactPhone.equals("") || contactPhone.length() < 3) {
                        validation = false;
                        etContactPhone.setError("Invalid Number!");
                    }
//                    if (contactEmail.equals("") || contactEmail.length() < 3) {
//                        validation = false;
//                        etContactEmail.setError("Invalid Email!");
//                    }
                    if (validation && editingMode) {
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
                        LSContact tempContact = selectedContact;
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(intlNum);
                        tempContact.setContactEmail(contactEmail);
                        tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                        tempContact.save();
                        finish();
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
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
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
                        LSContact checkContact;
                        checkContact = LSContact.getContactFromNumber(intlNum);
                        if (checkContact != null) {
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNTAGGED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_LEAD);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                finish();
                                Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                                long contactId = checkContact.getId();
                                detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                                startActivity(detailsActivityIntent);
                                Toast.makeText(AddLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            finish();
                            Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                            long contactId = tempContact.getId();
                            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            startActivity(detailsActivityIntent);
                            Toast.makeText(AddLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (launchMode.equals(LAUNCH_MODE_CONVERT_NON_BUSINESS)) {
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
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_LEAD);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                finish();
                                Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                                long contactId = checkContact.getId();
                                detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                                startActivity(detailsActivityIntent);
                                Toast.makeText(AddLeadActivity.this, "Saved AS LEAD", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(LSContact.CONTACT_TYPE_SALES);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            finish();
                            Intent detailsActivityIntent = new Intent(AddLeadActivity.this, ContactDetailsTabActivity.class);
                            long contactId = tempContact.getId();
                            detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                            startActivity(detailsActivityIntent);
                            Toast.makeText(AddLeadActivity.this, "Saved AS LEAD", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                DataSenderNew dataSenderNew = new DataSenderNew(getApplicationContext());
                dataSenderNew.execute();
            }
        });
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
}