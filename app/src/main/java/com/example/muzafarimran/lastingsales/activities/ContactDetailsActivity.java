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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.NotesByContactsFragment;
import com.example.muzafarimran.lastingsales.fragments.NotesListFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;

public class ContactDetailsActivity extends AppCompatActivity {

    public static final String KEY_CONTACT_ID = "contact_id";
    Toolbar toolbar;
    TempFollowUp selectedFolloup = null;
    private String contactIdString = "0";
    private LSContact selectedContact;
    private Button addFollowupBtn;
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
//    private ListView lvNotesList;
    private FrameLayout notesListHolderFrameLayout;
    private NotesListFragment notesListFragment;
    private NotesByContactsFragment notesByContactsFragment;
    private LinearLayout llFolloupNoteRow;
    private LinearLayout llFolloupDateTimeRow;
    private TextView tvFollowupNoteText;
    private TextView tvFollowupDateTime;
    private ImageButton ibEditFollowup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        addFollowupBtn = (Button) findViewById(R.id.bAddFollowupContactDetailsScreen);
        tvName = (TextView) findViewById(R.id.tvNameOfUserContactDetailsScreen);
        toolbar = (Toolbar) findViewById(R.id.toolbarContactDetailsActivity);
//        toolbar.setTitle("Contact Details");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvNumberOne = (TextView) findViewById(R.id.tvPhoneOneOfUserContactDetailsScreen);
        tvNumberTwo = (TextView) findViewById(R.id.tvPhoneTwoOfUserContactDetailsScreen);
        tvDescription = (TextView) findViewById(R.id.tvDescriptionContactDetails);
        tvDescriptionTitle = (TextView) findViewById(R.id.tvDescriptionContactDetailsTitle);
        tvEmail = (TextView) findViewById(R.id.tvEmailOfUserContactDetailsScreen);
        tvAddress = (TextView) findViewById(R.id.tvAddressContactDetails);
        tvAddressTitle = (TextView) findViewById(R.id.tvAddressContactDetailsTitle);
        tvCompany = (TextView) findViewById(R.id.tvCompanyContactDetails);
        tvCompanyTitle = (TextView) findViewById(R.id.tvCompanyContactDetailsTitle);
        llFolloupNoteRow = (LinearLayout) findViewById(R.id.followupNoteRow);
        llFolloupDateTimeRow = (LinearLayout) findViewById(R.id.followupDateTimeRow);
        tvFollowupNoteText = (TextView) findViewById(R.id.followupNoteText);
        tvFollowupDateTime = (TextView) findViewById(R.id.followupDateTimeText);
        ibEditFollowup = (ImageButton) findViewById(R.id.ibEditFollowupButton);
//        lvNotesList = (ListView) findViewById(R.id.lvNoteListContactDetailsScreen);
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
                toolbar.setTitle(selectedContact.getContactName());
                setSupportActionBar(toolbar);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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


        // Ibtisam working here
//        ArrayList<LSNote> allNotesOfThisContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId());
//        lvNotesList.setAdapter(new NotesListAdapter(getApplicationContext(),allNotesOfThisContact));

//        if (savedInstanceState == null) {
//            notesListFragment = new NotesListFragment();
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.notesListHolderFrameLayout, notesListFragment);
//            transaction.commit();
//        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString(NotesByContactsFragment.CONTACT_ID, selectedContact.getId().toString());
            notesByContactsFragment = new NotesByContactsFragment();
            notesByContactsFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.notesListHolderFrameLayout, notesByContactsFragment);
            transaction.commit();
        }


        addFollowupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), TagNumberAndAddFollowupActivity.class);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_FOLLOWUP);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, selectedContact.getId() + "");
                startActivity(myIntent);
            }
        });

        ArrayList<TempFollowUp> allFollowupsOfThisContact = selectedContact.getAllFollowups();
//        ArrayList<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getAllFollowupsFromContactId(selectedContact.getId()+"");
        Calendar now = Calendar.getInstance();
        if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
            for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                if (oneFollowup.getDateTimeForFollowup() > now.getTimeInMillis()) {
                    selectedFolloup = oneFollowup;
                    break;
                }
            }
        }
        if (selectedFolloup == null) {
            llFolloupNoteRow.setVisibility(View.GONE);
            llFolloupDateTimeRow.setVisibility(View.GONE);
            ibEditFollowup.setVisibility(View.GONE);
        } else {
            tvFollowupNoteText.setText(selectedFolloup.getTitle());
            Calendar followupTimeDate = Calendar.getInstance();
            followupTimeDate.setTimeInMillis(selectedFolloup.getDateTimeForFollowup());
            String dateTimeForFollowupString;
            dateTimeForFollowupString = followupTimeDate.get(Calendar.DAY_OF_MONTH) + "-"
                    + (followupTimeDate.get(Calendar.MONTH) + 1) + "-" + followupTimeDate.get(Calendar.YEAR)
                    + " at " + followupTimeDate.get(Calendar.HOUR_OF_DAY) + " : " + followupTimeDate.get(Calendar.MINUTE);
            tvFollowupDateTime.setText(dateTimeForFollowupString);
            addFollowupBtn.setVisibility(View.GONE);
        }
        ibEditFollowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), TagNumberAndAddFollowupActivity.class);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_EDIT_EXISTING_FOLLOWUP);
                myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, selectedContact.getId() + "");
                myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_FOLLOWUP_ID, selectedFolloup.getId() + "");
                startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_options_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_action_edit:
                Intent addContactScreenIntent = new Intent(getApplicationContext(), TagNumberAndAddFollowupActivity.class);
                addContactScreenIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                addContactScreenIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, contactIdString);
                startActivity(addContactScreenIntent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}