package com.example.muzafarimran.lastingsales.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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

import com.example.muzafarimran.lastingsales.Events.ColleagueContactAddedEventModel;
import com.example.muzafarimran.lastingsales.Events.PersonalContactAddedEventModel;
import com.example.muzafarimran.lastingsales.Events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

import java.util.ArrayList;

import de.halfbit.tinybus.TinyBus;

import static android.view.View.GONE;
import static com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils.updateAllCallsOfThisContact;
@Deprecated
public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    private static final String TAG = "AddContactActivity";
    contactImportClickListener contactimportclicklistener = new contactImportClickListener();
    changeSelectedContactType changeselectedcontactType = new changeSelectedContactType();
    String idOfEditContactString = "";
    Long idOfEditContactLong = -1L;
    String selectedContactType = LSContact.CONTACT_TYPE_SALES;
    LSContact editingContact = null;
    Boolean editContactFlow = false;
    private Button bImportContact = null;
    private Button salesRadio = null;
    private Button collegueRadio = null;
    private Button personalRadio = null;
    private Button currentSelectedRadio = null;
    private Button bAddContactCTA = null;
    private Button bSaveContactCTA = null;
    private EditText etContactName = null;
    private EditText etContactPhone = null;
    private EditText etContactEmail = null;
    private EditText etContactNotes = null;
    private EditText etContactPhoneTwo = null;
    private EditText etContactDescription = null;
    private EditText etContactCompany = null;
    private EditText etContactAddress = null;
    private LinearLayout llContactPhoneTwo = null;
    private LinearLayout llContactDescription = null;
    private LinearLayout llContactCompany = null;
    private LinearLayout llContactAddress = null;
    private Uri uriContact;
    private String contactID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        bImportContact = (Button) findViewById(R.id.b_import_contact);
        salesRadio = (Button) findViewById(R.id.sales_radio);
        currentSelectedRadio = salesRadio;
        collegueRadio = (Button) findViewById(R.id.collegue_radio);
        personalRadio = (Button) findViewById(R.id.personal_radio);
        bAddContactCTA = (Button) findViewById(R.id.add_contact_cta);
        bSaveContactCTA = (Button) findViewById(R.id.save_contact_cta);
        bImportContact.setOnClickListener(this.contactimportclicklistener);
        etContactName = (EditText) findViewById(R.id.contact_name);
        etContactPhone = (EditText) findViewById(R.id.contact_phone);
        etContactEmail = (EditText) findViewById(R.id.contact_email);
        etContactNotes = (EditText) findViewById(R.id.contact_notes);
        etContactPhoneTwo = (EditText) findViewById(R.id.et_contact_phone_two);
        etContactDescription = (EditText) findViewById(R.id.et_contact_description);
        etContactCompany = (EditText) findViewById(R.id.et_contact_company);
        etContactAddress = (EditText) findViewById(R.id.et_contact_address);
        llContactPhoneTwo = (LinearLayout) findViewById(R.id.phone_two_layout);
        llContactDescription = (LinearLayout) findViewById(R.id.description_layout);
        llContactCompany = (LinearLayout) findViewById(R.id.company_layout);
        llContactAddress = (LinearLayout) findViewById(R.id.address_layout);
        Bundle bundle = getIntent().getExtras();
        String num = "";
        String cat = "";
        if (bundle != null) {
            num = bundle.getString(TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER); //TODO Review later
            etContactPhone.setText(num);
            etContactName.setText(PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(getApplicationContext(), num));
            idOfEditContactString = bundle.getString(ContactDetailsActivity.KEY_CONTACT_ID);
            if (idOfEditContactString != null && idOfEditContactString != "") {
                editContactFlow = true;
                idOfEditContactLong = Long.parseLong(idOfEditContactString);
            }

//            cat = bundle.getString(TagNumberActivity.CATEGORY);
//            if(cat!=null) {
//                if (cat.equalsIgnoreCase("sales") && cat != "") {
//                    Log.d(TAG, "onCreate: Bundle Sales");
//                    selectedContactType = LSContact.CONTACT_TYPE_SALES;
//                    salesRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
//                    collegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
//                } else if (cat.equalsIgnoreCase("collegue") && cat != "") {
//                    Log.d(TAG, "onCreate: Bundle Colleagues");
//                    selectedContactType = LSContact.CONTACT_TYPE_COLLEAGUE;
//                    salesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
//                    collegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
//
//                }
//            }
        }
        if (!editContactFlow) {
            hideEditFields();
        } else {
            hideAddContactFields();
            editingContact = LSContact.findById(LSContact.class, idOfEditContactLong);
            etContactName.setText(editingContact.getContactName());
            etContactPhone.setText(editingContact.getPhoneOne());
            etContactEmail.setText(editingContact.getContactEmail());
            etContactPhoneTwo.setText(editingContact.getPhoneTwo());
            etContactDescription.setText(editingContact.getContactDescription());
            etContactCompany.setText(editingContact.getContactCompany());
            etContactAddress.setText(editingContact.getContactAddress());
        }

        salesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactType = LSContact.CONTACT_TYPE_SALES;
                salesRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
                collegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                personalRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
            }
        });
        collegueRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactType = LSContact.CONTACT_TYPE_COLLEAGUE;
                salesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                collegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
                personalRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
            }
        });
        personalRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactType = LSContact.CONTACT_TYPE_PERSONAL;
                salesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                collegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                personalRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            }
        });
        bAddContactCTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if (validation) {
                    LSContact tempContact = new LSContact();
                    tempContact.setContactName(contactName);
                    tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone));
                    tempContact.setContactEmail(contactEmail);
                    tempContact.setContactType(selectedContactType);
                    tempContact.save();
//                    updating all previous calls of this contact adding contact to call and removing contact name
                    updateAllCallsOfThisContact(tempContact);
                    String noteText = etContactNotes.getText().toString();
                    if (noteText.length() > 0) {
                        LSNote tempNote = new LSNote();
                        tempNote.setNoteText(noteText);
                        tempNote.setContactOfNote(tempContact);
                        tempNote.save();
                    }
                    if (tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                        tempContact.setContactSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                        tempContact.save();
                        SalesContactAddedEventModel mCallEvent = new SalesContactAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.register(mCallEvent);
                        bus.post(mCallEvent);
                        Log.d(TAG, "onSaveContact() called with: Name = [" + tempContact.getContactName() + "], number = [" + tempContact.getPhoneOne() + "], type = [" + tempContact.getContactType() + "]");
                    } else if (tempContact.getContactType().equals(LSContact.CONTACT_TYPE_COLLEAGUE)) {
                        ColleagueContactAddedEventModel mCallEvent = new ColleagueContactAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.register(mCallEvent);
                        bus.post(mCallEvent);
                        Log.d(TAG, "onSaveContact() called with: Name = [" + tempContact.getContactName() + "], number = [" + tempContact.getPhoneOne() + "], type = [" + tempContact.getContactType() + "]");
                    } else if (tempContact.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
                        PersonalContactAddedEventModel mCallEvent = new PersonalContactAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.register(mCallEvent);
                        bus.post(mCallEvent);
                        Log.d(TAG, "onSaveContact() called with: Name = [" + tempContact.getContactName() + "], number = [" + tempContact.getPhoneOne() + "], type = [" + tempContact.getContactType() + "]");
                    }
                    finish();
                }
            }
        });
        bSaveContactCTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LSContact tempContact = editingContact;
                tempContact.setContactName(etContactName.getText().toString());
                tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(etContactPhone.getText().toString()));
                tempContact.setContactEmail(etContactEmail.getText().toString());
                tempContact.setContactType(selectedContactType);
                tempContact.setPhoneTwo(etContactPhoneTwo.getText().toString());
                tempContact.setContactAddress(etContactAddress.getText().toString());
                tempContact.setContactDescription(etContactDescription.getText().toString());
                tempContact.setContactCompany(etContactCompany.getText().toString());
                tempContact.save();
//                    updating all previous calls of this contact adding contact to call and removing contact name
                updateAllCallsOfThisContact(tempContact);
                if (tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                    SalesContactAddedEventModel mCallEvent = new SalesContactAddedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.register(mCallEvent);
                    bus.post(mCallEvent);
                    Log.d(TAG, "onSaveContact() called with: Name = [" + tempContact.getContactName() + "], number = [" + tempContact.getPhoneOne() + "], type = [" + tempContact.getContactType() + "]");
                } else if (tempContact.getContactType().equals(LSContact.CONTACT_TYPE_COLLEAGUE)) {
                    ColleagueContactAddedEventModel mCallEvent = new ColleagueContactAddedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.register(mCallEvent);
                    bus.post(mCallEvent);
                    Log.d(TAG, "onSaveContact() called with: Name = [" + tempContact.getContactName() + "], number = [" + tempContact.getPhoneOne() + "], type = [" + tempContact.getContactType() + "]");
                } else if (tempContact.getContactType().equals(LSContact.CONTACT_TYPE_PERSONAL)) {
                    PersonalContactAddedEventModel mCallEvent = new PersonalContactAddedEventModel();
                    TinyBus bus = TinyBus.from(getApplicationContext());
                    bus.register(mCallEvent);
                    bus.post(mCallEvent);
                    Log.d(TAG, "onSaveContact() called with: Name = [" + tempContact.getContactName() + "], number = [" + tempContact.getPhoneOne() + "], type = [" + tempContact.getContactType() + "]");
                }
                finish();
            }
        });
    }

    private void hideAddContactFields() {
        bAddContactCTA.setVisibility(GONE);
        bImportContact.setVisibility(GONE);
    }

    private void hideEditFields() {
        llContactPhoneTwo.setVisibility(GONE);
        llContactDescription.setVisibility(GONE);
        llContactCompany.setVisibility(GONE);
        llContactAddress.setVisibility(GONE);
        bSaveContactCTA.setVisibility(GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String email = "";
        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            uriContact = data.getData();
            String name = retrieveContactName();
            String number = retrieveContactNumber();
            try {
//                email = retrieveContactEmail();
                email = getContactDetailsEmail();
            } catch (Exception e) {
                e.printStackTrace();
                email = "";
            }
//            retrieveContactPhoto();
            etContactName.setText(name);
            etContactPhone.setText(number);
            etContactEmail.setText(email);
            if (PhoneNumberAndCallUtils.isNumeric(name)) {
                etContactPhone.setText(number);
                etContactName.setText("");
            }

        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "clockedImport button", Toast.LENGTH_SHORT).show();
    }

    /**
     * this method can read all of the contact information, but here it is being used to read only the email data of the contact
     *
     * @return
     */
    public String getContactDetailsEmail() {

        ArrayList nameList = new ArrayList<String>();
        ArrayList phoneList = new ArrayList<String>();
        ArrayList emailList = new ArrayList<String>();

        ContentResolver cr = getContentResolver();
//        Commented below line because it queries for all contacts while the below used
//        queries for only the selected contact
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        Cursor cur = cr.query(uriContact, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer
                        .parseInt(cur.getString(cur
                                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    // Query phone here. Covered next

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        // Do something with phones
                        String phoneNo = pCur
                                .getString(pCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        nameList.add(name); // Here you can list of contact.
                        phoneList.add(phoneNo); // Here you will get list of phone number.

                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                            emailList.add(email); // Here you will get list of email

//                            If more than one email address is needed remove this break;
//                            break;
                        }
                        emailCur.close();
                    }
                    pCur.close();
                }
            }
        }

        if (emailList.size() > 0) {
            return emailList.get(0).toString();
        } else {
            return "";
        }
//        return nameList; // here you can return whatever you want.
    }

    public class contactImportClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // using native contacts selection
            // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
        }
    }

    public class changeSelectedContactType implements View.OnClickListener {
        @Override
        public void onClick(View view) {


            //currentSelectedRadio.setBackground(getResources().getDrawable(R.drawable.ready, null));

            /*String tag =  String.valueOf(view.getTag());

            switch(tag){

                case "sales_radio":

                    break;

                case "collegue_radio":

                    break;

                case "personal_radio":

                    break;
            }*/
        }
    }
}