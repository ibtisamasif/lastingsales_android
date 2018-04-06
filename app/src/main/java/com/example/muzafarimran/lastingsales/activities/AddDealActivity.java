package com.example.muzafarimran.lastingsales.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 3/28/2018.
 */

public class AddDealActivity extends AppCompatActivity {
    private String TAG = "AddDealActivity";
    //    private Button bPendingRadio;
//    private Button bWonRadio;
//    private Button bLostRadio;
    private EditText etContactName;
    private AutoCompleteTextView etNumberAddDeal;
    private Button bSaveAddDeal;
    private Button bCancelAddDeal;

    //    private LinearLayout llDealType;
    String selectedDealType = LSDeal.DEAL_STATUS_CLOSED_WON;
    private LSContact selectedContact;
    private ArrayAdapter adapter;
    private LSDeal mDeal;
    private Spinner isPrivateSpinner;
    private String dealStatus = LSDeal.DEAL_VISIBILITY_STATUS_PUBLIC;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deal);
        List<LSContact> contacts = LSContact.getDateArrangedSalesContacts();


//        LSContactSpinAdapter dataAdapter = new LSContactSpinAdapter(this, android.R.layout.simple_dropdown_item_1line, contacts);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etNumberAddDeal = (AutoCompleteTextView) findViewById(R.id.etNumberAddDeal);
        etNumberAddDeal.setThreshold(1);//will start working from first character
//        etNumberAddDeal.setAdapter(dataAdapter);//setting the adapter data into the AutoCompleteTextView
        etNumberAddDeal.setTextColor(Color.RED);

        setupAutoComplete(etNumberAddDeal, contacts);

        etNumberAddDeal.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                // TODO Auto-generated method stub
                ArrayAdapter adapter = ((ArrayAdapter) etNumberAddDeal.getAdapter());
                selectedContact = (LSContact) adapter.getItem(position);
//                if (selectedContact != null) {
//                    Toast.makeText(AddDealActivity.this, "" + selectedContact.getPhoneOne(), Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onItemClick: selectedContact: " + selectedContact.getPhoneOne());
//                }
            }
        });

//        etNumberAddDeal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                LSContact selectedContact = (LSContact) arg0.getAdapter().getItem(arg2);
//                etNumberAddDeal.setText("" + selectedContact.getPhoneOne());
//                Toast.makeText(AddDealActivity.this, "Clicked " + arg2 + " name: " + selectedContact.getContactName(), Toast.LENGTH_SHORT).show();
//            }
//        });


        etContactName = (EditText) findViewById(R.id.etNameAddDeal);
//        llDealType = (LinearLayout) findViewById(R.id.llDealType);
        bSaveAddDeal = (Button) findViewById(R.id.bSaveAddDeal);
        bCancelAddDeal = (Button) findViewById(R.id.bCancelAddDeal);

/*        bPendingRadio = (Button) findViewById(R.id.bPendingRadio);
        bWonRadio = (Button) findViewById(R.id.bWonRadio);
        bLostRadio = (Button) findViewById(R.id.bLostRadio);

        bPendingRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddDealActivity.this, "Pending", Toast.LENGTH_SHORT).show();
                selectRadioButton(LSDeal.DEAL_STATUS_PENDING);
            }
        });
        bWonRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddDealActivity.this, "Won", Toast.LENGTH_SHORT).show();
                selectRadioButton(LSDeal.DEAL_STATUS_CLOSED_WON);
            }
        });
        bLostRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddDealActivity.this, "Lost", Toast.LENGTH_SHORT).show();
                selectRadioButton(LSDeal.DEAL_STATUS_CLOSED_LOST);
            }
        });*/
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
                    String dealPhone = selectedContact.getPhoneOne();
//                String dealPhone = etNumberAddDeal.getText().toString();

                    if (isValid(dealName, dealPhone)) {

                        String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(AddDealActivity.this, dealPhone);
                        LSContact tempContact = LSContact.getContactFromNumber(intlNum);
                        if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                            //TODO fill all data
                            mDeal = new LSDeal();
                            mDeal.setName(dealName);
                            mDeal.setContact(tempContact);
//                            mDeal.setLeadId(Long.toString(tempContact.getId()));
                            mDeal.setStatus(selectedDealType);
                            mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_ADD_NOT_SYNCED);
                            LSStage lsStage = LSStage.getFirstStage();
                            mDeal.setWorkflowId(LSWorkflow.getDefaultWorkflow().getServerId());
                            mDeal.setWorkflowStageId(lsStage.getServerId());
                            mDeal.setIsPrivate(dealStatus);
                            mDeal.save();
                            finish();
                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(AddDealActivity.this);
                            dataSenderAsync.run();
                        } else {
                            Toast.makeText(AddDealActivity.this, "Corresponding Lead doesn't exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(AddDealActivity.this, "Please select a lead", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        etNumberAddDeal.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        addItemsOnSpinnerDealIsPrivate();
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

    private boolean isValid(String dealName, String dealPhone) {
        etContactName.setError(null);
        etNumberAddDeal.setError(null);
        boolean validation = true;
        if (dealName.equals("") || dealName.length() < 3) {
            validation = false;
            etContactName.setError("Invalid Name!");
            return false;
        }
        if (dealPhone.equals("") || dealPhone.length() < 3) {
            validation = false;
            etNumberAddDeal.setError("Invalid Number!");
            return false;
        }
        return true;
    }

/*    private void selectRadioButton(String button) {
        if (button.equals(LSDeal.DEAL_STATUS_PENDING)) {

            selectedDealType = LSDeal.DEAL_STATUS_PENDING;

            bPendingRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            bWonRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            bLostRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            bPendingRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            bWonRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
            bLostRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));

            bPendingRadio.setTextColor(Color.WHITE);
            bWonRadio.setTextColor(Color.BLACK);
            bLostRadio.setTextColor(Color.BLACK);

        } else if (button.equals(LSDeal.DEAL_STATUS_CLOSED_WON)) {

            selectedDealType = LSDeal.DEAL_STATUS_CLOSED_WON;

            bWonRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            bPendingRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            bLostRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            bWonRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            bPendingRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
            bLostRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));

            bWonRadio.setTextColor(Color.WHITE);
            bPendingRadio.setTextColor(Color.BLACK);
            bLostRadio.setTextColor(Color.BLACK);

        } else if (button.equals(LSDeal.DEAL_STATUS_CLOSED_LOST)) {

            selectedDealType = LSDeal.DEAL_STATUS_CLOSED_LOST;

            bLostRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            bPendingRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            bWonRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            bLostRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
            bPendingRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
            bWonRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));

            bLostRadio.setTextColor(Color.WHITE);
            bPendingRadio.setTextColor(Color.BLACK);
            bWonRadio.setTextColor(Color.BLACK);
        }
    }*/

    private void setupAutoComplete(AutoCompleteTextView view, List<LSContact> objects) {
        List<LSContact> names = new AbstractList<LSContact>() {
            @Override
            public int size() {
                return objects.size();
            }

            @Override
            public LSContact get(int location) {
                return objects.get(location);
            }

        };
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, names);
        view.setAdapter(adapter);
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
}
