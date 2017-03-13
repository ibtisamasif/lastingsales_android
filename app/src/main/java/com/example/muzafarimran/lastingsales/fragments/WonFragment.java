package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.LeadsAdapter;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.ColleagueContactAddedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class WonFragment extends TabFragment{
    public static final String TAG = "WonFragment";
    ListView listView = null;
//    ImageView imageView;
    LeadsAdapter leadsAdapter;
    MaterialSearchView searchView;
    private TinyBus bus;

    ErrorScreenView errorScreenView;

    public static WonFragment newInstance(int page, String title) {
        WonFragment fragmentFirst = new WonFragment();
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
        leadsAdapter = new LeadsAdapter(getContext(), null, LSContact.SALES_STATUS_CLOSED_WON);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        //Filtering out colleagues from list
        List<LSContact> allCollegues = (List<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        contacts.removeAll(allCollegues);
        setList(contacts);
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void onColleagueContactAddedEventModel(ColleagueContactAddedEventModel event) {
        List<LSContact> contacts = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        //Filtering out colleagues from list
        List<LSContact> allCollegues = (List<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        contacts.removeAll(allCollegues);
        setList(contacts);
//        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(LeadContactDeletedEventModel event) {
        List<LSContact> contacts = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        //Filtering out colleagues from list
        List<LSContact> allCollegues = (List<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        contacts.removeAll(allCollegues);
        setList(contacts);
//        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && leadsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            leadsAdapter.setDeleteFlow(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        errorScreenView = new ErrorScreenView(this.getContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leads, container, false);
        listView = (ListView) view.findViewById(R.id.leads_contacts_list);
//        imageView = (ImageView) view.findViewById(R.id.ivleads_contacts);
//        imageView.setImageResource(R.drawable.delight_won);
        listView.setAdapter(leadsAdapter);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_won);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_won_delight));
        listView.setEmptyView(errorScreenView);

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
