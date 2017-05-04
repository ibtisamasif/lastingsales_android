package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.adapters.SalesAdapter;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

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
    WeakHashMap<View,Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();
    ImageView imageView;
    SalesAdapter salesAdapter;
    ShowAddContactForm showaddcontactform = new ShowAddContactForm();
    private TinyBus bus;
    private ErrorScreenView errorScreenView;

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
    public void onSalesContactAddedEventModel(LeadContactAddedEventModel event) {
        Log.d(TAG, "onSalesContactAddedEvent() called with: event = [" + event + "]");
        List<LSContact> contacts = LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        setList(contacts);
//        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
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
//        imageView = (ImageView) view.findViewById(R.id.ivAllleads_contacts);
//        imageView.setImageResource(R.drawable.delight_all);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_all);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_all_delight));
        listView = (ExpandableStickyListHeadersListView) view.findViewById(R.id.sales_contacts_list);
        listView.setAnimExecutor(new AnimationExecutor());
        listView.setFastScrollEnabled(true);
        listView.setFastScrollAlwaysVisible(true);
        listView.setStickyHeaderTopOffset(-20);
        listView.setAdapter(salesAdapter);
        listView.setEmptyView(errorScreenView);
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

    public List<LSContact> getAllArrangedContactsAccordingToLeadType() { // TODO optimize this function. Crashed here too so must fix it.
        List<LSContact> arrangedContacts = new ArrayList<>();
        List<LSContact> contactsLe = LSContact.getSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        List<LSContact> contactsLo = LSContact.getSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
        List<LSContact> contactsWo = LSContact.getSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        arrangedContacts.addAll(contactsLe);
        arrangedContacts.addAll(contactsLo);
        arrangedContacts.addAll(contactsWo);

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
            Intent myIntent = new Intent(getActivity(), AddEditLeadActivity.class);
            myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
            getActivity().startActivity(myIntent);
        }
    }
    //animation executor StickeyHeader ListView
    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if(ExpandableStickyListHeadersListView.ANIMATION_EXPAND==animType&&target.getVisibility()==View.VISIBLE){
                return;
            }
            if(ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE==animType&&target.getVisibility()!=View.VISIBLE){
                return;
            }
            if(mOriginalViewHeightPool.get(target)==null){
                mOriginalViewHeightPool.put(target,target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            target.setVisibility(View.VISIBLE);

        }
    }
}