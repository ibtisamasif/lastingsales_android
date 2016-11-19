package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

    public static final String NUMBER_EXTRA = "number_extra";
    private String number = "";
    private String name = "";

    private LSContact contact;

    Button bTagButton;
    Button bFollowupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_call_details);


        bTagButton = (Button) findViewById(R.id.b_tag_individual_contact_call_screen);
        bFollowupButton = (Button) findViewById(R.id.b_followup_individual_contact_call_screen);


        Intent intent = getIntent();
        this.number = intent.getStringExtra("number");

        LSContact contact = LSContact.getContactFromNumber(this.number);

        if (contact != null) {
            this.name = contact.getContactName();
            bTagButton.setVisibility(GONE);
            bFollowupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), FollowupsActivity.class));
                }
            });
        } else {
            this.name = "UNKNOWN";
            bFollowupButton.setVisibility(GONE);
            bTagButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addContactIntent = new Intent(getApplicationContext(), AddContactActivity.class);

                    addContactIntent.putExtra(NUMBER_EXTRA,number);

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

/*

        List<Call> allCalls = new ArrayList<>();

        //TODO get call history against the number and remove the data below
        allCalls.add(new Call("", "0323-4433108", "missed", "1 min ago"));
        allCalls.add(new Call("abc", "0332-5404943", "missed", "10 mins ago"));
        allCalls.add(new Call("def", "03xx-yyzzxxx", "missed", "2 hours ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "missed", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "missed", "1 min ago"));

        allCalls.add(new Call("ghi", "0323-4433108", "incoming", "1 min ago"));


        allCalls.add(new Call("ghi", "0323-4433108", "incoming", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "incoming", "1 min ago"));

        allCalls.add(new Call("ghi", "0323-4433108", "incoming", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "incoming", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "incoming", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "incoming", "1 min ago"));

        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "missed", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "missed", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));
        allCalls.add(new Call("ghi", "0323-4433108", "outgoing", "1 min ago"));

*/


//        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).orderBy("begin_time DESC").list();
        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();
//        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) LSCall.listAll(LSCall.class);


//        Collections.sort(allCalls, AnalysisCell.ASCENDING_COMPARE_BY_DATA_DOUBLE);


        //View view = getLayoutInflater().inflate(R.layout.list_view_contact_call_details, null);

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