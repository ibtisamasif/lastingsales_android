package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NonbusinessFragment extends TabFragment {

    private List<Contact> nonbusinessContacts = new ArrayList<>();

    public void setList(List<Contact> nonbusinessContacts){ this.nonbusinessContacts = nonbusinessContacts; }

    public NonbusinessFragment() {
        // Required empty public constructor
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

        ContactsAdapter contactsAdapter = new ContactsAdapter(getContext(), this.nonbusinessContacts);
        listView.setAdapter(contactsAdapter);

        return view;
    }

}
