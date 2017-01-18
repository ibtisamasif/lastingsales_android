package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

/**
 * Created by ibtisam on 1/12/2017.
 */

public class EditNoteActivity extends AppCompatActivity {
    public static final String TAG = "AddNoteFragment";
    TextView tvContactName;
    EditText etContactNote;
    Button bOk, bCancel;
    View view;
    LSNote selectedNote;
    LSContact selectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_note);
        tvContactName = (TextView) findViewById(R.id.contact_name_add_note);
        etContactNote = (EditText) findViewById(R.id.contact_note_add_note);
        bOk = (Button) findViewById(R.id.ok_add_note);
        bCancel = (Button) findViewById(R.id.cancel_add_note);

        Bundle extras = getIntent().getExtras();
        Long noteIdLong = 0l;
        if (extras != null) {
            noteIdLong = Long.parseLong(extras.getString("noteid"));
            selectedNote = LSNote.findById(LSNote.class, noteIdLong);
            etContactNote.setText(selectedNote.getNoteText());
            selectedContact = selectedNote.getContactOfNote();
            tvContactName.setText(selectedContact.getContactName());
        }

        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedContact != null) {
                    selectedNote.setNoteText(etContactNote.getText().toString());
                    selectedNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOT_SYNCED);
                    selectedNote.save();
                    Toast.makeText(EditNoteActivity.this, "Note Edited", Toast.LENGTH_SHORT).show();
                    Toast.makeText(EditNoteActivity.this, "SyncStatus :"+selectedNote.getSyncStatus(), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    LSNote note = new LSNote();
                    note.setContactOfNote(selectedContact);
                    note.setNoteText(etContactNote.getText().toString());
                    selectedNote.setSyncStatus(SyncStatus.SYNC_STATUS_NOT_SYNCED);
                    Toast.makeText(EditNoteActivity.this, "Note Added", Toast.LENGTH_SHORT).show();
                    Toast.makeText(EditNoteActivity.this, "SyncStatus :"+selectedNote.getSyncStatus(), Toast.LENGTH_SHORT).show();
                    note.save();

                }
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
