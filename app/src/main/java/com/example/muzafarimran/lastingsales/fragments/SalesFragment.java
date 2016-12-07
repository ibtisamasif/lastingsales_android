package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.Events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddContactActivity;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesFragment extends SearchFragment {

    private static final String TAG = "SalesContactFragment";
    ListView listView = null;
    ContactsAdapter contactsAdapter;
    FloatingActionButton addContactCta = null;
    ShowAddContactForm showaddcontactform = new ShowAddContactForm();
    private TinyBus bus;

    public static SalesFragment newInstance(int page, String title) {
        SalesFragment fragmentFirst = new SalesFragment();
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
        contactsAdapter = new ContactsAdapter(getContext(), null, LSContact.CONTACT_TYPE_SALES);
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
    public void onSalesContactAddedEventModel(SalesContactAddedEventModel event) {
        Log.d(TAG, "onSalesContactAddedEvent() called with: event = [" + event + "]");
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        setList(contacts);
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
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
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        this.addContactCta = (FloatingActionButton) view.findViewById(R.id.add_contact_cta);
        this.addContactCta.setOnClickListener(this.showaddcontactform);
        listView = (ListView) view.findViewById(R.id.sales_contacts_list);
        listView.setAdapter(contactsAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu_sales_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);
    }

    @Override
    protected void onSearch(String query) {
        contactsAdapter.getFilter().filter(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_funnel:
                View menuItemView = getActivity().findViewById(R.id.action_funnel);
                Toast.makeText(getActivity(), "Click on funnel Icon", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.filter_all:
                                Toast.makeText(getActivity(), "Filter All", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_prospects:
                                Toast.makeText(getActivity(), "Filter Prospects", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_leads:
                                Toast.makeText(getActivity(), "Filter Leads", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_closed_lost:
                                Toast.makeText(getActivity(), "Filter Lost", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_closed_won:
                                Toast.makeText(getActivity(), "Filter Won", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.sales_tab_filter_actions);
                popupMenu.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * event handler for click on add contact cta
    * */
    public class ShowAddContactForm implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(getActivity(), AddContactActivity.class);
            //myIntent.putExtra("number",(String) v.getTag());
            getActivity().startActivity(myIntent);
        }
    }
}