package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

/**
 * Created by ibtisam on 3/28/2018.
 */

public class EditDealActivity extends AppCompatActivity {
    public static final String TAG = "EditDealActivity";
    public static final String TAG_LAUNCH_MODE_DEAL_ID = "deal_id";

    private EditText etNameAddDeal;
    private EditText etNumberAddDeal;
    private Button bSaveAddDeal;
    private Button bCancelAddDeal;
//    private Button bPendingRadio;
//    private Button bWonRadio;
//    private Button bLostRadio;
//    private LinearLayout llDealType;

    String selectedDealType = LSDeal.DEAL_STATUS_CLOSED_WON;
    private long dealIdLong;
    private LSDeal selectedDeal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deal);

        etNameAddDeal = (EditText) findViewById(R.id.etNameAddDeal);
        etNumberAddDeal = (EditText) findViewById(R.id.etNumberAddDeal);
//        llDealType = (LinearLayout) findViewById(R.id.llDealType);
        bSaveAddDeal = (Button) findViewById(R.id.bSaveAddDeal);
        bCancelAddDeal = (Button) findViewById(R.id.bCancelAddDeal);
//        bPendingRadio = (Button) findViewById(R.id.bPendingRadio);
//        bWonRadio = (Button) findViewById(R.id.bWonRadio);
//        bLostRadio = (Button) findViewById(R.id.bLostRadio);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            populateUpdateContactView(bundle);
        }


//        bPendingRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(EditDealActivity.this, "Pending", Toast.LENGTH_SHORT).show();
//                selectRadioButton(LSDeal.DEAL_STATUS_PENDING);
//            }
//        });
//        bWonRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(EditDealActivity.this, "Won", Toast.LENGTH_SHORT).show();
//                selectRadioButton(LSDeal.DEAL_STATUS_CLOSED_WON);
//            }
//        });
//        bLostRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(EditDealActivity.this, "Lost", Toast.LENGTH_SHORT).show();
//                selectRadioButton(LSDeal.DEAL_STATUS_CLOSED_LOST);
//            }
//        });
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
                String dealPhone = etNumberAddDeal.getText().toString();

                if (isValid(dealName, dealPhone)) {

                    String intlNum = PhoneNumberAndCallUtils.numberToInterNationalNumber(EditDealActivity.this, dealPhone);
                    LSContact tempContact = LSContact.getContactFromNumber(intlNum);
                    if (tempContact != null && tempContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                        if (selectedDeal != null) {
                            LSDeal lsDeal = selectedDeal;
                            lsDeal.setName(dealName);
                            lsDeal.setContact(tempContact);
//                            lsDeal.setLeadId(Long.toString(tempContact.getId()));
                            lsDeal.setStatus(selectedDealType);
                            lsDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                            lsDeal.save();
                            finish();
                            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(EditDealActivity.this);
                            dataSenderAsync.run();
                        }else {
                            Toast.makeText(EditDealActivity.this, "Deal doesn't exists", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(EditDealActivity.this, "Corresponding Lead doesn't exists", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    private void populateUpdateContactView(Bundle bundle) {
//        tvTitleAddContact.setText(TITLE_EDIT_CONTACT);
        String id = bundle.getString(TAG_LAUNCH_MODE_DEAL_ID);
        if (id != null && !id.equals("")) {
            dealIdLong = Long.parseLong(id);
            selectedDeal = LSDeal.findById(LSDeal.class, dealIdLong);
        }
        if (selectedDeal.getName() != null) {
            etNameAddDeal.setText(selectedDeal.getName());
//            selectRadioButton(selectedDeal.getStatus());
        } else {
            etNameAddDeal.setText("");
        }
        if (selectedDeal.getContact() != null && !selectedDeal.getContact().getPhoneOne().equalsIgnoreCase(null)) {
            etNumberAddDeal.setText(selectedDeal.getContact().getPhoneOne());
        } else {
            etNumberAddDeal.setText("");
        }
        etNumberAddDeal.setFocusable(false);
    }

    private boolean isValid(String dealName, String dealPhone) {
        etNameAddDeal.setError(null);
        etNumberAddDeal.setError(null);
        boolean validation = true;
        if (dealName.equals("") || dealName.length() < 3) {
            validation = false;
            etNameAddDeal.setError("Invalid Name!");
            return false;
        }
        if (dealPhone.equals("") || dealPhone.length() < 3) {
            validation = false;
            etNumberAddDeal.setError("Invalid Number!");
            return false;
        }
        return true;
    }

//    private void selectRadioButton(String button) {
//        if (button.equals(LSDeal.DEAL_STATUS_PENDING)) {
//
//            selectedDealType = LSDeal.DEAL_STATUS_PENDING;
//
//            bPendingRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//            bWonRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//            bLostRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//
//            bPendingRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
//            bWonRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//            bLostRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//
//            bPendingRadio.setTextColor(Color.WHITE);
//            bWonRadio.setTextColor(Color.BLACK);
//            bLostRadio.setTextColor(Color.BLACK);
//
//        } else if (button.equals(LSDeal.DEAL_STATUS_CLOSED_WON)) {
//
//            selectedDealType = LSDeal.DEAL_STATUS_CLOSED_WON;
//
//            bWonRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//            bPendingRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//            bLostRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//
//            bWonRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
//            bPendingRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//            bLostRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//
//            bWonRadio.setTextColor(Color.WHITE);
//            bPendingRadio.setTextColor(Color.BLACK);
//            bLostRadio.setTextColor(Color.BLACK);
//
//        } else if (button.equals(LSDeal.DEAL_STATUS_CLOSED_LOST)) {
//
//            selectedDealType = LSDeal.DEAL_STATUS_CLOSED_LOST;
//
//            bLostRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//            bPendingRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//            bWonRadio.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//
//            bLostRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
//            bPendingRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//            bWonRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_black_border));
//
//            bLostRadio.setTextColor(Color.WHITE);
//            bPendingRadio.setTextColor(Color.BLACK);
//            bWonRadio.setTextColor(Color.BLACK);
//
//        }
//    }

}
