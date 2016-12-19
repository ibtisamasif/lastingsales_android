package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.Events.ColleagueContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.PendingProspectsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/17/2016.
 */

public class InActiveLeadsFragment extends  TabFragment{

    private static final String TAG = "PendingProspectsFragment";
    ListView listView = null;
    PendingProspectsAdapter pendingProspectsAdapter;
    private TinyBus bus;

    public static CollegueFragment newInstance(int page, String title) {
        CollegueFragment fragmentFirst = new CollegueFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (pendingProspectsAdapter != null) {
            pendingProspectsAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        pendingProspectsAdapter = new PendingProspectsAdapter(getContext(), null, LSContact.SALES_STATUS_PROSTPECT);
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
    public void onColleagueContactAddedEventModel(ColleagueContactAddedEventModel event) {
        Log.d(TAG, "onColleagueContactEvent() called with: event = [" + event + "]");
        List<LSContact> contacts = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        setList(contacts);
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && pendingProspectsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            pendingProspectsAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_prospects, container, false);
        listView = (ListView) view.findViewById(R.id.pending_prospects_contacts_list);
        listView.setAdapter(pendingProspectsAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
