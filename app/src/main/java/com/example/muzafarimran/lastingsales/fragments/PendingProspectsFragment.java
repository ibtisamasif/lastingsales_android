package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.Events.ColleagueContactAddedEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.LeadsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/17/2016.
 */

public class PendingProspectsFragment extends TabFragment{

    public static final String TAG = "PendingProspectsFragment";
    ListView listView = null;
    LeadsAdapter leadsAdapter;
    MaterialSearchView searchView;
    private TinyBus bus;

    public static PendingProspectsFragment newInstance(int page, String title) {
        PendingProspectsFragment fragmentFirst = new PendingProspectsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (leadsAdapter != null) {
            leadsAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        leadsAdapter = new LeadsAdapter(getContext(), null, LSContact.SALES_STATUS_PROSTPECT);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onColleagueContactAddedEventModel(ColleagueContactAddedEventModel event) {
        List<LSContact> contacts = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        //Filtering out colleagues from list
        List<LSContact> allCollegues = (List<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        contacts.removeAll(allCollegues);
        setList(contacts);
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && leadsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            leadsAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        //Filtering out colleagues from list
        List<LSContact> allCollegues = (List<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        contacts.removeAll(allCollegues);
        setList(contacts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leads, container, false);
        listView = (ListView) view.findViewById(R.id.leads_contacts_list);
        listView.setAdapter(leadsAdapter);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                leadsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                leadsAdapter.getFilter().filter(newText);
                return false;
            }
        });
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
