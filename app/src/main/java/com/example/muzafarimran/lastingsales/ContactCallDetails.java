package com.example.muzafarimran.lastingsales;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ContactCallDetails extends AppCompatActivity {

    LinearLayout mainLayout;
    TabLayout tabs;
    ViewPager vpContactCalls;
    private String number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_call_details);


        Intent intent = getIntent();
        this.number = intent.getStringExtra("number");

        this.vpContactCalls = (ViewPager) findViewById(R.id.contact_call_detail_vp);
        this.tabs = (TabLayout) findViewById(R.id.sliding_tabs_call_details);
        setUpPager();
    }


    private void setUpPager() {

        SecondLevelFragmentPagerAdapter adp = new SecondLevelFragmentPagerAdapter(getSupportFragmentManager(), ContactCallDetails.this);

        MissedCallsFragment mc = new MissedCallsFragment();
        IncomingCallsFragment ic = new IncomingCallsFragment();
        OutgoingCallsFragment oc = new OutgoingCallsFragment();
        Bundle args = new Bundle();

        args.putInt("key", 2);

        //TODO replace the number and name with the ones from db
        args.putString("number","03234433108");
        args.putString("name","Salman");
        mc.setArguments(args);

        ic.setArguments(args);

        oc.setArguments(args);

        List<Call> missedCalls = new ArrayList<>();
        List<Call> incomingCalls = new ArrayList<>();
        List<Call> outgoingCalls = new ArrayList<>();

        //TODO get call history against the number and remove the data below
        missedCalls.add(new Call("", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("abc", "0332-5404943", "missed", "10 mins ago"));
        missedCalls.add(new Call("def", "03xx-yyzzxxx", "missed", "2 hours ago"));
        missedCalls.add(new Call("ghi", "0323-4433108", "missed", "1 min ago"));

        missedCalls.add(new Call("lmn", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("opq", "0323-4433108", "missed", "1 min ago"));


        missedCalls.add(new Call("rst", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("uvw", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("xyz", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));

        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));

        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));


        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mc.setList(missedCalls);
        ic.setList(incomingCalls);
        oc.setList(outgoingCalls);

        adp.addFrag(mc, "Missed");
        adp.addFrag(ic,"Incoming");
        adp.addFrag(oc,"Outgoing");

        this.vpContactCalls.setAdapter(adp);
        tabs.setupWithViewPager(this.vpContactCalls);
    }


}
