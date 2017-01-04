package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.Events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.adapters.SalesAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

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
    FloatingActionButton floatingActionButtonAdd, floatingActionButtonImport;
    FloatingActionMenu floatingActionMenu;
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

        System.out.println("pro "+countProspects());
        System.out.println("lead "+countLeads());
        System.out.println("Lost "+countClosedLost());
        System.out.println("Won "+countClosedWon());
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
//        this.addContactCta = (FloatingActionButton) view.findViewById(R.id.add_contact_cta);
//        this.addContactCta.setOnClickListener(this.showaddcontactform);
        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_add);
        floatingActionButtonImport = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_import);
        salesAdapter.setSupportFragmentManager(getFragmentManager());
        floatingActionMenu.setClosedOnTouchOutside(true);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_SALES);
                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_IMPORT_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_SALES);
                startActivity(intent);

            }
        });
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu_sales_fragment, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        if (materialSearchView != null) {
            materialSearchView.setMenuItem(item);
        }
    }

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
                            case R.id.filter_prospects:
                                List<LSContact> contactsProspects = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                                salesAdapter.setList(contactsProspects);
                                Toast.makeText(getActivity(), "Filter Prospects", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_leads:
                                List<LSContact> contactsLeads = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_LEAD);
                                salesAdapter.setList(contactsLeads);
                                Toast.makeText(getActivity(), "Filter Leads", Toast.LENGTH_SHORT).show();
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
        List<LSContact> contactsPros = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        List<LSContact> contactsLe = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_LEAD);
        List<LSContact> contactsLo = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
        List<LSContact> contactsWo = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        arrangedContacts.addAll(contactsPros);
        arrangedContacts.addAll(contactsLe);
        arrangedContacts.addAll(contactsLo);
        arrangedContacts.addAll(contactsWo);
        arrangedContacts.removeAll(contactsToBeRemoved);

        return arrangedContacts;
    }

    public int countProspects(){
        int count = 0;
        List<LSContact> arrangedContacts = getAllArrangedContactsAccordingToLeadType();
        if(arrangedContacts!=null){
            for (int i = 0; i < arrangedContacts.size() ; i++) {
                if(arrangedContacts.get(i).getContactSalesStatus().equals(LSContact.SALES_STATUS_PROSTPECT)){
                    count++;
                }
            }
            return count;
        }else{
            return 0;
        }
    }
    public  int countLeads(){
        int count = 0;
        List<LSContact> arrangedContacts = getAllArrangedContactsAccordingToLeadType();
        if(arrangedContacts!=null){
            for (int i = 0; i < arrangedContacts.size() ; i++) {
                if(arrangedContacts.get(i).getContactSalesStatus().equals(LSContact.SALES_STATUS_LEAD)){
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
            Intent myIntent = new Intent(getActivity(), TagNumberAndAddFollowupActivity.class);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
            getActivity().startActivity(myIntent);
        }
    }
}