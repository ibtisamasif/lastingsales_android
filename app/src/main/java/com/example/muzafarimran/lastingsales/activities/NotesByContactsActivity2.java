package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.NotesByContactsFragment;

/**
 * Created by ibtisam on 12/16/2016.
 */

public class NotesByContactsActivity2 extends AppCompatActivity {
    public static final String KEY_CONTACT_ID = "contact_id";
    Toolbar toolbar;
    private String contactIdString = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_by_contacts);

        toolbar = (Toolbar) findViewById(R.id.toolbarContactDetailsActivity);
        toolbar.setTitle("Notes");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        Long contactIDLong;
        if (extras != null) {
            contactIdString = extras.getString(NotesByContactsActivity2.KEY_CONTACT_ID);
            if (contactIdString != null) {
                Bundle bundle = new Bundle();
                bundle.putString(NotesByContactsFragment.CONTACT_ID, contactIdString);
                NotesByContactsFragment fragobj = new NotesByContactsFragment();
                fragobj.setArguments(bundle);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.FragmentContainer, fragobj);
                transaction.commit();
            }
        }
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
