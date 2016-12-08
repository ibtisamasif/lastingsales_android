package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

public class TagNumberActivity extends Activity {

    public static final String NUMBER_TO_TAG = "phone_number";
    private Button bSalesRadio = null;
    private Button bCollegueRadio = null;
    private Button bPersonalRadio = null;
    private Button bSave = null;
    private Button bCancel = null;
    private String selectedContactType = LSContact.CONTACT_TYPE_SALES;
    private EditText etName = null;
    private LinearLayout llNameLayout;
    private String phoneNumber = null;
    private TextView tvNumber;
    private boolean userInteracted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_number);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!userInteracted) {
                    finish();
                }
            }
        }, 4000);
        bSalesRadio = (Button) findViewById(R.id.sales_radio);
        bCollegueRadio = (Button) findViewById(R.id.collegue_radio);
        bPersonalRadio = (Button) findViewById(R.id.personal_radio);
        bCancel = (Button) findViewById(R.id.bCancelTagCallPopup);
        bSave = (Button) findViewById(R.id.bSaveTagCallPopup);
        etName = (EditText) findViewById(R.id.etNameTagCallPopup);
        llNameLayout = (LinearLayout) findViewById(R.id.nameAndButtonForm);
        tvNumber = (TextView) findViewById(R.id.tvNumberTagCallPopup);
        Bundle bundle = null;
        bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNumber = bundle.getString(NUMBER_TO_TAG);
        }
        if (phoneNumber != null) {
            tvNumber.setText(phoneNumber);
        }
        etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInteracted = true;
            }
        });
        llNameLayout.setVisibility(View.GONE);
        bSalesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactType = LSContact.CONTACT_TYPE_SALES;
                bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
                bCollegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                bPersonalRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                llNameLayout.setVisibility(View.VISIBLE);
                userInteracted = true;
            }
        });
        bCollegueRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactType = LSContact.CONTACT_TYPE_COLLEAGUE;
                bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                bCollegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
                bPersonalRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                llNameLayout.setVisibility(View.VISIBLE);
                userInteracted = true;
            }
        });
        bPersonalRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedContactType = LSContact.CONTACT_TYPE_PERSONAL;
                bSalesRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                bCollegueRadio.setBackground(getResources().getDrawable(R.drawable.btn_transparent_white_border));
                bPersonalRadio.setBackground(getResources().getDrawable(R.drawable.btn_primary));
                llNameLayout.setVisibility(View.VISIBLE);
                userInteracted = true;
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etName.setError(null);
                boolean validation = true;
                String name = etName.getText().toString();
                if (name.equals("") || name.length() < 3) {
                    validation = false;
                    etName.setError("Invalid Name!");
                }
                if (validation) {
                    LSContact tempContact = new LSContact();
                    tempContact.setContactName(name);
                    tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(phoneNumber));
                    tempContact.setContactType(selectedContactType);
                    tempContact.save();
                    PhoneNumberAndCallUtils.updateAllCallsOfThisContact(tempContact);
                    finish();
                }
            }
        });
    }
}