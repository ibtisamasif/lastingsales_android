package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.AddNoteFragment;


public class AddNoteActivity extends AppCompatActivity {
    private static final String TAG = "AddNoteActivity";
    public static final String ADD_NOTE_CONTACT_NUMBER = "add_note_contact_number";
    public static final int ADD_NOTE_REQUEST_CODE = 14;
    AddNoteFragment addNoteFragment;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            number = bundle.getString(ADD_NOTE_CONTACT_NUMBER);
            Log.d(TAG, "onCreate: "+number);

            addNoteFragment = new AddNoteFragment();
            addNoteFragment.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_add_note_activity, addNoteFragment);
//        transaction.addToBackStack(null);
            transaction.commit();
        }

//        addNoteFragment = new AddNoteFragment();
//        addNoteFragment.setArguments(bundle);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.fragment_container_add_note_activity, addNoteFragment);
////        transaction.addToBackStack(null);
//        transaction.commit();

//        if (savedInstanceState == null) {
//            addNoteFragment = new AddNoteFragment();
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.fragment_container_add_note_activity, addNoteFragment);
////        transaction.addToBackStack(null);
//            transaction.commit();
//        }
//        Intent intent = new Intent(getApplicationContext(), LSContactChooserActivity.class);
//        intent.putExtra(LSContactChooserActivity.CONTACT_TYPE_TO_DISPLAY, LSContactChooserActivity.LAUNCH_MODE_CONTACTS_COLLEGUES_AND_SALES);
//        startActivityForResult(intent, AddNoteFragment.CONTACT_REQUEST_CODE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeyboard(this);
    }
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == AddNoteFragment.CONTACT_REQUEST_CODE) {
//            if (data == null) {
//                finish();
//            }
//            if (resultCode == RESULT_OK) {
//                String returnedResult = data.getData().toString();
//                addNoteFragment.setContact(returnedResult);
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}