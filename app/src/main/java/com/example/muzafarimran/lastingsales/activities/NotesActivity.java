package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.NotesListFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.Note;

public class NotesActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST_CODE = 13;
    NotesListFragment notesListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);


        if (savedInstanceState == null) {
            notesListFragment = new NotesListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container_notes_activity, notesListFragment);
//        transaction.addToBackStack(null);

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
