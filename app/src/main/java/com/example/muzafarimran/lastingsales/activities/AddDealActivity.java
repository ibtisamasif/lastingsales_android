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
import com.example.muzafarimran.lastingsales.adapters.LSStageSpinAdapter;
import com.example.muzafarimran.lastingsales.autocompletetext.ContactsCompletionView;
import com.example.muzafarimran.lastingsales.autocompletetext.OrganizationsCompletionView;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 3/28/2018.
 */

public class AddDealActivity extends AppCompatActivity {
    public static final String TAG_LAUNCH_MODE_CONTACT_ID = "contact_id";
    public static final String TAG_LAUNCH_MODE_ORGANIZATION_ID = "organization_id";
    private String TAG = "AddDealActivity";
    private EditText etDealName;
    private EditText etValueAddDeal;
    private Button bSaveAddDeal;
    private Button bCancelAddDeal;
    private Spinner stageSpinner;
    private Spinner isPrivateSpinner;

    //    private LinearLayout llDealType;
    String selectedDealType = LSDeal.DEAL_STATUS_CLOSED_WON;
    private LSContact selectedContact;
    private LSOrganization selectedOrganization;
    private LSDeal mDeal;
    List<LSStage> stageList = new ArrayList<LSStage>();
    private String dealStatus = LSDeal.DEAL_VISIBILITY_STATUS_COMPANY;

    private ContactsCompletionView acLeadAddDeal;
    private EditText etLeadAddDeal;
    ArrayAdapter<LSContact> adapterContacts;
    long contactIdLong = -1;
    ArrayAdapter<LSOrganization> adapterOrganizations;
    private OrganizationsCompletionView acOrganizationAddDeal;
    private EditText etOrganizationAddDeal;
    long organizationIdLong = -1;
    private String selectedStageServerId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);
        etLeadAddDeal = (EditText) findViewById(R.id.etLeadAddDeal);
        acLeadAddDeal = (ContactsCompletionView) findViewById(R.id.acLeadAddDeal);
        etOrganizationAddDeal = (EditText) findViewById(R.id.etOrganizationAddDeal);
        acOrganizationAddDeal = (OrganizationsCompletionView) findViewById(R.id.acOrganizationAddDeal);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String id = Long.toString(bundle.getLong(TAG_LAUNCH_MODE_CONTACT_ID));
            if (id != null && !id.equals("")) {
                contactIdLong = Long.parseLong(id);
                selectedContact = LSContact.findById(LSContact.class, contactIdLong);
            }
            String id2 = Long.toString(bundle.getLong(TAG_LAUNCH_MODE_ORGANIZATION_ID));
            if (id2 != null && !id2.equals("")) {
                organizationIdLong = Long.parseLong(id2);
                selectedOrganization = LSOrganization.findById(LSOrganization.class, organizationIdLong);
            }
        }
        if (selectedContact != null) {
            etLeadAddDeal.setVisibility(View.VISIBLE);
            acLeadAddDeal.setVisibility(View.GONE);
            if (selectedContact.getContactName() != null) {
                etLeadAddDeal.setText(selectedContact.getContactName());
            } else if (selectedContact.getPhoneOne() != null) {
                etLeadAddDeal.setText(selectedContact.getPhoneOne());
            } else {
                etLeadAddDeal.setText("");
            }
        } else {
            etLeadAddDeal.setVisibility(View.GONE);
            acLeadAddDeal.setVisibility(View.VISIBLE);
        }

        if (selectedOrganization != null) {
            etOrganizationAddDeal.setVisibility(View.VISIBLE);
            acOrganizationAddDeal.setVisibility(View.GONE);
            if (selectedOrganization.getName() != null) {
                etOrganizationAddDeal.setText(selectedOrganization.getName());
            } else if (selectedOrganization.getPhone() != null) {
                etOrganizationAddDeal.setText(selectedOrganization.getPhone());
            } else {
                etOrganizationAddDeal.setText("");
            }
        } else {
            etOrganizationAddDeal.setVisibility(View.GONE);
            acOrganizationAddDeal.setVisibility(View.VISIBLE);
        }

        List<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
        adapterContacts = new FilteredArrayAdapter<LSContact>(this, R.layout.person_layout, contacts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }
                LSContact lsContact = getItem(position);
                if (lsContact != null) {
                    if (lsContact.getContactName() != null) {
                        ((TextView) convertView.findViewById(R.id.name)).setText(lsContact.getContactName());
                    } else {
                        ((TextView) convertView.findViewById(R.id.name)).setText("");
                    }
                    if (lsContact.getPhoneOne() != null) {
                        ((TextView) convertView.findViewById(R.id.number)).setText(lsContact.getPhoneOne());
                    } else {
                        ((TextView) convertView.findViewById(R.id.number)).setText("");
                    }
                    return convertView;
                } else {
                    return null;
                }
            }

            @Override
            protected boolean keepObject(LSContact lsContact, String mask) {
                mask = mask.toLowerCase();
                if (lsContact.getContactName() != null) {
                    if (lsContact.getContactName().toLowerCase().contains(mask)) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (lsContact.getPhoneOne() != null) {
                    if (lsContact.getPhoneOne().toLowerCase().contains(mask)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        };
        acLeadAddDeal.setAdapter(adapterContacts);
        acLeadAddDeal.setTokenListener(new TokenCompleteTextView.TokenListener<LSContact>() {
            @Override
            public void onTokenAdded(LSContact token) {
                selectedContact = token;
            }

            @Override
            public void onTokenRemoved(LSContact token) {
                selectedContact = null;
            }
        });
        acLeadAddDeal.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
//        acLeadAddDeal.performBestGuess(true);
        acLeadAddDeal.setThreshold(1);
        acLeadAddDeal.setTokenLimit(1);
        if (selectedContact != null) {
            if (selectedContact.getContactName() != null) {
                acLeadAddDeal.setText(selectedContact.getContactName());
                acLeadAddDeal.setEnabled(false);
            }
        }

        List<LSOrganization> organizations = LSOrganization.listAll(LSOrganization.class);
        adapterOrganizations = new FilteredArrayAdapter<LSOrganization>(this, R.layout.person_layout, organizations) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(R.layout.person_layout, parent, false);
                }
                LSOrganization lsOrganization = getItem(position);
                ((TextView) convertView.findViewById(R.id.name)).setText(lsOrganization.getName());
                ((TextView) convertView.findViewById(R.id.number)).setText(lsOrganization.getPhone());
                return convertView;
            }

            @Override
            protected boolean keepObject(LSOrganization lsOrganization, String mask) {
                mask = mask.toLowerCase();
                if (lsOrganization.getName() != null) {
                    if (lsOrganization.getName().toLowerCase().contains(mask) || lsOrganization.getPhone().toLowerCase().contains(mask)) {
                        return true;
                    } else {
                        return false;
                    }
                } else return false;
            }
        };
        acOrganizationAddDeal.setAdapter(adapterOrganizations);
        acOrganizationAddDeal.setTokenListener(new TokenCompleteTextView.TokenListener<LSOrganization>() {
            @Override
            public void onTokenAdded(LSOrganization token) {
                selectedOrganization = token;
            }

            @Override
            public void onTokenRemoved(LSOrganization token) {
                selectedOrganization = null;
            }
        });
        acOrganizationAddDeal.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Select);
//        acOrganizationAddDeal.performBestGuess(true);
        acOrganizationAddDeal.setThreshold(1);
        acOrganizationAddDeal.setTokenLimit(1);
        if (selectedOrganization != null) {
            if (selectedOrganization.getName() != null) {
                acOrganizationAddDeal.setText(selectedOrganization.getName());
                acOrganizationAddDeal.setEnabled(false);
            }
        }

        etDealName = (EditText) findViewById(R.id.etNameAddDeal);
        if (selectedContact != null) {
            if (selectedContact.getContactName() != null) {
                etDealName.setText(selectedContact.getContactName() + " Deal");
            }
        }
        if (selectedOrganization != null) {
            if (selectedOrganization.getName() != null) {
                etDealName.setText(selectedOrganization.getName() + " Deal");
            }
        }

        etValueAddDeal = (EditText) findViewById(R.id.etValueAddDeal);

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
                if (selectedContact != null || selectedOrganization != null) {
                    String dealName = etDealName.getText().toString();
                    String dealValue = etValueAddDeal.getText().toString();
                    if (isValid(dealName)) {
                        mDeal = new LSDeal();
                        mDeal.setName(dealName);
                        if (selectedContact != null) {
                            mDeal.setContact(selectedContact);
                        }
                        if (selectedOrganization != null) {
                            mDeal.setOrganization(selectedOrganization);
                        }
                        mDeal.setStatus(selectedDealType);
                        mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_NOT_SYNCED);
                        String defaultWorkflowServerId = LSWorkflow.getDefaultWorkflow().getServerId(); //TODO add null check
                        mDeal.setWorkflowId(defaultWorkflowServerId);
                        mDeal.setWorkflowStageId(LSStage.getStageByWorkflowServerIdAndPosition(defaultWorkflowServerId, "100").getServerId()); //TODO add null check
                        mDeal.setIsPrivate(dealStatus);
                        mDeal.setUpdatedAt(Calendar.getInstance().getTime());
                        if (selectedStageServerId != null) {
                            mDeal.setWorkflowStageId(selectedStageServerId);
                        }
                        if (dealValue != null) {
                            mDeal.setValue(dealValue);
                        }
                        mDeal.save();
                        finish();
                        moveToDealDetailScreenIfNeeded(mDeal);
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(AddDealActivity.this);
                        dataSenderAsync.run();
                    }
                } else {
                    Toast.makeText(AddDealActivity.this, "Please select a contact or Organization", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        //populate stage spinner
//        LSStage lsStage = LSStage.getStageFromServerId(mDeal.getWorkflowStageId());
//        int index = 0;
//        for (int i = 0; i < stageList.size(); i++) {
//            if (lsStage != null && stageList.get(i).getName().equalsIgnoreCase(lsStage.getName())) {
//                index = i;
//            }
//        }
//        stageSpinner.setSelection(index, false);

        addItemsOnSpinnerDealStage();

        addItemsOnSpinnerDealIsPrivate();
    }

    public void addItemsOnSpinnerDealStage() {
        stageSpinner = (Spinner) findViewById(R.id.stage_spinner);
        LSWorkflow defaultWorkFlow = LSWorkflow.getDefaultWorkflow();
        Collection<LSStage> lsStages = LSStage.getAllStagesInPositionSequenceByWorkflowServerId(defaultWorkFlow.getServerId());
        if (lsStages != null) {
            stageList.addAll(lsStages);
        }
        LSStageSpinAdapter dataAdapter = new LSStageSpinAdapter(AddDealActivity.this, R.layout.spinner_item, stageList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stageSpinner.setAdapter(dataAdapter);
        stageSpinner.post(new Runnable() {
            public void run() {
                stageSpinner.setOnItemSelectedListener(new CustomSpinnerDealStageOnItemSelectedListener());
            }
        });
    }

    private void addItemsOnSpinnerDealIsPrivate() {
        isPrivateSpinner = (Spinner) findViewById(R.id.isPrivateSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Company");
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

    private boolean isValid(String dealName) {
        etDealName.setError(null);
        acLeadAddDeal.setError(null);
        if (dealName.equals("") || dealName.length() < 3) {
            etDealName.setError("Invalid Deal Name!");
            return false;
        }
        return true;
    }

    private class CustomSpinnerDealStageOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            LSStage selectedStage = (LSStage) parent.getItemAtPosition(pos);
//            LSStage lsStage = LSStage.getStageByName(selectedStepName);
            if (selectedStage != null) {
                selectedStageServerId = selectedStage.getServerId(); //mDeal is null
                Toast.makeText(parent.getContext(), "Stage Changed to " + selectedStage.getName(), Toast.LENGTH_SHORT).show();
//                TinyBus.from(parent.getContext().getApplicationContext()).post(new DealEventModel());
//                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(parent.getContext().getApplicationContext());
//                dataSenderAsync.run();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class CustomSpinnerDealStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    dealStatus = LSDeal.DEAL_VISIBILITY_STATUS_COMPANY;
                    Toast.makeText(parent.getContext(), "Status Changed to Company", Toast.LENGTH_SHORT).show();
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
