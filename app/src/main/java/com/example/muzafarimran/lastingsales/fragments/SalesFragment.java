package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
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

    EditText inputSearch;

    ContactsAdapter contactsAdapter;
    public SalesFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        inputSearch = (EditText) (getActivity().findViewById(R.id.search_box));

        View view = null;
        ListView listView = null;

        view = inflater.inflate(R.layout.fragment_sales, container, false);
        listView = (ListView) view.findViewById(R.id.sales_contacts_list);

        contactsAdapter = new ContactsAdapter(getContext(), this.salesContacts);
        listView.setAdapter(contactsAdapter);

        this.inputSearch.addTextChangedListener(new addListenerOnTextChange());


        return view;
    }

    public class addListenerOnTextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Toast.makeText(getActivity(),s, Toast.LENGTH_LONG ).show();
            contactsAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }



}
