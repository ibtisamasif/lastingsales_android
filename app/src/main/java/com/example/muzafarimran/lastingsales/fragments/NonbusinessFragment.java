package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.Events.PersonalContactAddedEventModel;
import com.example.muzafarimran.lastingsales.Events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class NonbusinessFragment extends TabFragment {
    private static final String TAG = "PersonalContactFragment";

    private List<LSContact> nonbusinessContacts = new ArrayList<>();
    private Bus mBus;
    private TinyBus bus;
    ListView listView = null;
    ContactsAdapter contactsAdapter;


    public static NonbusinessFragment newInstance(int page, String title) {
        NonbusinessFragment fragmentFirst = new NonbusinessFragment();
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
    public NonbusinessFragment() {
        // Required empty public constructor
    }
    @Override
    public void onStart() {
        super.onStart();
//        mBus.register(this);

        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
//        mBus.unregister(this);
        bus.unregister(this);
        Log.d(TAG, "onStop() called");

        super.onStop();

    }

    @Subscribe
    public void onPersonalContactAddedEventModel(PersonalContactAddedEventModel event) {
        Log.d(TAG, "onPersonalContactAddedEvent() called with: event = [" + event + "]");
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_PERSONAL);
        setList(contacts);

        TinyBus.from(getActivity().getApplicationContext()).unregister(event);

    }


    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_PERSONAL);
        this.nonbusinessContacts = contacts;

        setList(contacts);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = null;
        ListView listView = null;

        view = inflater.inflate(R.layout.fragment_personal, container, false);
        listView = (ListView) view.findViewById(R.id.personal_contacts_list);


        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_PERSONAL);
        this.nonbusinessContacts = contacts;
        contactsAdapter = new ContactsAdapter(getContext(), this.nonbusinessContacts);
        listView.setAdapter(contactsAdapter);

        return view;
    }

}
