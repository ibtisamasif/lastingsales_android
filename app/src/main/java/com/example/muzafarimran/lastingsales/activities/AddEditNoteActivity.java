package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Calendar;

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
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "launch_mode_contact_id";
    public static final String TAG_LAUNCH_MODE_DEAL_ID = "launch_mode_deal_id";
    public static final String TAG_LAUNCH_MODE_ORGANIZATION_ID = "launch_mode_organization_id";

    String launchMode = LAUNCH_MODE_ADD_NEW_NOTE;

    TextView etName;
    EditText etNote;
    Button bSave, bCancel;
    View view;
    LSNote selectedNote;
    LSContact selectedContact;
    LSDeal selectedDeal;
    LSOrganization selectedOrganization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
//        android.support.v7.app.ActionBar bar = getSupportActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        etName = (TextView) findViewById(R.id.etName);
        etNote = (EditText) findViewById(R.id.etNote);
        bSave = (Button) findViewById(R.id.bSave);
        bCancel = (Button) findViewById(R.id.bCancel);

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
            etNote.setText(selectedNote.getNoteText());
            if (selectedNote.getContactOfNote() != null) {
                selectedContact = selectedNote.getContactOfNote();
            }
            if (selectedNote.getDealOfNote() != null) {
                selectedDeal = selectedNote.getDealOfNote();
            }
            if (selectedNote.getOrganizationOfNote() != null) {
                selectedOrganization = selectedNote.getOrganizationOfNote();
            }
            etName.setText(selectedContact.getContactName());

        } else if (launchMode.equals(LAUNCH_MODE_ADD_NEW_NOTE)) {
            //for add new note
            if (bundle != null) {
                Long contact_id = bundle.getLong(TAG_LAUNCH_MODE_CONTACT_ID);
                Long deal_id = bundle.getLong(TAG_LAUNCH_MODE_DEAL_ID);
                Long organization_id = bundle.getLong(TAG_LAUNCH_MODE_ORGANIZATION_ID);
                if (contact_id != 0) {
                    selectedContact = LSContact.findById(LSContact.class, contact_id);
                    if (selectedContact != null) {
                        etName.setText(selectedContact.getContactName());
                    }
                } else if (deal_id != 0) {
                    selectedDeal = LSDeal.findById(LSDeal.class, deal_id);
                    if (selectedDeal != null) {
                        etName.setText(selectedDeal.getName());
                    }
                } else if (organization_id != 0) {
                    selectedOrganization = LSOrganization.findById(LSOrganization.class, organization_id);
                    if (selectedOrganization != null) {
                        etName.setText(selectedOrganization.getName());
                    }
                }
            }
        }

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNote.getText().toString() != null) {
                    if (launchMode.equals(LAUNCH_MODE_EDIT_EXISTING_NOTE)) {
                        selectedNote.setNoteText(etNote.getText().toString());
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
                        if (selectedContact != null) {
                            note.setContactOfNote(selectedContact);
                            note.setNotableType(LSNote.NOTEABLE_TYPE_APP_LEAD);
                        }
                        if (selectedDeal != null) {
                            note.setDealOfNote(selectedDeal);
                            note.setNotableType(LSNote.NOTEABLE_TYPE_APP_DEAL);
                        }
                        if (selectedOrganization != null) {
                            note.setOrganizationOfNote(selectedOrganization);
                            note.setNotableType(LSNote.NOTEABLE_TYPE_APP_ORGANIZATION);
                        }
                        note.setNoteText(etNote.getText().toString());
                        note.setSyncStatus(SyncStatus.SYNC_STATUS_NOTE_ADDED_NOT_SYNCED);
                        note.setCreatedAt(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(Calendar.getInstance().getTimeInMillis()));
                        note.save();
                        NoteAddedEventModel mNoteAdded = new NoteAddedEventModel();
                        TinyBus bus = TinyBus.from(getApplicationContext());
                        bus.post(mNoteAdded);
                        finish();
                        Toast.makeText(getApplicationContext(), "Note Added", Toast.LENGTH_SHORT).show();
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                        dataSenderAsync.run();
                        String projectToken = MixpanelConfig.projectToken;
                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                        mixpanel.track("Notes - Created");
                    }
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
