package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddLeadActivity;
import com.example.muzafarimran.lastingsales.adapters.SalesAdapter;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * A simple {@link Fragment} subclass.
 */

public class SalesFragment extends SearchFragment {

    public static final String TAG = "SalesContactFragment";
    ExpandableStickyListHeadersListView listView = null;
    SalesAdapter salesAdapter;
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
        if (salesAdapter != null) {
            salesAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        salesAdapter = new SalesAdapter(getContext(), null, LSContact.CONTACT_TYPE_SALES);
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
        if (!event.backPressHandled && salesAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            salesAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
//        setList(contacts);
        setList(getAllArrangedContactsAccordingToLeadType());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        listView = (ExpandableStickyListHeadersListView) view.findViewById(R.id.sales_contacts_list);
        listView.setAdapter(salesAdapter);
        //Expand and Contract Leads
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (listView.isHeaderCollapsed(headerId)) {
                    listView.expand(headerId);
                } else {
                    listView.collapse(headerId);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.search_options_menu_sales_fragment, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        if (materialSearchView != null) {
//            materialSearchView.setMenuItem(item);
//        }
//    }

    @Override
    protected void onSearch(String query) {
        salesAdapter.getFilter().filter(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_funnel:
                View menuItemView = getActivity().findViewById(R.id.action_funnel);
//                Toast.makeText(getActivity(), "Click on funnel Icon", Toast.LENGTH_SHORT).show();
                PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.filter_all:
//                                List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
//                                setList(contacts);
                                salesAdapter.setList(getAllArrangedContactsAccordingToLeadType());
                                Toast.makeText(getActivity(), "Filter All", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_leads:
                                List<LSContact> contactsLeads = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                                salesAdapter.setList(contactsLeads);
                                Toast.makeText(getActivity(), "Filter InProgress", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_closed_lost:
                                List<LSContact> contactsClosedLost = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
                                salesAdapter.setList(contactsClosedLost);
                                Toast.makeText(getActivity(), "Filter Lost", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_closed_won:
                                List<LSContact> contactsClosedWon = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
                                salesAdapter.setList(contactsClosedWon);
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

    public List<LSContact> getAllArrangedContactsAccordingToLeadType() {
        List<LSContact> contactsColle = LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        List<LSContact> contactsPerso = LSContact.getContactsByType(LSContact.CONTACT_TYPE_PERSONAL);
        List<LSContact> contactsToBeRemoved = new ArrayList<>();
        contactsToBeRemoved.addAll(contactsColle);
        contactsToBeRemoved.addAll(contactsPerso);
        List<LSContact> arrangedContacts = new ArrayList<>();
        List<LSContact> contactsLe = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        List<LSContact> contactsLo = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
        List<LSContact> contactsWo = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        arrangedContacts.addAll(contactsLe);
        arrangedContacts.addAll(contactsLo);
        arrangedContacts.addAll(contactsWo);
        arrangedContacts.removeAll(contactsToBeRemoved);

        return arrangedContacts;
    }

    public  int countLeads(){
        int count = 0;
        List<LSContact> arrangedContacts = getAllArrangedContactsAccordingToLeadType();
        if(arrangedContacts!=null){
            for (int i = 0; i < arrangedContacts.size() ; i++) {
                if(arrangedContacts.get(i).getContactSalesStatus().equals(LSContact.SALES_STATUS_INPROGRESS)){
                    count++;
                }
            }
            return count;
        }else{
            return 0;
        }
    }
    public int countClosedLost(){
        int count = 0;
        List<LSContact> arrangedContacts = getAllArrangedContactsAccordingToLeadType();
        if(arrangedContacts!=null){
            for (int i = 0; i < arrangedContacts.size() ; i++) {
                if(arrangedContacts.get(i).getContactSalesStatus().equals(LSContact.SALES_STATUS_CLOSED_LOST)){
                    count++;
                }
            }
            return count;
        }else{
            return 0;
        }
    }
    public int countClosedWon(){
        int count = 0;
        List<LSContact> arrangedContacts = getAllArrangedContactsAccordingToLeadType();
        if(arrangedContacts!=null){
            for (int i = 0; i < arrangedContacts.size() ; i++) {
                if(arrangedContacts.get(i).getContactSalesStatus().equals(LSContact.SALES_STATUS_CLOSED_WON)){
                    count++;
                }
            }
            return count;
        }else{
            return 0;
        }
    }
    /*
    * event handler for click on add contact cta
    * */
    public class ShowAddContactForm implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(getActivity(), AddLeadActivity.class);
            myIntent.putExtra(AddLeadActivity.ACTIVITY_LAUNCH_MODE, AddLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
            getActivity().startActivity(myIntent);
        }
    }
}