package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.NotesListAdapter;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
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

public class NotesFragment extends TabFragment {
    public static final String TAG = "NotesFragment";
    public static final String CONTACT_ID = "contact_id";
    NotesListAdapter notesListAdapter;
    MaterialSearchView searchView;
    View view;
    ListView lvNotesList;
    LSContact selectedContact;
    private String number = "";
    private Long contactIDLong;
    private TinyBus bus;
    private ErrorScreenView errorScreenView;

    public NotesFragment() {
    }

    public static NotesFragment newInstance(int page, String title, Long id) {
        NotesFragment fragmentFirst = new NotesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSNote> notes) {
        Log.d(TAG, "setList: called");
        if (notesListAdapter != null) {
            notesListAdapter.setList(notes);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        notesListAdapter = new NotesListAdapter(getActivity(), null);
        setHasOptionsMenu(true);
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

    @Subscribe
    public void onNoteAddedEventModel(NoteAddedEventModel event) {
        Log.d(TAG, "onNoteAddedEvent() called with: event = [" + event + "]");
        onResumeFetchFreshData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        onResumeFetchFreshData();
    }

    private void onResumeFetchFreshData() {
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
            lvNotesList.setAdapter(new NotesListAdapter(getActivity(), allNotesOfThisContact));
        }
        String contactIdString = this.getArguments().getString(CONTACT_ID);
        Long contactIDLong;
        if (contactIdString != null) {
            contactIDLong = Long.parseLong(contactIdString);
            selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            lvNotesList.setAdapter(new NotesListAdapter(getActivity(), allNotesOfThisContact));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notes_by_contacts2, container, false);
        lvNotesList = (ListView) view.findViewById(R.id.lvNoteListContactDetailsScreen);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_won);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_notes_delight));
        lvNotesList.setEmptyView(errorScreenView);
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        selectedContact = LSContact.findById(LSContact.class, contactIDLong);
        number = selectedContact.getPhoneOne();
        if (number != null) {
            selectedContact = LSContact.getContactFromNumber(number);
            List<LSNote> allNotesOfThisContact = Select.from(LSNote.class).where(Condition.prop("contact_of_note").eq(selectedContact.getId())).orderBy("id DESC").list();
//            List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
            lvNotesList.setAdapter(new NotesListAdapter(getActivity(), allNotesOfThisContact));
        }
        String contactIdString = this.getArguments().getString(CONTACT_ID);
        Long contactIDLong;
        if (contactIdString != null) {
            contactIDLong = Long.parseLong(contactIdString);
            selectedContact = LSContact.findById(LSContact.class, contactIDLong);
            List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(selectedContact.getId());
            lvNotesList.setAdapter(new NotesListAdapter(getActivity(), allNotesOfThisContact));
        }
        return view;
    }
}