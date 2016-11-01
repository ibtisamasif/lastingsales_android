package com.example.muzafarimran.lastingsales.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.muzafarimran.lastingsales.R;

public class AddContactActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CONTACTS = 10;

    Button importContact = null;
    Button salesRadio  = null;
    Button collegueRadio = null;
    Button personalRadio = null;
    Button currentSelectedRadio = null;

    EditText contactName = null;
    EditText contactPhone = null;
    EditText contactEmail = null;
    EditText contactNotes = null;

    private Uri uriContact = null;




    contactImportClickListener contactimportclicklistener = new contactImportClickListener();
    changeSelectedContactType  changeselectedcontactType  = new changeSelectedContactType();
    private String contactID;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        importContact = (Button) findViewById(R.id.import_contact);
        salesRadio = (Button) findViewById(R.id.sales_radio);
        currentSelectedRadio = salesRadio;

        collegueRadio = (Button) findViewById(R.id.collegue_radio);
        personalRadio = (Button) findViewById(R.id.personal_radio);

        importContact.setOnClickListener(this.contactimportclicklistener);
        importContact.setOnClickListener(this.changeselectedcontactType);
        importContact.setOnClickListener(this.changeselectedcontactType);
        importContact.setOnClickListener(this.changeselectedcontactType);

        contactName = (EditText) findViewById(R.id.contact_name);
        contactPhone = (EditText) findViewById(R.id.contact_phone);
        contactEmail = (EditText) findViewById(R.id.contact_email);
        contactNotes = (EditText) findViewById(R.id.contact_notes);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String email="";

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {

            uriContact = data.getData();


            String name = retrieveContactName();
            //String number = retrieveContactNumber();
            try
            {

//                email = retrieveContactEmail();
                //email = getContactDetailsEmail();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                email = "";
            }


//            retrieveContactPhoto();


            contactName.setText(name);
            //contactPhone.setText(number);
            //etEmail.setText(email);

        }
    }




    public class contactImportClickListener implements View.OnClickListener{


        @Override
        public void onClick(View view)
        {

            // using native contacts selection
            // Intent.ACTION_PICK = Pick an item from the data, returning what was selected.
            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);

        }
    }


    public class changeSelectedContactType implements View.OnClickListener{
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
