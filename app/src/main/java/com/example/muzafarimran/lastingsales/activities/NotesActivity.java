package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.NotesListFragmentOld;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;

@Deprecated
public class NotesActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST_CODE = 13;
    NotesListFragmentOld notesListFragmentOld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        if (savedInstanceState == null) {
            notesListFragmentOld = new NotesListFragmentOld();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_notes_activity, notesListFragmentOld);
            transaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_NOTE_REQUEST_CODE) {
            if (data == null) {
                finish();
            }
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                LSNote tempNote = LSNote.findById(LSNote.class, Long.parseLong(returnedResult));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}