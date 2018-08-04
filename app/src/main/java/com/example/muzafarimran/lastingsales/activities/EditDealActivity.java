package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;

import java.util.Calendar;

/**
 * Created by ibtisam on 3/28/2018.
 */

public class EditDealActivity extends AppCompatActivity {
    public static final String TAG = "EditDealActivity";
    public static final String TAG_LAUNCH_MODE_DEAL_ID = "deal_id";
    String selectedDealType = LSDeal.DEAL_STATUS_CLOSED_WON;
    private EditText etNameAddDeal;
    private EditText etLeadAddDeal;
    private EditText etValueAddDeal;
    private Button bSaveAddDeal;
    private Button bCancelAddDeal;
    private long dealIdLong;
    private LSDeal selectedDeal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deal);
        etNameAddDeal = (EditText) findViewById(R.id.etNameAddDeal);
        etLeadAddDeal = (EditText) findViewById(R.id.etLeadAddDeal);
        etValueAddDeal = (EditText) findViewById(R.id.etValueAddDeal);
        bSaveAddDeal = (Button) findViewById(R.id.bSaveAddDeal);
        bCancelAddDeal = (Button) findViewById(R.id.bCancelAddDeal);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            populateUpdateContactView(bundle);
        }
        bCancelAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bSaveAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dealName = etNameAddDeal.getText().toString();
                String dealValue = etValueAddDeal.getText().toString();
                if (isValid(dealName)) {
                    if (selectedDeal != null) {
                        LSDeal lsDeal = selectedDeal;
                        lsDeal.setName(dealName);
                        lsDeal.setStatus(selectedDealType);
                        lsDeal.setUpdatedAt(Calendar.getInstance().getTime());
                        if (dealValue != null && !dealValue.equalsIgnoreCase("")) {
                            lsDeal.setValue(dealValue);
                        }
                        if (lsDeal.getSyncStatus().equals(SyncStatus.SYNC_STATUS_DEAL_ADD_SYNCED) || lsDeal.getSyncStatus().equals(SyncStatus.SYNC_STATUS_DEAL_UPDATE_SYNCED)) {
                            lsDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                        }
                        lsDeal.save();
                        finish();
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(EditDealActivity.this);
                        dataSenderAsync.run();
                    } else {
                        Toast.makeText(EditDealActivity.this, "Deal doesn't exists", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });
    }

    private void populateUpdateContactView(Bundle bundle) {
        String id = bundle.getString(TAG_LAUNCH_MODE_DEAL_ID);
        if (id != null && !id.equals("")) {
            dealIdLong = Long.parseLong(id);
            selectedDeal = LSDeal.findById(LSDeal.class, dealIdLong);
        }
        if (selectedDeal != null) {
            if (selectedDeal.getName() != null) {
                etNameAddDeal.setText(selectedDeal.getName());
            } else {
                etNameAddDeal.setText("");
            }
            if (selectedDeal.getValue() != null) {
                etValueAddDeal.setText(selectedDeal.getValue());
            } else {
                etValueAddDeal.setText("");
            }
            if (selectedDeal.getContact() != null) {
                if (selectedDeal.getContact().getContactName() != null) {
                    if (!selectedDeal.getContact().getContactName().equalsIgnoreCase(null)) {
                        etLeadAddDeal.setText(selectedDeal.getContact().getContactName());
                    } else {
                        etLeadAddDeal.setText("");
                    }
                } else {
                    etLeadAddDeal.setText("");
                }
            } else {
                etLeadAddDeal.setText("");
            }
        }
    }

    private boolean isValid(String dealName) {
        etNameAddDeal.setError(null);
        etLeadAddDeal.setError(null);
        if (dealName.equals("") || dealName.length() < 3) {
            etNameAddDeal.setError("Invalid Name!");
            return false;
        }
        return true;
    }
}
