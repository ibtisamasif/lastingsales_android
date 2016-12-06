package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.os.Build;
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

import static com.example.muzafarimran.lastingsales.R.id.nameAndButtonForm;

public class TagNumberActivity extends Activity {

    public static final String NUMBER_TO_TAG = "phone_number";
    Button bSalesRadio = null;
    Button bCollegueRadio = null;
    Button bPersonalRadio = null;
    Button bSave = null;
    Button bCancel = null;
    String selectedContactType = LSContact.CONTACT_TYPE_SALES;
    EditText etName = null;
    LinearLayout llNameLayout;
    String phoneNumber = null;
    TextView tvNumber;
    boolean userIntracted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_number);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!userIntracted) {
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
                userIntracted = true;
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
                userIntracted = true;
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
                userIntracted = true;
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
                userIntracted = true;
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