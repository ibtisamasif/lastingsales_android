package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.autocompletetext.ContactsCompletionView;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ibtisam on 3/28/2018.
 */

public class AddDealActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<LSContact> {
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    private String TAG = "AddDealActivity";
    private EditText etContactName;
    private Button bSaveAddDeal;
    private Button bCancelAddDeal;

    //    private LinearLayout llDealType;
    String selectedDealType = LSDeal.DEAL_STATUS_CLOSED_WON;
    private LSContact selectedContact;
    private LSDeal mDeal;
    private Spinner isPrivateSpinner;
    private String dealStatus = LSDeal.DEAL_VISIBILITY_STATUS_PUBLIC;

    private ContactsCompletionView etLeadAddDeal;
    ArrayAdapter<LSContact> adapter;
    long contactIdLong = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = bundle.getString(TAG_LAUNCH_MODE_CONTACT_ID);
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
                selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            }
        }

        List<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
        adapter = new FilteredArrayAdapter<LSContact>(this, R.layout.person_layout, contacts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }
                LSContact lsContact = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(lsContact.getContactName());
                ((TextView) convertView.findViewById(R.id.number)).setText(lsContact.getPhoneOne());

                return convertView;
            }

            @Override
            protected boolean keepObject(LSContact lsContact, String mask) {
                mask = mask.toLowerCase();
                if (lsContact.getContactName() != null) {
                    if (lsContact.getContactName().toLowerCase().contains(mask) || lsContact.getPhoneOne().toLowerCase().contains(mask)) {
                        return true;
                    } else {
                        return false;
                    }
                } else return false;
            }
        };

        etLeadAddDeal = (ContactsCompletionView) findViewById(R.id.etLeadAddDeal);
        etLeadAddDeal.setAdapter(adapter);
        etLeadAddDeal.setTokenListener(this);
        etLeadAddDeal.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
//        etLeadAddDeal.performBestGuess(true);
        etLeadAddDeal.setThreshold(1);
        etLeadAddDeal.setTokenLimit(1);
        if (selectedContact != null) {
            if (selectedContact.getContactName() != null) {
                etLeadAddDeal.setText(selectedContact.getContactName());
                etLeadAddDeal.setEnabled(false);
            }
        }

        etContactName = (EditText) findViewById(R.id.etNameAddDeal);
        bSaveAddDeal = (Button) findViewById(R.id.bSaveAddDeal);
        bCancelAddDeal = (Button) findViewById(R.id.bCancelAddDeal);
        bCancelAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bSaveAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedContact != null) {
                    String dealName = etContactName.getText().toString();
                    String dealPhone = selectedContact.getPhoneOne();               //refactor
//                String dealPhone = etLeadAddDeal.getText().toString();
                    if (isValid(dealName, dealPhone)) {
                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(AddDealActivity.this, dealPhone);
                        LSContact tempContact = LSContact.getContactFromNumber(intlNum);
                        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                            mDeal = new LSDeal();
                            mDeal.setName(dealName);
                            mDeal.setContact(tempContact);
                            mDeal.setStatus(selectedDealType);
                            mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_NOT_SYNCED);
                            String defaultWorkflowServerId = LSWorkflow.getDefaultWorkflow().getServerId(); //TODO add null check
                            mDeal.setWorkflowId(defaultWorkflowServerId);
                            mDeal.setWorkflowStageId(LSStage.getStageByWorkflowServerIdAndPosition(defaultWorkflowServerId, "100").getServerId()); //TODO add null check
                            mDeal.setIsPrivate(dealStatus);
                            mDeal.setUpdatedAt(Calendar.getInstance().getTime());
                            mDeal.save();
                            finish();
                            moveToDealDetailScreenIfNeeded(mDeal);
                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(AddDealActivity.this);
                            dataSenderAsync.run();
                        } else {
                            Toast.makeText(AddDealActivity.this, "Corresponding Lead doesn't exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(AddDealActivity.this, "Please select a lead", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addItemsOnSpinnerDealIsPrivate();
    }

    @Override
    public void onTokenAdded(LSContact token) {
        selectedContact = token;
    }

    @Override
    public void onTokenRemoved(LSContact token) {
        selectedContact = null;
    }

    private void addItemsOnSpinnerDealIsPrivate() {
        isPrivateSpinner = (Spinner) findViewById(R.id.isPrivateSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Public");
        list.add("Private");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddDealActivity.this, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isPrivateSpinner.setAdapter(dataAdapter);
        isPrivateSpinner.post(new Runnable() {
            public void run() {
                isPrivateSpinner.setOnItemSelectedListener(new CustomSpinnerDealStatusOnItemSelectedListener());
            }
        });
    }

    private boolean isValid(String dealName, String dealLead) {
        etContactName.setError(null);
        etLeadAddDeal.setError(null);
        if (dealName.equals("") || dealName.length() < 3) {
            etContactName.setError("Invalid Name!");
            return false;
        }
        if (dealLead.equals("") || dealLead.length() < 1) {
            etLeadAddDeal.setError("Invalid Lead!");
            return false;
        }
        return true;
    }

    private class CustomSpinnerDealStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    dealStatus = LSDeal.DEAL_VISIBILITY_STATUS_PUBLIC;
                    Toast.makeText(parent.getContext(), "Status Changed to Public", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    dealStatus = LSDeal.DEAL_VISIBILITY_STATUS_PRIVATE;
                    Toast.makeText(parent.getContext(), "Status Changed to Private", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void moveToDealDetailScreenIfNeeded(LSDeal deal) {
        Intent detailsActivityIntent = new Intent(AddDealActivity.this, DealDetailsTabActivity.class);
        long dealId = deal.getId();
        detailsActivityIntent.putExtra(DealDetailsTabActivity.KEY_DEAL_ID, dealId + "");
        startActivity(detailsActivityIntent);
    }
}
