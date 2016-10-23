package com.example.muzafarimran.lastingsales;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ContactCallDetails extends AppCompatActivity {

    private String number = "";
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_call_details);

        Intent intent = getIntent();
        this.number = intent.getStringExtra("number");

        //TODO get name of the person if exists
        this.name = "Salman";

        setUpList();

    }


    private void setUpList() {


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


        //View view = getLayoutInflater().inflate(R.layout.list_view_contact_call_details, null);

        CallClickListener callClickListener = new CallClickListener(ContactCallDetails.this);

        ((TextView)(this.findViewById(R.id.call_numbe_ind))).setText(this.number);


        ((TextView)(this.findViewById(R.id.contact_name_ind))).setText(this.name);
        ((ImageView)(this.findViewById(R.id.call_icon_ind))).setTag(this.number);
        ((ImageView)(this.findViewById(R.id.call_icon_ind))).setOnClickListener(callClickListener);

        //hide tag button if name is not stored
        if (this.name == null || (this.name).isEmpty()){
            ((Button)(this.findViewById(R.id.tag_button_ind))).setVisibility(View.GONE);
        }


        ListView listview = (ListView) this.findViewById(R.id.calls_list);
        IndividualConatactCallAdapter indadapter = new IndividualConatactCallAdapter(ContactCallDetails.this, allCalls);
        listview.setAdapter(indadapter);

    }

}
