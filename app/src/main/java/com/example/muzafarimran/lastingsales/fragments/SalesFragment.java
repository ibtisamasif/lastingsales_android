package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.Events.SalesContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.TagNumberAndAddFollowupActivity;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.github.clans.fab.FloatingActionButton;

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

    private static final String TAG = "SalesContactFragment";
    ExpandableStickyListHeadersListView listView = null;
    ContactsAdapter2 contactsAdapter2;
    ShowAddContactForm showaddcontactform = new ShowAddContactForm();
    private TinyBus bus;
    FloatingActionButton floatingActionButtonAdd, floatingActionButtonImport;

    public static SalesFragment newInstance(int page, String title) {
        SalesFragment fragmentFirst = new SalesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (contactsAdapter2 != null) {
            contactsAdapter2.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        contactsAdapter2 = new ContactsAdapter2(getContext(), null, LSContact.CONTACT_TYPE_SALES);
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
        if (!event.backPressHandled && contactsAdapter2.isDeleteFlow()) {
            event.backPressHandled = true;
            contactsAdapter2.setDeleteFlow(false);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
//        this.addContactCta = (FloatingActionButton) view.findViewById(R.id.add_contact_cta);
//        this.addContactCta.setOnClickListener(this.showaddcontactform);
        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_add);
        floatingActionButtonImport = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_import);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_IMPORT_CONTACT);
                startActivity(intent);
            }
        });
        listView = (ExpandableStickyListHeadersListView) view.findViewById(R.id.sales_contacts_list);
        listView.setAdapter(contactsAdapter2);
        //Expand and Contract Leads
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if(listView.isHeaderCollapsed(headerId)){
                    listView.expand(headerId);
                }else {
                    listView.collapse(headerId);
                }
            }
        });
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.search_options_menu_sales_fragment, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

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
        if (materialSearchView!=null){
        materialSearchView.setMenuItem(item);
        }
    }

    @Override
    protected void onSearch(String query) {
        contactsAdapter2.getFilter().filter(query);
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
                                contactsAdapter2.setList(getAllArrangedContactsAccordingToLeadType());
                                Toast.makeText(getActivity(), "Filter All", Toast.LENGTH_SHORT).show();

                                break;
                            case R.id.filter_prospects:
                                List<LSContact> contactsProspects = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
                                contactsAdapter2.setList(contactsProspects);
                                Toast.makeText(getActivity(), "Filter Prospects", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_leads:
                                List<LSContact> contactsLeads = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_LEAD);
                                contactsAdapter2.setList(contactsLeads);
                                Toast.makeText(getActivity(), "Filter Leads", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_closed_lost:
                                List<LSContact> contactsClosedLost = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
                                contactsAdapter2.setList(contactsClosedLost);
                                Toast.makeText(getActivity(), "Filter Lost", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_closed_won:
                                List<LSContact> contactsClosedWon = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
                                contactsAdapter2.setList(contactsClosedWon);
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


    public List<LSContact> getAllArrangedContactsAccordingToLeadType(){

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


    /*
    * event handler for click on add contact cta
    * */
    public class ShowAddContactForm implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(getActivity(), TagNumberAndAddFollowupActivity.class);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE , TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
            myIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_SALES);
            getActivity().startActivity(myIntent);
        }
    }
}