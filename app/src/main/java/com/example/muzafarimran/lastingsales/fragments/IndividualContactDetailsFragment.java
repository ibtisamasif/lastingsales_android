package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/9/2017.
 */

public class IndividualContactDetailsFragment extends TabFragment {

    public static final String TAG = "IndividualConDetailFrag";
    TextView tvName;
    TextView tvNumber;
    TextView tvEmail;
    TextView tvAddress;
    ListView listView = null;
    MaterialSearchView searchView;
    private Long contactIDLong;
    private Spinner leadStatusSpinner;
    private Button bSave;
    private LSContact mContact;
    private TinyBus bus;

    public static IndividualContactDetailsFragment newInstance(int page, String title, Long id) {
        IndividualContactDetailsFragment fragmentFirst = new IndividualContactDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
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
        contactIDLong = bundle.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
        if (mContact != null && mContact.getContactName() != null) {
            tvName.setText(mContact.getContactName());
        }
        if (mContact != null && mContact.getPhoneOne() != null) {
            tvNumber.setText(mContact.getPhoneOne());
        }
        if (mContact != null && mContact.getContactEmail() != null) {
            tvEmail.setText(mContact.getContactEmail());
        }
        if (mContact != null && mContact.getContactAddress() != null) {
            tvAddress.setText(mContact.getContactAddress());
        }
        if (mContact != null && mContact.getContactType() != null) {
            if (mContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                if (mContact.getContactSalesStatus() != null && !mContact.getContactSalesStatus().equals("")) {
                    switch (mContact.getContactSalesStatus()) {
                        case LSContact.SALES_STATUS_INPROGRESS:
                            leadStatusSpinner.setSelection(0, false);
                            break;
                        case LSContact.SALES_STATUS_CLOSED_WON:
                            leadStatusSpinner.setSelection(1, false);
                            break;
                        case LSContact.SALES_STATUS_CLOSED_LOST:
                            leadStatusSpinner.setSelection(2, false);
                            break;
                    }
                }
            }
        }
        leadStatusSpinner.post(new Runnable() {
            public void run() {
                leadStatusSpinner.setOnItemSelectedListener(new CustomSpinnerLeadStatusOnItemSelectedListener());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_profile_details_fragment, container, false);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvNumber = (TextView) view.findViewById(R.id.tvNumber);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
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
        setHasOptionsMenu(true);
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

    @Subscribe
    public void onSalesContactAddedEventModel(LeadContactAddedEventModel event) {
        Log.d(TAG, "onSalesContactAddedEventModel: CalledInFrag");

    }
}
