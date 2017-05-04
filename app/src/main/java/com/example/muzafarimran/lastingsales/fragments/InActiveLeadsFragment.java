package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.InActiveLeadsAdapter;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.BackPressedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/17/2016.
 */

public class InActiveLeadsFragment extends  TabFragment{

    public static final String TAG = "InActiveLeadsFragment";
    ListView listView = null;
//    ImageView imageView;
    InActiveLeadsAdapter inActiveLeadsAdapter;
//    MaterialSearchView searchView;
    private TinyBus bus;
    private ErrorScreenView errorScreenView;

    public static InActiveLeadsFragment newInstance(int page, String title) {
        InActiveLeadsFragment fragmentFirst = new InActiveLeadsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSContact> contacts) {
        if (inActiveLeadsAdapter != null) {
            inActiveLeadsAdapter.setList(contacts);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        inActiveLeadsAdapter = new InActiveLeadsAdapter(getContext(), LSContact.getAllInactiveLeadContacts());
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSContact> contacts = LSContact.getAllInactiveLeadContacts();
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
    public void onSaleContactAddedEventModel(LeadContactAddedEventModel event) {
        List<LSContact> contacts = LSContact.getAllInactiveLeadContacts();
        setList(contacts);
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(LeadContactDeletedEventModel event) {
        List<LSContact> contacts = LSContact.getAllInactiveLeadContacts();
        setList(contacts);
    }

    @Subscribe
    public void onBackPressedEventModel(BackPressedEventModel event) {
        if (!event.backPressHandled && inActiveLeadsAdapter.isDeleteFlow()) {
            event.backPressHandled = true;
            inActiveLeadsAdapter.setDeleteFlow(false);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leads, container, false);
        listView = (ListView) view.findViewById(R.id.leads_contacts_list);
//        imageView = (ImageView) view.findViewById(R.id.ivleads_contacts);
//        imageView.setImageResource(R.drawable.delight_inactive);
        listView.setAdapter(inActiveLeadsAdapter);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_inactive);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_inactive_delight));
        listView.setEmptyView(errorScreenView);
//        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
//        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                inActiveLeadsAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                inActiveLeadsAdapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//        setHasOptionsMenu(true);
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
