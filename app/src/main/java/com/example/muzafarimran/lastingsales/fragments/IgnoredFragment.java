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

import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.PersonalContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class IgnoredFragment extends TabFragment {
    private static final String TAG = "PersonalContactFragment";
    ListView listView = null;
    ContactsAdapter contactsAdapter;
    EditText inputSearch;
    MaterialSearchView searchView;
    private TinyBus bus;
    private ErrorScreenView errorScreenView;

    public static IgnoredFragment newInstance(int page, String title) {
        IgnoredFragment fragmentFirst = new IgnoredFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (contactsAdapter != null) {
            contactsAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        contactsAdapter = new ContactsAdapter(getContext(), null, LSContact.CONTACT_TYPE_IGNORED); // TODO remove this line as it populates all contacts have inprogress status including ignored,business
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
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
        setList(contacts);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && contactsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            contactsAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_IGNORED);
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        listView = (ListView) view.findViewById(R.id.personal_contacts_list);
        inputSearch = (EditText) (getActivity().findViewById(R.id.search_box));
        listView.setAdapter(contactsAdapter);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_home);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_ignored_delight));
        listView.setEmptyView(errorScreenView);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                contactsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsAdapter.getFilter().filter(newText);
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