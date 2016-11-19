package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.muzafarimran.lastingsales.PopUpWindowAddNewFollowUp;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.AddNoteFragment;
import com.example.muzafarimran.lastingsales.fragments.NotesListFragment;
import com.example.muzafarimran.lastingsales.providers.models.Contact;

import static java.security.AccessController.getContext;

public class AddNoteActivity extends AppCompatActivity {
    AddNoteFragment addNoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);


        if (savedInstanceState == null) {
            addNoteFragment = new AddNoteFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container_add_note_activity, addNoteFragment);
//        transaction.addToBackStack(null);

            transaction.commit();

        }

        startActivityForResult(new Intent(getApplicationContext(), LSContactChooserActivity.class), AddNoteFragment.CONTACT_REQUEST_CODE);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddNoteFragment.CONTACT_REQUEST_CODE) {
            if (data == null) {
                finish();
            }
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                addNoteFragment.setContact(returnedResult);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}