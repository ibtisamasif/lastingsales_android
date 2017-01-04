package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.Events.PersonalContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddNoteActivity;
import com.example.muzafarimran.lastingsales.adapters.ContactsListForNotesAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.github.clans.fab.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsListForNotesFragment extends TabFragment {
    public static final String TAG = "ContactsListForNotesFragment";
    private static final int CONTACT_REQUEST_CODE = 11;
    ListView listView = null;
    ContactsListForNotesAdapter contactsListForNotesAdapter;
    EditText inputSearch;
    MaterialSearchView searchView;
    FloatingActionButton floatingActionButtonAdd;
    private TinyBus bus;
    private FragmentManager supportFragmentManager;

    public static ContactsListForNotesFragment newInstance(int page, String title) {
        ContactsListForNotesFragment fragmentFirst = new ContactsListForNotesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (contactsListForNotesAdapter != null) {
            contactsListForNotesAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        contactsListForNotesAdapter = new ContactsListForNotesAdapter(getContext(), null);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onPersonalContactAddedEventModel(PersonalContactAddedEventModel event) {
        List<LSContact> contacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);
        setList(contacts);
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && contactsListForNotesAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            contactsListForNotesAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getAllContactsHavingNotes();
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_by_contacts, container, false);

        floatingActionButtonAdd = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_add);
        contactsListForNotesAdapter.setSupportFragmentManager(getFragmentManager());

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddNoteActivity.class);
                startActivity(intent);
            }
        });

        listView = (ListView) view.findViewById(R.id.notes_contacts_list);
        listView.setAdapter(contactsListForNotesAdapter);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactsListForNotesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsListForNotesAdapter.getFilter().filter(newText);
                return false;
            }
        });
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