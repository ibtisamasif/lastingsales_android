package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.NotesListFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

public class ContactDetailsActivity extends AppCompatActivity {

    public static final String KEY_CONTACT_ID = "contact_id";
    private String contactIdString = "0";
    private LSContact selectedContact;
    private TextView tvName;
    private TextView tvNumberOne;
    private TextView tvNumberTwo;
    private TextView tvDescription;
    private TextView tvDescriptionTitle;
    private TextView tvEmail;
    private TextView tvAddress;
    private TextView tvCompanyTitle;
    private TextView tvCompany;
    private TextView tvAddressTitle;
    private ListView lvNotesList;
    private FrameLayout notesListHolderFrameLayout;
    private NotesListFragment notesListFragment;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        tvName = (TextView) findViewById(R.id.tvNameOfUserContactDetailsScreen);
        toolbar = (Toolbar) findViewById(R.id.toolbarContactDetailsActivity);
        toolbar.setTitle("Contact Details");
        setSupportActionBar(toolbar);
        tvNumberOne = (TextView) findViewById(R.id.tvPhoneOneOfUserContactDetailsScreen);
        tvNumberTwo = (TextView) findViewById(R.id.tvPhoneTwoOfUserContactDetailsScreen);
        tvDescription = (TextView) findViewById(R.id.tvDescriptionContactDetails);
        tvDescriptionTitle = (TextView) findViewById(R.id.tvDescriptionContactDetailsTitle);
        tvEmail = (TextView) findViewById(R.id.tvEmailOfUserContactDetailsScreen);
        tvAddress = (TextView) findViewById(R.id.tvAddressContactDetails);
        tvAddressTitle = (TextView) findViewById(R.id.tvAddressContactDetailsTitle);
        tvCompany = (TextView) findViewById(R.id.tvCompanyContactDetails);
        tvCompanyTitle = (TextView) findViewById(R.id.tvCompanyContactDetailsTitle);
//        lvNotesList = (ListView) findViewById(R.id.lvNotesListContactDetailsScreen);
        notesListHolderFrameLayout = (FrameLayout) findViewById(R.id.notesListHolderFrameLayout);
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
            if (selectedContact.getContactName() == null || selectedContact.getContactName().equals("")) {
                tvName.setVisibility(View.GONE);
            } else {
            tvName.setText(selectedContact.getContactName());
            }
            if (selectedContact.getPhoneOne() == null || selectedContact.getPhoneOne().equals("")) {
                tvNumberOne.setVisibility(View.GONE);
            } else {
                tvNumberOne.setText(selectedContact.getPhoneOne());
            }
            if (selectedContact.getPhoneTwo() == null || selectedContact.getPhoneTwo().equals("")) {
                tvNumberTwo.setVisibility(View.GONE);
            } else {
                tvNumberTwo.setText(selectedContact.getPhoneTwo());
            }
            if (selectedContact.getContactDescription() == null || selectedContact.getContactDescription().equals("")) {
                tvDescription.setVisibility(View.GONE);
                tvDescriptionTitle.setVisibility(View.GONE);
            } else {
                tvDescription.setText(selectedContact.getContactDescription());
            }
            if (selectedContact.getContactEmail() == null || selectedContact.getContactEmail().equals("")) {
                tvEmail.setVisibility(View.GONE);
            } else {
                tvEmail.setText(selectedContact.getContactEmail());
            }
            if (selectedContact.getContactAddress() == null || selectedContact.getContactAddress().equals("")) {
                tvAddress.setVisibility(View.GONE);
                tvAddressTitle.setVisibility(View.GONE);
            } else {
                tvAddress.setText(selectedContact.getContactAddress());
            }
            if (selectedContact.getContactCompany() == null || selectedContact.getContactCompany().equals("")) {
                tvCompany.setVisibility(View.GONE);
                tvCompanyTitle.setVisibility(View.GONE);
            } else {
                tvCompany.setText(selectedContact.getContactCompany());
            }
//            tvType.setText(selectedContact.getContactType());
        }
//        lvNotesList.setAdapter();

        if (savedInstanceState == null) {
            notesListFragment = new NotesListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.notesListHolderFrameLayout, notesListFragment);
            transaction.commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_options_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ic_action_edit:
                Intent addContactScreenIntent = new Intent(getApplicationContext(), AddContactActivity.class);
                addContactScreenIntent.putExtra(KEY_CONTACT_ID, contactIdString);
                startActivity(addContactScreenIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}