package com.example.muzafarimran.lastingsales.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Events.ColleagueContactAddedEventModel;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.Events.PersonalContactAddedEventModel;
import com.example.muzafarimran.lastingsales.Events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

import java.util.ArrayList;

import de.halfbit.tinybus.TinyBus;

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;
    private static final String TAG = "AddContactActivity";

    Button importContact = null;
    Button salesRadio = null;
    Button collegueRadio = null;
    Button personalRadio = null;
    Button currentSelectedRadio = null;
    Button addContactCTA = null;

    EditText contactName = null;
    EditText contactPhone = null;
    EditText contactEmail = null;
    EditText contactNotes = null;

    private Uri uriContact;

    contactImportClickListener contactimportclicklistener = new contactImportClickListener();
    changeSelectedContactType changeselectedcontactType = new changeSelectedContactType();
    private String contactID;


    String selectedContactType = LSContact.CONTACT_TYPE_SALES;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        importContact = (Button) findViewById(R.id.b_import_contact);
        salesRadio = (Button) findViewById(R.id.sales_radio);
        currentSelectedRadio = salesRadio;

        collegueRadio = (Button) findViewById(R.id.collegue_radio);
        personalRadio = (Button) findViewById(R.id.personal_radio);
        addContactCTA = (Button) findViewById(R.id.add_contact_cta);

        importContact.setOnClickListener(this.contactimportclicklistener);
//        importContact.setOnClickListener(this);

//
//        importContact.setOnClickListener(this.changeselectedcontactType);
//        importContact.setOnClickListener(this.changeselectedcontactType);
//        importContact.setOnClickListener(this.changeselectedcontactType);

        contactName = (EditText) findViewById(R.id.contact_name);
        contactPhone = (EditText) findViewById(R.id.contact_phone);
        contactEmail = (EditText) findViewById(R.id.contact_email);
        contactNotes = (EditText) findViewById(R.id.contact_notes);

        Bundle bundle = getIntent().getExtras();

        String num = "";
        if (bundle != null) {



            num = bundle.getString(ContactCallDetails.NUMBER_EXTRA);

            contactPhone.setText(num);
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

        addContactCTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LSContact tempContact = new LSContact();

                tempContact.setContactName(contactName.getText().toString());
                tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(contactPhone.getText().toString()));
                tempContact.setContactEmail(contactEmail.getText().toString());
                tempContact.setContactType(selectedContactType);

                tempContact.save();

                String noteText = contactNotes.getText().toString();

                if (noteText.length() > 0) {
                    LSNote tempNote = new LSNote();

                    tempNote.setNoteText(noteText);
                    tempNote.setContactOfNote(tempContact);

                    tempNote.save();

//                    LSNote.listAll(LSNote.class);

                    ArrayList<LSNote> allNotes = (ArrayList<LSNote>) LSNote.listAll(LSNote.class);

//                    if (allNotes.size()>0)

                }

                ArrayList<LSContact> allContacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);

                for (int counter1 = 0; counter1 < allContacts.size(); counter1++) {
                    Toast.makeText(AddContactActivity.this, "name: " + allContacts.get(counter1).getContactName(), Toast.LENGTH_SHORT).show();
                }

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


    /*to be seen*/
    private String retrieveContactName() {
        String contactName = null;

        // querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            contactEmail = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        }
        cursor.close();
        // Log.d(TAG, "Contact Name: " + contactName);
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


            contactName.setText(name);
            contactPhone.setText(number);
            contactEmail.setText(email);

            if (PhoneNumberAndCallUtils.isNumeric(name)) {
                contactPhone.setText(number);
                contactName.setText("");
            }

        }
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "clockedImport button", Toast.LENGTH_SHORT).show();
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


}
