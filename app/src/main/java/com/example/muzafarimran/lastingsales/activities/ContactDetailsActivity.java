package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

public class ContactDetailsActivity extends AppCompatActivity {

    public static final String KEY_CONTACT_ID = "contact_id";
    Button bEditContactButton;
    String contactIdString = "0";
    LSContact selectedContact;
    TextView tvName;
    TextView tvNumberOne;
    TextView tvNumberTwo;
    TextView tvDescription;
    TextView tvEmail;
    TextView tvAddress;
    TextView tvCompany;
    TextView tvType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        tvName = (TextView) findViewById(R.id.tvNameContactDetails);
        tvNumberOne = (TextView) findViewById(R.id.tvNumberContactDetails);
        tvNumberTwo = (TextView) findViewById(R.id.tvNumber2ContactDetails);
        tvDescription = (TextView) findViewById(R.id.tvDescriptionContactDetails);
        tvEmail = (TextView) findViewById(R.id.tvEmailContactDetails);
        tvAddress = (TextView) findViewById(R.id.tvAddressContactDetails);
        tvCompany = (TextView) findViewById(R.id.tvCompanyContactDetails);
        tvType = (TextView) findViewById(R.id.tvTypeContactDetails);
        Bundle extras = getIntent().getExtras();
        Long contactIDLong;
        if (extras != null) {
            contactIdString = extras.getString(ContactDetailsActivity.KEY_CONTACT_ID);
            if (contactIdString != null) {
                contactIDLong = Long.parseLong(contactIdString);
                selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            }
        }
        if (selectedContact != null) {
            tvName.setText(selectedContact.getContactName());
            tvNumberOne.setText(selectedContact.getPhoneOne());
            tvNumberTwo.setText(selectedContact.getPhoneTwo());
            tvDescription.setText(selectedContact.getContactDescription());
            tvEmail.setText(selectedContact.getContactEmail());
            tvAddress.setText(selectedContact.getContactAddress());
            tvCompany.setText(selectedContact.getContactCompany());
            tvType.setText(selectedContact.getContactType());
        }
        bEditContactButton = (Button) findViewById(R.id.bEditContactContactDetailsScreen);
        bEditContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addContactScreenIntent = new Intent(getApplicationContext(), AddContactActivity.class);
                addContactScreenIntent.putExtra(KEY_CONTACT_ID, contactIdString);
                startActivity(addContactScreenIntent);
                finish();
            }
        });
    }
}