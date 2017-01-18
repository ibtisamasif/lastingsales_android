package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.muzafarimran.lastingsales.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/9/2017.
 */

public class IndividualContactDetailsFragment extends TabFragment {

    public static final String TAG = "IndividualContactDetailsFragment";
    ListView listView = null;
    MaterialSearchView searchView;
    private TinyBus bus;
    private String number = "";
    private Spinner leadStatusSpinner, customTagSpinner1 , customTagSpinner2;

    public static IndividualContactDetailsFragment newInstance(int page, String title, String number) {
        IndividualContactDetailsFragment fragmentFirst = new IndividualContactDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putString("someNumber", number);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        number = bundle.getString("someNumber");
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


    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = this.getArguments();
        number = bundle.getString("someNumber");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_profile_details_fragment, container, false);
        addItemsOnSpinnerLeadStatus(view);
        addItemsOnSpinner1(view);
        addItemsOnSpinner2(view);
        addListenerOnSpinnerLeadStatusItemSelection(view);
        addListenerOnSpinner1ItemSelection(view);
        addListenerOnSpinner2ItemSelection(view);
        setHasOptionsMenu(true);
        return view;
    }

    public void addItemsOnSpinnerLeadStatus(View view) {

        leadStatusSpinner = (Spinner) view.findViewById(R.id.lead_status_spinner);
        List<String> list = new ArrayList<String>();
        list.add("Prospect");
        list.add("Leads");
        list.add("Closed");
        list.add("Won");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leadStatusSpinner.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinner1(View view) {

        customTagSpinner1 = (Spinner) view.findViewById(R.id.custom_tag_spinner1);
        List<String> list = new ArrayList<String>();
        list.add("Residential");
        list.add("Commercial");
        list.add("Industrial");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customTagSpinner1.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinner2(View view) {

        customTagSpinner2 = (Spinner) view.findViewById(R.id.custom_tag_spinner2);
        List<String> list = new ArrayList<String>();
        list.add("Honda City");
        list.add("Civic Reborn");
        list.add("Chevrolet Exclusive");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customTagSpinner2.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerLeadStatusItemSelection(View view){

        leadStatusSpinner = (Spinner) view.findViewById(R.id.lead_status_spinner);
        leadStatusSpinner.setOnItemSelectedListener(new CustomSpinnerLeadStatusOnItemSelectedListener());
    }

    public void addListenerOnSpinner1ItemSelection(View view){

        customTagSpinner1 = (Spinner) view.findViewById(R.id.custom_tag_spinner1);
        customTagSpinner1.setOnItemSelectedListener(new CustomSpinner1OnItemSelectedListener());
    }

    public void addListenerOnSpinner2ItemSelection(View view){

        customTagSpinner2 = (Spinner) view.findViewById(R.id.custom_tag_spinner2);
        customTagSpinner2.setOnItemSelectedListener(new CustomSpinner2OnItemSelectedListener());
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
    private class CustomSpinnerLeadStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            Toast.makeText(parent.getContext(), "Status : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }

    private class CustomSpinner1OnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            Toast.makeText(parent.getContext(), "Custom Tag1 : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }
    private class CustomSpinner2OnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            Toast.makeText(parent.getContext(), "Custom Tag2 : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    }
}
