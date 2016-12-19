package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.NotesListAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

import java.util.ArrayList;

/**
 * Created by ibtisam on 12/16/2016.
 */

public class NotesByContactsActivity extends AppCompatActivity {
    public static final String KEY_CONTACT_ID = "contact_id";
    Toolbar toolbar;
    private String contactIdString = "0";
    private LSContact selectedContact;
    private ListView lvNotesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_by_contacts);
        toolbar = (Toolbar) findViewById(R.id.toolbarContactDetailsActivity);
        toolbar.setTitle("Notes");
        EditText editText = (EditText) findViewById(R.id.noteLine);
        ImageView imageView = (ImageView) findViewById(R.id.ivbutton);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvNotesList = (ListView) findViewById(R.id.lvNoteListContactDetailsScreen);
        Bundle extras = getIntent().getExtras();
        Long contactIDLong;
        if (extras != null) {
            contactIdString = extras.getString(NotesByContactsActivity.KEY_CONTACT_ID);
            if (contactIdString != null) {
                contactIDLong = Long.parseLong(contactIdString);
                selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            }
        }
        final ArrayList<LSNote> allNotesOfThisContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId());
        lvNotesList.setAdapter(new NotesListAdapter2(getApplicationContext(),allNotesOfThisContact));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.noteLine);
                //add new note to user
                LSNote tempNote = new LSNote();
                String note = editText.getText().toString();
                tempNote.setNoteText(note);
                tempNote.setContactOfNote(selectedContact);
                tempNote.save();
                editText.setText(null);
                lvNotesList.setAdapter(new NotesListAdapter2(getApplicationContext(),(ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId())));
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(NotesByContactsActivity.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
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
