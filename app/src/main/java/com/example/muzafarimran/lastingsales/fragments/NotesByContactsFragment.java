package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddNoteActivity;
import com.example.muzafarimran.lastingsales.activities.NotesActivity;
import com.example.muzafarimran.lastingsales.activities.NotesByContactsActivity2;
import com.example.muzafarimran.lastingsales.adapters.NotesListAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/20/2016.
 */

public class NotesByContactsFragment extends TabFragment {

    public static final String TAG = "NotesContactsFragment";
    public static final String CONTACT_ID = "contact_id";
    ListView listView = null;
    NotesListAdapter2 notesListAdapter2;
    EditText inputSearch;
    MaterialSearchView searchView;
    View view;
    ImageView imageView;
    ListView lvNotesList;
    LSContact selectedContact;
    private TinyBus bus;
    private String number = "";
    FloatingActionButton floatingActionButton;

    public NotesByContactsFragment() {
    }

    public static NotesByContactsFragment newInstance(int page, String title , String number) {
        NotesByContactsFragment fragmentFirst = new NotesByContactsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putString("someNumber", number);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSNote> contacts) {
        if (notesListAdapter2 != null) {
            notesListAdapter2.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        notesListAdapter2 = new NotesListAdapter2(getActivity(), null);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<LSNote> allNotesOfThisContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId());
        setList(allNotesOfThisContact);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notes_by_contacts2, container, false);
        imageView = (ImageView) view.findViewById(R.id.ivbutton);
        lvNotesList = (ListView) view.findViewById(R.id.lvNoteListContactDetailsScreen);

        Bundle bundle = this.getArguments();
        number = bundle.getString("someNumber");
        if(number!=null) {
            selectedContact = LSContact.getContactFromNumber(number);
            ArrayList<LSNote> allNotesOfThisContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId());
            lvNotesList.setAdapter(new NotesListAdapter2(getActivity(), allNotesOfThisContact));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) view.findViewById(R.id.noteLine);
                    LSNote tempNote = new LSNote();
                    String note = editText.getText().toString();
                    if (note != null && note.length() > 0) {
                        tempNote.setNoteText(note);
                        tempNote.setContactOfNote(selectedContact);
                        tempNote.save();
                        editText.setText(null);
                        Toast.makeText(getActivity(), "Note Saved", Toast.LENGTH_SHORT).show();
                        lvNotesList.setAdapter(new NotesListAdapter2(getActivity(), (ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId())));
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(NotesByContactsActivity2.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            });

        }
        String contactIdString = this.getArguments().getString(CONTACT_ID);
        Long contactIDLong;
        if (contactIdString != null) {
            contactIDLong = Long.parseLong(contactIdString);
            selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            ArrayList<LSNote> allNotesOfThisContact = (ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId());
            lvNotesList.setAdapter(new NotesListAdapter2(getActivity(), allNotesOfThisContact));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText) view.findViewById(R.id.noteLine);
                    LSNote tempNote = new LSNote();
                    String note = editText.getText().toString();
                    if (note != null && note.length() > 0) {
                        tempNote.setNoteText(note);
                        tempNote.setContactOfNote(selectedContact);
                        tempNote.save();
                        editText.setText(null);
                        Toast.makeText(getActivity(), "Note Saved", Toast.LENGTH_SHORT).show();
                        lvNotesList.setAdapter(new NotesListAdapter2(getActivity(), (ArrayList<LSNote>) LSNote.getNotesByContactId(selectedContact.getId())));
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(NotesByContactsActivity2.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            });
        }


        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_add_note);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getContext(), AddNoteActivity.class), NotesActivity.ADD_NOTE_REQUEST_CODE);
            }
        });
        return view;
    }
}