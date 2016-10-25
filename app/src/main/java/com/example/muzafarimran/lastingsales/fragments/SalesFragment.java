package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.Contact;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends TabFragment {

    private List<Contact> salesContacts = new ArrayList<>();

    public void setList(List<Contact> salesContacts){
        this.salesContacts = salesContacts;
    }


    public SalesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = null;
        ListView listView = null;

        view = inflater.inflate(R.layout.fragment_sales, container, false);
        listView = (ListView) view.findViewById(R.id.sales_contacts_list);

        ContactsAdapter contactsAdapter = new ContactsAdapter(getContext(), this.salesContacts);
        listView.setAdapter(contactsAdapter);


        return view;
    }

}
