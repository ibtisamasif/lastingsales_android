package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllCallsFragment extends TabFragment {

    public static final String TAG = "AllCallFragment";
    CallsAdapter callsadapter;
    MaterialSearchView searchView;
    ListView listView = null;
    private Bus mBus;
    private TinyBus bus;

    public static AllCallsFragment newInstance(int page, String title) {
        AllCallsFragment fragmentFirst = new AllCallsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSCall> AllCalls) {
        if (callsadapter != null) {
            callsadapter.setList(AllCalls);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        callsadapter = new CallsAdapter(getContext());
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
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onAnyOutGoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
            setList(allCalls);
        }
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onAnyMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
            setList(allCalls);
        }
    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onAnyIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {
            List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
            setList(allCalls);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
        setList(allCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_incoming_calls, container, false);
        listView = (ListView) view.findViewById(R.id.incoming_calls_list);
        listView.setAdapter(callsadapter);

        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callsadapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callsadapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callsadapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callsadapter.getFilter().filter(newText);
                return false;
            }
        });

        setHasOptionsMenu(true);
        return view;
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
//
//    @Override
//    protected void onSearch(String query) {
//        callsadapter.getFilter().filter(query);
//    }
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
//                                callsadapter.setList(getAllArrangedContactsAccordingToLeadType());
                                Toast.makeText(getActivity(), "All", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_prospects:
//                                List<LSContact> contactsProspects = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
//                                callsadapter.setList(contactsProspects);
                                Toast.makeText(getActivity(), "Missed", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_leads:
//                                List<LSContact> contactsLeads = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_LEAD);
//                                callsadapter.setList(contactsLeads);
                                Toast.makeText(getActivity(), "Incoming", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.filter_closed_lost:
//                                List<LSContact> contactsClosedLost = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
//                                callsadapter.setList(contactsClosedLost);
                                Toast.makeText(getActivity(), "Outgoing", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.sales_tab_filter_actions);
                popupMenu.show();
                break;

            case android.R.id.home:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}