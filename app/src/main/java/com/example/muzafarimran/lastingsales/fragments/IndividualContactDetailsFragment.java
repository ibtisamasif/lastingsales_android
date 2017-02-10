package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
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
    private Spinner leadStatusSpinner, customTagSpinner1, customTagSpinner2;
    private Button bSave;
    private LSContact mContact;

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
        mContact = LSContact.getContactFromNumber(number);
        if (mContact.getContactType() != null) { //TODO crashed here
            if (mContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                if (mContact.getContactSalesStatus() != null && !mContact.getContactSalesStatus().equals("")) {
                    switch (mContact.getContactSalesStatus()) {
                        case LSContact.SALES_STATUS_INPROGRESS:
                            leadStatusSpinner.setSelection(0);
                            break;
                        case LSContact.SALES_STATUS_CLOSED_WON:
                            leadStatusSpinner.setSelection(1);
                            break;
                        case LSContact.SALES_STATUS_CLOSED_LOST:
                            leadStatusSpinner.setSelection(2);
                            break;
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        number = bundle.getString("someNumber");
        View view = inflater.inflate(R.layout.contact_profile_details_fragment, container, false);
        bSave = (Button) view.findViewById(R.id.contactDetailsSaveButton);
        bSave.setVisibility(View.GONE);
//        bSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mContact = LSContact.getContactFromNumber(number);
//                mContact.save();
//                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//            }
//        });
        addItemsOnSpinnerLeadStatus(view);
        addItemsOnSpinner1(view);
        addItemsOnSpinner2(view);
        addListenerOnSpinnerLeadStatusItemSelection(view);
        addListenerOnSpinner1ItemSelection(view);
        addListenerOnSpinner2ItemSelection(view);
        setHasOptionsMenu(true);
//        edEmailLabel.setSelected(false);
//        edEmailLabel.clearFocus();
//        edEmailLabel.setFocusableInTouchMode(false);
//        edEmailLabel.setFocusable(false);
//        edEmailLabel.setFocusableInTouchMode(true);
//        edEmailLabel.setFocusable(true);
//        // hide virtual keyboard
//        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(edEmailLabel.getWindowToken(), 0);
        return view;
    }

    public void addItemsOnSpinnerLeadStatus(View view) {
        leadStatusSpinner = (Spinner) view.findViewById(R.id.lead_status_spinner);
        List<String> list = new ArrayList<String>();
        list.add("InProgress");
        list.add("Close Won");
        list.add("Closed Lost");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leadStatusSpinner.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinner1(View view) {
        customTagSpinner1 = (Spinner) view.findViewById(R.id.custom_tag_spinner1);
        List<String> list = new ArrayList<String>();
        list.add("Residential");
        list.add("Commercial");
        list.add("Industrial");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customTagSpinner1.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinner2(View view) {
        customTagSpinner2 = (Spinner) view.findViewById(R.id.custom_tag_spinner2);
        List<String> list = new ArrayList<String>();
        list.add("Honda City");
        list.add("Civic Reborn");
        list.add("Chevrolet Exclusive");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customTagSpinner2.setAdapter(dataAdapter);
    }

    public void addListenerOnSpinnerLeadStatusItemSelection(View view) {
        leadStatusSpinner = (Spinner) view.findViewById(R.id.lead_status_spinner);
        leadStatusSpinner.setOnItemSelectedListener(new CustomSpinnerLeadStatusOnItemSelectedListener());
    }

    public void addListenerOnSpinner1ItemSelection(View view) {
        customTagSpinner1 = (Spinner) view.findViewById(R.id.custom_tag_spinner1);
        customTagSpinner1.setOnItemSelectedListener(new CustomSpinner1OnItemSelectedListener());
    }

    public void addListenerOnSpinner2ItemSelection(View view) {
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
            DataSenderAsync dataSenderNew = new DataSenderAsync(getActivity().getApplicationContext());
            switch (pos) {
                case 0:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to InProgress", Toast.LENGTH_SHORT).show();
                    dataSenderNew.execute();
                    break;
                case 1:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Won", Toast.LENGTH_SHORT).show();
                    dataSenderNew.execute();
                    break;
                case 2:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Lost", Toast.LENGTH_SHORT).show();
                    dataSenderNew.execute();
                    break;
            }
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
