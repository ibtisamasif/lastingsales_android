package com.example.muzafarimran.lastingsales;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallTabsFragment extends TabFragment{

    LinearLayout mainLayout;
    TabLayout tabs;
    ViewPager vpCalls;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_call_tabs, container, false);
        tabs = (TabLayout) mainLayout.findViewById(R.id.call_types);
        vpCalls = (ViewPager) mainLayout.findViewById(R.id.vp_call_types);
        setUpPager();

        return mainLayout;

    }

    private void setUpPager() {

        SecondLevelFragmentPagerAdapter adp = new SecondLevelFragmentPagerAdapter(getChildFragmentManager(), getActivity());
        //NewsList n1 = new NewsList();
        MissedCallsFragment mc = new MissedCallsFragment();
        IncomingCallsFragment ic = new IncomingCallsFragment();
        OutgoingCallsFragment oc = new OutgoingCallsFragment();

        List<Call> missedCalls = new ArrayList<>();
        List<Call> incomingCalls = new ArrayList<>();
        List<Call> outgoingCalls = new ArrayList<>();

        missedCalls.add(new Call("Kashif Naeem", "03xx-yyzzxxx", "missed", "2 hours ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Raza Ahmad", "0332-5404943", "missed", "10 mins ago"));

        incomingCalls.add(new Call("Kashif klkj", "03xx-yyzzxxx", "incoming", "2 hours ago"));
        incomingCalls.add(new Call("Salman lkj", "0323-4433108", "incoming", "1 min ago"));
        incomingCalls.add(new Call("Raza klj", "0332-5404943", "incoming", "10 mins ago"));

        outgoingCalls.add(new Call("hello", "03xx-yyzzxxx", "outgoing", "2 hours ago"));
        outgoingCalls.add(new Call("hi", "0323-4433108", "outgoing", "1 min ago"));
        outgoingCalls.add(new Call("some", "0332-5404943", "outgoing", "10 mins ago"));


        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mc.setList(missedCalls);
        ic.setList(incomingCalls);
        oc.setList(outgoingCalls);

        adp.addFrag(mc, "Missed");
        adp.addFrag(ic,"Incoming");
        adp.addFrag(oc,"Outgoing");

        vpCalls.setAdapter(adp);
        tabs.setupWithViewPager(vpCalls);
    }

}
