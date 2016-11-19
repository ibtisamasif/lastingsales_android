package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.Events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends TabFragment {

    private static final String TAG = "SalesContactFragment";
    private List<LSContact> salesContacts = new ArrayList<>();
    private Bus mBus;
    private TinyBus bus;
    ListView listView = null;
    ContactsAdapter contactsAdapter;


    EditText inputSearch;



    public static SalesFragment newInstance(int page, String title) {
        SalesFragment fragmentFirst = new SalesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public SalesFragment() {

    }

    public void setList(List<LSContact> contacts) {
        if (contactsAdapter != null) {
            contactsAdapter.setList(contacts);
        }
    }
/*

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
//        mBus = TinyBus.from(getActivity().getApplicationContext());
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        contactsAdapter = new ContactsAdapter(getContext(), salesContacts);
        contactsAdapter.setList(salesContacts);
        if (listView != null) {
            listView.setAdapter(contactsAdapter);
        }
        setList(contacts);

    }
*/

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
    public void onSalesContactAddedEventModel(SalesContactAddedEventModel event) {
        Log.d(TAG, "onSalesContactAddedEvent() called with: event = [" + event + "]");
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        setList(contacts);

        TinyBus.from(getActivity().getApplicationContext()).unregister(event);

    }


    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        this.salesContacts = contacts;

        setList(contacts);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = null;
        inputSearch = (EditText) (getActivity().findViewById(R.id.search_box));


        view = inflater.inflate(R.layout.fragment_sales, container, false);
        listView = (ListView) view.findViewById(R.id.sales_contacts_list);

        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        this.salesContacts = contacts;
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
