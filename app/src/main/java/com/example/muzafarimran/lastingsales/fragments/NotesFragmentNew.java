package com.example.muzafarimran.lastingsales.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.NotesListAdapterNew;
import com.example.muzafarimran.lastingsales.events.NoteAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/20/2016.
 */

public class NotesFragmentNew extends TabFragment {
    public static final String TAG = "NotesFragmentNew";
    public static final String CONTACT_ID = "contact_id";
    ListView listView = null;
    NotesListAdapterNew notesListAdapterNew;
    MaterialSearchView searchView;
    View view;
    ListView lvNotesList;
    LSContact selectedContact;
    private String number = "";
    private Long contactIDLong;
    //    FloatingActionButton floatingActionButton;
    private TinyBus bus;

    public NotesFragmentNew() {
    }

    public static NotesFragmentNew newInstance(int page, String title, Long id) {
        NotesFragmentNew fragmentFirst = new NotesFragmentNew();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void setList(List<LSNote> contacts) {
        Log.d(TAG, "setList: called");
        if (notesListAdapterNew != null) {
            notesListAdapterNew.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        notesListAdapterNew = new NotesListAdapterNew(getActivity(), null);
        setHasOptionsMenu(true);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);

//        //            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Subscribe
    public void onNoteAddedEventModel(NoteAddedEventModel event) {
        Log.d(TAG, "onNoteAddedEvent() called with: event = [" + event + "]");
        List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
        setList(allNotesOfThisContact);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        List<LSNote> allNotesOfThisContact = Select.from(LSNote.class).where(Condition.prop("contact_of_note").eq(selectedContact.getId())).orderBy("id DESC").list();
//        List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
        setList(allNotesOfThisContact);
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        selectedContact = LSContact.findById(LSContact.class, contactIDLong);
        number = selectedContact.getPhoneOne();
//        number = bundle.getString("someNumber");
        if (number != null) {
            selectedContact = LSContact.getContactFromNumber(number);
            //List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
            lvNotesList.setAdapter(new NotesListAdapterNew(getActivity(), allNotesOfThisContact));
        }
        String contactIdString = this.getArguments().getString(CONTACT_ID);
        Long contactIDLong;
        if (contactIdString != null) {
            contactIDLong = Long.parseLong(contactIdString);
            selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            lvNotesList.setAdapter(new NotesListAdapterNew(getActivity(), allNotesOfThisContact));
        }
        hideKeyboard(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notes_by_contacts2, container, false);
        lvNotesList = (ListView) view.findViewById(R.id.lvNoteListContactDetailsScreen);
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        selectedContact = LSContact.findById(LSContact.class, contactIDLong);
        number = selectedContact.getPhoneOne();
        if (number != null) {
            selectedContact = LSContact.getContactFromNumber(number);
            List<LSNote> allNotesOfThisContact = Select.from(LSNote.class).where(Condition.prop("contact_of_note").eq(selectedContact.getId())).orderBy("id DESC").list();
//            List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
            lvNotesList.setAdapter(new NotesListAdapterNew(getActivity(), allNotesOfThisContact));
        }
        String contactIdString = this.getArguments().getString(CONTACT_ID);
        Long contactIDLong;
        if (contactIdString != null) {
            contactIDLong = Long.parseLong(contactIdString);
            selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
            lvNotesList.setAdapter(new NotesListAdapterNew(getActivity(), allNotesOfThisContact));
        }

//        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_add_note);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), AddEditNoteActivity.class);
//                intent.putExtra(AddEditNoteActivity.ACTIVITY_LAUNCH_MODE, AddEditNoteActivity.LAUNCH_MODE_ADD_NEW_NOTE);
//                intent.putExtra(AddEditNoteActivity.TAG_LAUNCH_MODE_CONTACT_NUMBER, number);
//                startActivity(intent);
////                startActivityForResult(new Intent(getContext(), AddNoteActivity.class), AddNoteActivity.ADD_NOTE_REQUEST_CODE);
//            }
//        });
        return view;
    }
}