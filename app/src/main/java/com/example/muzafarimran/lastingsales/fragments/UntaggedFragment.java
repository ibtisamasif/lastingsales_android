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
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UntaggedFragment extends TabFragment {

    private List<LSContact> untaggedContacts = new ArrayList<>();



    public static SalesFragment newInstance(int page, String title) {
        SalesFragment fragmentFirst = new SalesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }



    public void setList(List<LSContact> untaggedContacts) {
        this.untaggedContacts = untaggedContacts;
    }

    public UntaggedFragment() {
        // Required empty public constructor
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
        View view = null;
        ListView listView = null;

        view = inflater.inflate(R.layout.fragment_untagged, container, false);
        listView = (ListView) view.findViewById(R.id.untagged_contacts_list);

        ContactsAdapter contactsAdapter = new ContactsAdapter(getContext(), this.untaggedContacts);
        listView.setAdapter(contactsAdapter);

        return view;
    }

}
