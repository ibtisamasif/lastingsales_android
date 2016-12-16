package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.Events.PersonalContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.NotesListByContactAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotesListByContactsFragment extends TabFragment {
    private static final String TAG = "NotesContactsFragment";
    ListView listView = null;
    NotesListByContactAdapter notesListByContactAdapter;
    EditText inputSearch;
    MaterialSearchView searchView;
    private TinyBus bus;

    public static NotesListByContactsFragment newInstance(int page, String title) {
        NotesListByContactsFragment fragmentFirst = new NotesListByContactsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (notesListByContactAdapter != null) {
            notesListByContactAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //Change
//        contactsAdapter = new NotesListByContactAdapter(getContext(), null, LSContact.CONTACT_TYPE_PERSONAL);
        notesListByContactAdapter = new NotesListByContactAdapter(getContext(), null);
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
    public void onPersonalContactAddedEventModel(PersonalContactAddedEventModel event) {
        Log.d(TAG, "onPersonalContactAddedEvent() called with: event = [" + event + "]");
//        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_PERSONAL);
        List<LSContact> contacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);
        setList(contacts);
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && notesListByContactAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            notesListByContactAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Change
//        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_PERSONAL);
        List<LSContact> contacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_by_contacts, container, false);
        listView = (ListView) view.findViewById(R.id.notes_contacts_list);
        listView.setAdapter(notesListByContactAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}