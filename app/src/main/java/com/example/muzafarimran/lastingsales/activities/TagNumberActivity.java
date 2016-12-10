package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

public class TagNumberActivity extends Activity {
    public static final String NUMBER_TO_TAG = "phone_number";
    private Button bSalesRadio = null;
    private Button bCollegueRadio = null;
    private Button bPersonalRadio = null;
    private String phoneNumber = null;
    private TextView textViewNum;
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
        textViewNum = (TextView) findViewById(R.id.tvNumberTagCallPopup);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneNumber = bundle.getString(NUMBER_TO_TAG);
        }
        if (phoneNumber != null) {
            textViewNum.setText(phoneNumber);
        }
        bSalesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInteracted = true;
                bSalesRadio.setBackground(ContextCompat.getDrawable(TagNumberActivity.this,R.drawable.btn_primary));
                Intent intent = new Intent(getApplicationContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE,TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
                startActivity(intent);
                finish();
            }
        });
        bCollegueRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInteracted = true;
                bCollegueRadio.setBackground(ContextCompat.getDrawable(TagNumberActivity.this,R.drawable.btn_primary));
                Intent intent = new Intent(getApplicationContext(), TagNumberAndAddFollowupActivity.class);
                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE,TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_COLLEAGUE);
                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER , phoneNumber);
                startActivity(intent);
                finish();
            }
        });
        bPersonalRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set contact as non-business to avoid future popups
                userInteracted = true;
                bPersonalRadio.setBackground(ContextCompat.getDrawable(TagNumberActivity.this,R.drawable.btn_primary));
                LSContact tempContact = new LSContact();
                tempContact.setPhoneOne(PhoneNumberAndCallUtils.numberToInterNationalNumber(phoneNumber));
                tempContact.setContactType(LSContact.CONTACT_TYPE_PERSONAL);
                tempContact.save();
                //Update Previous Record of User.
                PhoneNumberAndCallUtils.updateAllCallsOfThisContact(tempContact);
                finish();
                Toast.makeText(TagNumberActivity.this, "NON-BUSINESS", Toast.LENGTH_SHORT).show();
            }
        });
    }
}