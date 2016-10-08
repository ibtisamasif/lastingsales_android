package com.example.muzafarimran.lastingsales;


import android.os.Bundle;
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

    public CallTabsFragment() {
        // Required empty public constructor
    }


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

        SecondLevelFragmentPagerAdapter adp = new SecondLevelFragmentPagerAdapter(getFragmentManager(),this.getContext());
        //NewsList n1 = new NewsList();
        MissedCallsFragment mc = new MissedCallsFragment();
        List<Call> missedCalls = new ArrayList<>();
        missedCalls.add(new Call("Kashif Naeem", "03xx-yyzzxxx", "missed", "2 hours ago"));
        missedCalls.add(new Call("Kashif Naeem", "03xx-yyzzxxx", "missed", "2 hours ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Raza Ahmad", "0332-5404943", "incoming", "10 mins ago"));



        mc.setList(missedCalls);

        adp.addFrag(mc, "Missed");

        vpCalls.setAdapter(adp);
       // tabs.setupWithViewPager(vpCalls);
    }

}
