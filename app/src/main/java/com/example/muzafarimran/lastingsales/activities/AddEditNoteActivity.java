package com.example.muzafarimran.lastingsales.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.MixpanelConfig;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/12/2017.
 */

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String TAG = "AddEditNoteActivity";

    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";

    public static final String LAUNCH_MODE_ADD_NEW_NOTE = "launch_mode_add_new_note";
    public static final String LAUNCH_MODE_EDIT_EXISTING_NOTE = "launch_mode_edit_existing_note";

    public static final String TAG_LAUNCH_MODE_NOTE_ID = "launch_mode_note_id";
    public static final String TAG_LAUNCH_MODE_CONTACT_NUMBER = "launch_mode_contact_number";
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "launch_mode_contact_id";

    public static final String ADD_NOTE_CONTACT_NUMBER = "add_note_contact_number";

    String launchMode = LAUNCH_MODE_ADD_NEW_NOTE;

    TextView tvContactName;
    EditText etContactNote;
    Button bSave, bCancel;
    View view;
    LSNote selectedNote;
    LSContact selectedContact;
    String number;
    Long contactIdLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        tvContactName = (TextView) findViewById(R.id.contact_name_add_note);
        etContactNote = (EditText) findViewById(R.id.contact_note_add_note);
        bSave = (Button) findViewById(R.id.ok_add_note);
        bCancel = (Button) findViewById(R.id.cancel_add_note);


        String projectToken = MixpanelConfig.projectToken;
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
        mixpanel.track("Notes - Activity Opened");
        Bundle bundle = getIntent().getExtras();
        Long noteIdLong = 0l;

        if (bundle != null) {
            launchMode = bundle.getString(ACTIVITY_LAUNCH_MODE);
        }

        if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_NOTE)) {
            // for edit note
            noteIdLong = Long.parseLong(bundle.getString(TAG_LAUNCH_MODE_NOTE_ID));
            selectedNote = LSNote.findById(LSNote.class, noteIdLong);
            etContactNote.setText(selectedNote.getNoteText());
            selectedContact = selectedNote.getContactOfNote();
            tvContactName.setText(selectedContact.getContactName());

        } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_NOTE)) {
            //for add new note
            number = bundle.getString(TAG_LAUNCH_MODE_CONTACT_NUMBER);
            Long id = bundle.getLong(TAG_LAUNCH_MODE_CONTACT_ID);
            if (number != null) {
                selectedContact = LSContact.getContactFromNumber(number);
                tvContactName.setText(selectedContact.getContactName());
            } else if (id != null && !id.equals("")) {
                contactIdLong = id;
                selectedContact = LSContact.findById(LSContact.class, contactIdLong);
                tvContactName.setText(selectedContact.getContactName());
            }

        }

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etContactNote.getText().toString() != null) {
                    if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_NOTE)) {
                        selectedNote.setNoteText(etContactNote.getText().toString());
                        if (selectedNote.getSyncStatus().equals(SyncStatus.SYNC_STATUS_NOTE_ADDED_SYNCED) || selectedNote.getSyncStatus().equals(SyncStatus.SYNC_STATUS_NOTE_EDIT_SYNCED)) {
                            selectedNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_EDIT_NOT_SYNCED);
                        }
                        selectedNote.save();
                        finish();
                        Toast.makeText(AddEditNoteActivity.this, "Note Edited", Toast.LENGTH_SHORT).show();
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                        dataSenderAsync.run();

                    } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_NOTE)) {
                        LSNote note = new LSNote();
                        note.setContactOfNote(selectedContact);
                        note.setNoteText(etContactNote.getText().toString());
                        note.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_NOT_SYNCED);
                        note.save();
                        NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.post(mNoteAdded);
                        finish();
                        Toast.makeText(getApplicationContext(), "Note Added", Toast.LENGTH_SHORT).show();
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                        dataSenderAsync.run();
                    }
                    String projectToken = MixpanelConfig.projectToken;
                    MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                    mixpanel.track("Notes - Created");
                }
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                mixpanel.track("Notes - Activity Canceled");
            }
        });

    }
}
