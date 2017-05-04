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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/19/2017.
 */

public class AddEditLeadActivity extends Activity {


    ///////////////////////////////////////////////////////////////////////////////////////////////////
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

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    String launchMode = LAUNCH_MODE_ADD_NEW_CONTACT;
    String selectedContactType = LSContact.CONTACT_TYPE_BUSINESS;
    String phoneNumberFromLastActivity;
    boolean editingMode = false;
    long contactIdLong = -1;
    TextView tvTitleAddContact;
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
    private String contactID;
    private LSContact selectedContact = null;
    private String contactPhone;
    private String contactName;
    private String contactEmail;
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
            tvTitleAddContact.setText(TITLE_IMPORT_CONTACT);
            editingMode = false;
            // Redirected Import Contact to LAUNCH_MODE_EDIT_EXISTING_CONTACT
            launchMode = AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT;
        }
        if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
            populateCreateContactView();
        } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
            populateUpdateContactView(bundle);
        }

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactName = etContactName.getText().toString();
                contactPhone = etContactPhone.getText().toString();
                contactEmail = etContactEmail.getText().toString();

                        if (checkContact != null) {
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(selectedContactType);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                    moveToContactDetailScreen(checkContact);
                                } else {
                                    //update inquiry as well if exists
                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                                    if (tempInquiry != null) {
                                        tempInquiry.setContact(checkContact);
                                        tempInquiry.save();
                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                        tempInquiry.save();
                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                        dataSenderAsync.execute();
                                    }
                                }
                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(selectedContactType);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            //update inquiry as well if exists
                            LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                            if (tempInquiry != null) {
                                tempInquiry.setContact(tempContact);
                                tempInquiry.save();
                            }
                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                moveToContactDetailScreen(tempContact);
                            } else {
                                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                tempInquiry.save();
                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                dataSenderAsync.execute();
                            }
                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
                    AddNewContactProcessor.Process(AddEditLeadActivity.this, etContactName, etContactPhone, editingMode, selectedContactType);           //////////////////////////////////////////////////////////
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
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(selectedContactType);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                    moveToContactDetailScreen(checkContact);
                                } else {
                                    //update inquiry as well if exists
                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                                    if (tempInquiry != null) {
                                        tempInquiry.setContact(checkContact);
                                        tempInquiry.save();
                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                        tempInquiry.save();
                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                        dataSenderAsync.execute();
                                    }
                                }
                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(selectedContactType);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            //update inquiry as well if exists
                            LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                            if (tempInquiry != null) {
                                tempInquiry.setContact(tempContact);
                                tempInquiry.save();
                            }
                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                moveToContactDetailScreen(tempContact);
                            } else {
                                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                tempInquiry.save();
                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                dataSenderAsync.execute();
                            }
                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } else if (launchMode.equals(LAUNCH_MODE_CONVERT_IGNORED)) {
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
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
                        tempContact.save();
                        Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                        finish();
                        if (checkContact != null) {
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(selectedContactType);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                    moveToContactDetailScreen(checkContact);
                                } else {
                                    //update inquiry as well if exists
                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                                    if (tempInquiry != null) {
                                        tempInquiry.setContact(checkContact);
                                        tempInquiry.save();
                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                        tempInquiry.save();
                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                        dataSenderAsync.execute();
                                    }
                                }
                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(selectedContactType);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            //update inquiry as well if exists
                            LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                            if (tempInquiry != null) {
                                tempInquiry.setContact(tempContact);
                                tempInquiry.save();
                            }
                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                moveToContactDetailScreen(tempContact);
                            } else {
                                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                tempInquiry.save();
                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                dataSenderAsync.execute();
                            }
                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
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
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(selectedContactType);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                    moveToContactDetailScreen(checkContact);
                                } else {
                                    //update inquiry as well if exists
                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                                    if (tempInquiry != null) {
                                        tempInquiry.setContact(checkContact);
                                        tempInquiry.save();
                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                        tempInquiry.save();
                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                        dataSenderAsync.execute();
                                    }
                                }
                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(selectedContactType);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                moveToContactDetailScreen(tempContact);
                            } else {
                                //update inquiry as well if exists
                                LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                                if (tempInquiry != null) {
                                    tempInquiry.setContact(checkContact);
                                    tempInquiry.save();
                                    tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                    tempInquiry.save();
                                    DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                    dataSenderAsync.execute();
                                }
                            }
                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
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
                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                                checkContact.setContactName(contactName);
                                checkContact.setPhoneOne(intlNum);
                                checkContact.setContactType(selectedContactType);
                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                                checkContact.save();
                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                    moveToContactDetailScreen(checkContact);
                                } else {
                                    //update inquiry as well if exists
                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                                    if (tempInquiry != null) {
                                        tempInquiry.setContact(checkContact);
                                        tempInquiry.save();
                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                        tempInquiry.save();
                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                        dataSenderAsync.execute();
                                    }
                                }
                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            LSContact tempContact = new LSContact();
                            tempContact.setContactName(contactName);
                            tempContact.setPhoneOne(intlNum);
                            tempContact.setContactType(selectedContactType);
                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
                            tempContact.save();
                            //update inquiry as well if exists
                            LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
                            if (tempInquiry != null) {
                                tempInquiry.setContact(tempContact);
                                tempInquiry.save();
                            }
                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                                moveToContactDetailScreen(tempContact);
                            } else {
                                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
                                tempInquiry.save();
                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                                dataSenderAsync.execute();
                            }
                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } else if (launchMode.equals(LAUNCH_MODE_CONVERT_IGNORED)) {
                    etContactName.setError(null);
                    etContactPhone.setError(null);
                    boolean validation = true;
                    String contactName = etContactName.getText().toString();
                    String contactPhone = etContactPhone.getText().toString();
                    if (contactName.equals("") || contactName.length() < 3) {
                        validation = false;
                        etContactName.setError("Invalid Name!");
                    }
                } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
                    if (isValid(contactName, contactPhone, contactEmail)) {
                        LSContact tempContact = selectedContact;
                        String oldType = selectedContact.getContactType();
                        tempContact.setContactName(contactName);
                        tempContact.setPhoneOne(intlNum);
                        tempContact.setContactType(selectedContactType);
                        tempContact.setContactEmail(contactEmail);
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
                    }
                    finish();
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                }
                SalesContactAddedEventModel mCallEvent = new SalesContactAddedEventModel();
                TinyBus bus = TinyBus.from(getApplicationContext());
                bus.post(mCallEvent);
                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
                dataSenderAsync.execute();
            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        if (launchMode.equals(LAUNCH_MODE_IMPORT_CONTACT)) {
//            llEmailAddress.setVisibility(View.GONE);
//            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
//            tvTitleAddContact.setText(TITLE_IMPORT_CONTACT);
//            editingMode = false;
//        } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
//            llEmailAddress.setVisibility(View.GONE);
//            tvTitleAddContact.setText(TITLE_ADD_NEW_CONTACT);
//            editingMode = false;
//        } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
//            tvTitleAddContact.setText(TITLE_EDIT_CONTACT);
//            editingMode = true;
//            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
//            if (id != null && !id.equals("")) {
//                contactIdLong = Long.parseLong(id);
//            }
//            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
//            if (selectedContact.getContactName() != null && !selectedContact.getContactName().equals("")) {
//                etContactName.setText(selectedContact.getContactName());
//                selectRadioButton(selectedContact.getContactType());
//            } else {
//                etContactName.setText("UNKNOWN");
//            }
//            if (selectedContact.getContactEmail() != null && !selectedContact.getContactEmail().equals("")) {
//                etContactEmail.setText(selectedContact.getContactEmail());
//            } else {
//                etContactEmail.setText("");
//            }
//            etContactPhone.setText(selectedContact.getPhoneOne());
////            etContactPhone.setFocusable(false);
//        } else if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
//            llEmailAddress.setVisibility(View.GONE);
//            tvTitleAddContact.setText(TITLE_TAG_NUMBER);
//            phoneNumberFromLastActivity = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
//            editingMode = false;
//            String internationalNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(phoneNumberFromLastActivity);
//            String nameFromPhoneBook = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), internationalNumber);
//            etContactPhone.setText(internationalNumber);
//            if (nameFromPhoneBook != null) {
//                etContactName.setText(nameFromPhoneBook);
//            }
//        } else if (launchMode.equals(LAUNCH_MODE_CONVERT_IGNORED)) {
//            llEmailAddress.setVisibility(View.GONE);
//            tvTitleAddContact.setText(TITLE_EDIT_CONTACT);
//            editingMode = false;
//            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
//            if (id != null && !id.equals("")) {
//                contactIdLong = Long.parseLong(id);
//            }
//            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
//            if (selectedContact.getContactName() != null && !selectedContact.getContactName().equals("")) {
//                etContactName.setText(selectedContact.getContactName());
//            } else {
//                etContactName.setText("UNKNOWN");
//            }
//            etContactPhone.setText(selectedContact.getPhoneOne());
//        }

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        bSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (launchMode.equals(LAUNCH_MODE_IMPORT_CONTACT)) {
//                    etContactName.setError(null);
//                    etContactPhone.setError(null);
//                    boolean validation = true;
//                    String contactName = etContactName.getText().toString();
//                    String contactPhone = etContactPhone.getText().toString();
//                    if (contactName.equals("") || contactName.length() < 3) {
//                        validation = false;
//                        etContactName.setError("Invalid Name!");
//                    }
//                    if (contactPhone.equals("") || contactPhone.length() < 3) {
//                        validation = false;
//                        etContactPhone.setError("Invalid Number!");
//                    }
//                    if (validation && !editingMode) {
//                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
//                        LSContact checkContact;
//                        checkContact = LSContact.getContactFromNumber(intlNum);
//
//                        if (checkContact != null) {
//                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
//                                checkContact.setContactName(contactName);
//                                checkContact.setPhoneOne(intlNum);
//                                checkContact.setContactType(selectedContactType);
//                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                                checkContact.save();
//                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                    moveToContactDetailScreen(checkContact);
//                                } else {
//                                    //update inquiry as well if exists
//                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
//                                    if (tempInquiry != null) {
//                                        tempInquiry.setContact(checkContact);
//                                        tempInquiry.save();
//                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
//                                        tempInquiry.save();
//                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                        dataSenderAsync.execute();
//                                    }
//                                }
//                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
//                                finish();
//                            } else {
//                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            LSContact tempContact = new LSContact();
//                            tempContact.setContactName(contactName);
//                            tempContact.setPhoneOne(intlNum);
//                            tempContact.setContactType(selectedContactType);
//                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                            tempContact.save();
//                            //update inquiry as well if exists
//                            LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
//                            if (tempInquiry != null) {
//                                tempInquiry.setContact(tempContact);
//                                tempInquiry.save();
//                            }
//                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                moveToContactDetailScreen(tempContact);
//                            } else {
//                                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
//                                tempInquiry.save();
//                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                dataSenderAsync.execute();
//                            }
//                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_CONTACT)) {
//                    etContactName.setError(null);
//                    etContactPhone.setError(null);
//                    boolean validation = true;
//                    String contactName = etContactName.getText().toString();
//                    String contactPhone = etContactPhone.getText().toString();
//                    if (contactName.equals("") || contactName.length() < 3) {
//                        validation = false;
//                        etContactName.setError("Invalid Name!");
//                    }
//                    if (contactPhone.equals("") || contactPhone.length() < 3) {
//                        validation = false;
//                        etContactPhone.setError("Invalid Number!");
//                    }
//                    if (validation && !editingMode) {
//                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
//                        LSContact checkContact;
//                        checkContact = LSContact.getContactFromNumber(intlNum);
//                        if (checkContact != null) {
//                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
//                                checkContact.setContactName(contactName);
//                                checkContact.setPhoneOne(intlNum);
//                                checkContact.setContactType(selectedContactType);
//                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                                checkContact.save();
//                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                    moveToContactDetailScreen(checkContact);
//                                } else {
//                                    //update inquiry as well if exists
//                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
//                                    if (tempInquiry != null) {
//                                        tempInquiry.setContact(checkContact);
//                                        tempInquiry.save();
//                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
//                                        tempInquiry.save();
//                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                        dataSenderAsync.execute();
//                                    }
//                                }
//                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
//                                finish();
//                            } else {
//                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            LSContact tempContact = new LSContact();
//                            tempContact.setContactName(contactName);
//                            tempContact.setPhoneOne(intlNum);
//                            tempContact.setContactType(selectedContactType);
//                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                            tempContact.save();
//                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                moveToContactDetailScreen(tempContact);
//                            } else {
//                                //update inquiry as well if exists
//                                LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
//                                if (tempInquiry != null) {
//                                    tempInquiry.setContact(checkContact);
//                                    tempInquiry.save();
//                                    tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
//                                    tempInquiry.save();
//                                    DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                    dataSenderAsync.execute();
//                                }
//                            }
//                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                } else if (launchMode.equals(LAUNCH_MODE_TAG_PHONE_NUMBER)) {
//                    etContactName.setError(null);
//                    etContactPhone.setError(null);
//                    boolean validation = true;
//                    String contactName = etContactName.getText().toString();
//                    String contactPhone = etContactPhone.getText().toString();
//                    if (contactName.equals("") || contactName.length() < 3) {
//                        validation = false;
//                        etContactName.setError("Invalid Name!");
//                    }
//                    if (contactPhone.equals("") || contactPhone.length() < 3) {
//                        validation = false;
//                        etContactPhone.setError("Invalid Number!");
//                    }
//                    if (validation && !editingMode) {
//                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
//                        LSContact checkContact;
//                        checkContact = LSContact.getContactFromNumber(intlNum);
//                        if (checkContact != null) {
//                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_UNLABELED)) {
//                                checkContact.setContactName(contactName);
//                                checkContact.setPhoneOne(intlNum);
//                                checkContact.setContactType(selectedContactType);
//                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                                checkContact.save();
//                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                    moveToContactDetailScreen(checkContact);
//                                } else {
//                                    //update inquiry as well if exists
//                                    LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
//                                    if (tempInquiry != null) {
//                                        tempInquiry.setContact(checkContact);
//                                        tempInquiry.save();
//                                        tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
//                                        tempInquiry.save();
//                                        DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                        dataSenderAsync.execute();
//                                    }
//                                }
//                                Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
//                                finish();
//                            } else {
//                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            LSContact tempContact = new LSContact();
//                            tempContact.setContactName(contactName);
//                            tempContact.setPhoneOne(intlNum);
//                            tempContact.setContactType(selectedContactType);
//                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                            tempContact.save();
//                            //update inquiry as well if exists
//                            LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
//                            if (tempInquiry != null) {
//                                tempInquiry.setContact(tempContact);
//                                tempInquiry.save();
//                            }
//                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                moveToContactDetailScreen(tempContact);
//                            } else {
//                                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
//                                tempInquiry.save();
//                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                dataSenderAsync.execute();
//                            }
//                            Toast.makeText(AddEditLeadActivity.this, "Contact Saved", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                } else if (launchMode.equals(LAUNCH_MODE_CONVERT_IGNORED)) {
//                    etContactName.setError(null);
//                    etContactPhone.setError(null);
//                    boolean validation = true;
//                    String contactName = etContactName.getText().toString();
//                    String contactPhone = etContactPhone.getText().toString();
//                    if (contactName.equals("") || contactName.length() < 3) {
//                        validation = false;
//                        etContactName.setError("Invalid Name!");
//                    }
//                    if (contactPhone.equals("") || contactPhone.length() < 3) {
//                        validation = false;
//                        etContactPhone.setError("Invalid Number!");
//                    }
//                    if (validation && !editingMode) {
//                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
//                        LSContact checkContact;
//                        checkContact = LSContact.getContactFromNumber(intlNum);
//                        if (checkContact != null) {
//                            if (checkContact.getContactType().equals(LSContact.CONTACT_TYPE_IGNORED)) {
//                                checkContact.setContactName(contactName);
//                                checkContact.setPhoneOne(intlNum);
//                                checkContact.setContactType(selectedContactType);
//                                checkContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                                checkContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                                checkContact.save();
//                                if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                    moveToContactDetailScreen(checkContact);
//                                }
//                                Toast.makeText(AddEditLeadActivity.this, "Saved AS LEAD", Toast.LENGTH_SHORT).show();
//                                finish();
//                            } else {
//                                Toast.makeText(AddEditLeadActivity.this, "Already Exists", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            LSContact tempContact = new LSContact();
//                            tempContact.setContactName(contactName);
//                            tempContact.setPhoneOne(intlNum);
//                            tempContact.setContactType(selectedContactType);
//                            tempContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_ADD_NOT_SYNCED);
//                            tempContact.save();
//                            if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                                moveToContactDetailScreen(tempContact);
//                            }
//                            Toast.makeText(AddEditLeadActivity.this, "Saved AS LEAD", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    }
//                } else if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_CONTACT)) {
//                    etContactName.setError(null);
//                    etContactPhone.setError(null);
//                    etContactEmail.setError(null);
//                    boolean validation = true;
//                    String contactName = etContactName.getText().toString();
//                    String contactPhone = etContactPhone.getText().toString();
//                    String contactEmail = etContactEmail.getText().toString();
//                    if (contactName.equals("") || contactName.length() < 3) {
//                        validation = false;
//                        etContactName.setError("Invalid Name!");
//                    }
//                    if (contactPhone.equals("") || contactPhone.length() < 3) {
//                        validation = false;
//                        etContactPhone.setError("Invalid Number!");
//                    }
////                    if (contactEmail.equals("") || contactEmail.length() < 3) {
////                        validation = false;
////                        etContactEmail.setError("Invalid Email!");
////                    }
//                    if (validation && editingMode) {
//                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone);
//                        LSContact tempContact = selectedContact;
//                        tempContact.setContactName(contactName);
//                        tempContact.setPhoneOne(intlNum);
//                        tempContact.setContactType(selectedContactType);
//                        tempContact.setContactEmail(contactEmail);
//                        if (tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || tempContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
//                            tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
//                        }
//                        tempContact.save();
//                        if (!selectedContactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
//                            moveToContactDetailScreen(tempContact);
//                        } else {
//                            //update inquiry as well if exists
//                            LSInquiry tempInquiry = LSInquiry.getInquiryByNumberIfExists(intlNum);
//                            if (tempInquiry != null) {
//                                tempInquiry.setContact(tempContact);
//                                tempInquiry.save();
//                                tempInquiry.setSyncStatus(SyncStatus.SYNC_STATUS_INQUIRY_DELETE_NOT_SYNCED);
//                                tempInquiry.save();
//                                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                                dataSenderAsync.execute();
//                            }
//                        }
//                        finish();
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
//                    }
//                    SalesContactAddedEventModel mCallEvent = new SalesContactAddedEventModel();
//                    TinyBus bus = TinyBus.from(getApplicationContext());
//                    bus.post(mCallEvent);
//                }
//                DataSenderAsync dataSenderAsync = new DataSenderAsync(getApplicationContext());
//                dataSenderAsync.execute();
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

    private void populateCreateContactView() {
        llEmailAddress.setVisibility(View.GONE);
        tvTitleAddContact.setText(TITLE_ADD_NEW_CONTACT);
        editingMode = false;
    }

    private void populateUpdateContactView(Bundle bundle) {
        tvTitleAddContact.setText(TITLE_EDIT_CONTACT);
        editingMode = true;
        String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
        String num = bundle.getString(TAG_LAUNCH_MODE_PHONE_NUMBER);
        if (id != null && !id.equals("")) {
            contactIdLong = Long.parseLong(id);
            selectedContact = LSContact.findById(LSContact.class, contactIdLong);
        } else if (num != null && !num.equals("")) {
            selectedContact = LSContact.getContactFromNumber(num);
        }
        if (selectedContact.getContactName() != null && !selectedContact.getContactName().equals("")) {
            etContactName.setText(selectedContact.getContactName());
            selectRadioButton(selectedContact.getContactType());
        } else {
            etContactName.setText("UNKNOWN");
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

    private void moveToContactDetailScreen(LSContact contact) {
        Intent detailsActivityIntent = new Intent(AddEditLeadActivity.this, ContactDetailsTabActivity.class);
        long contactId = contact.getId();
        detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
        startActivity(detailsActivityIntent);
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
}