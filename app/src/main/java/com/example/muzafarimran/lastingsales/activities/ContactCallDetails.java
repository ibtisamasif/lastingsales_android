package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.IndividualConatactCallAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;

import static android.view.View.GONE;

public class ContactCallDetails extends AppCompatActivity {
    Button bTagButton;
    private String number = "";
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_call_details);
        bTagButton = (Button) findViewById(R.id.b_tag_individual_contact_call_screen);
        Intent intent = getIntent();
        this.number = intent.getStringExtra("number");
        LSContact contact = LSContact.getContactFromNumber(this.number);
        if (contact != null) {
            this.name = contact.getContactName();
            bTagButton.setVisibility(GONE);
        } else {
            this.name = "UNKNOWN";
            //bFollowupButton.setVisibility(GONE);
            bTagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ContactCallDetails.this, "I AM PRESSED", Toast.LENGTH_SHORT).show();
                    Intent addContactIntent = new Intent(getApplicationContext(), TagNumberAndAddFollowupActivity.class);
                    addContactIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_TAG_PHONE_NUMBER);
                    addContactIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE, LSContact.CONTACT_TYPE_SALES);
                    addContactIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, number);
                    startActivity(addContactIntent);
                }
            });
        }
        //TODO get name of the person if exists
        {
            setUpList();
        }
    }

    private void setUpList() {
        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();
        CallClickListener callClickListener = new CallClickListener(ContactCallDetails.this);
        ((TextView) (this.findViewById(R.id.call_numbe_ind))).setText(this.number);
        String contactName = this.name;
        ((ImageView) (this.findViewById(R.id.call_icon_ind))).setTag(this.number);
        ((ImageView) (this.findViewById(R.id.call_icon_ind))).setOnClickListener(callClickListener);
        //hide tag button if name is not stored
        if (this.name == null || (this.name).isEmpty()) {
            ((Button) (this.findViewById(R.id.b_tag_individual_contact_call_screen))).setVisibility(GONE);
            contactName = this.name;
        }
        ((TextView) (this.findViewById(R.id.contact_name_ind))).setText(contactName);
        ListView listview = (ListView) this.findViewById(R.id.calls_list);
        IndividualConatactCallAdapter indadapter = new IndividualConatactCallAdapter(ContactCallDetails.this, allCalls);
        listview.setAdapter(indadapter);
    }
}