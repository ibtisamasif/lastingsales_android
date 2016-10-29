package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.SecondLevelFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallTabsFragment extends TabFragment {

    LinearLayout mainLayout;
    TabLayout tabs;
    ViewPager vpCalls;
   //private tabSelectedListener tabselectedlistener = new tabSelectedListener();

    TextView tab0 = null;
    TextView tab1 = null;
    TextView tab2 = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_call_tabs, container, false);
        tabs = (TabLayout) mainLayout.findViewById(R.id.call_types);

        vpCalls = (ViewPager) mainLayout.findViewById(R.id.vp_call_types);
        setUpPager();


        tab0 = (TextView) inflater.inflate(R.layout.custom_tab, null);
        tab1 = (TextView) inflater.inflate(R.layout.custom_tab, null);
        tab2 = (TextView) inflater.inflate(R.layout.custom_tab, null);



        tab0.setText("Missed Call");

        tab0.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_missed_second_level, 0, 0);
        tabs.getTabAt(0).setCustomView(tab0);

        tab1.setText("Incoming Call");
        tab1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_incoming_second_level, 0, 0);
        tabs.getTabAt(1).setCustomView(tab1);

        tab2.setText("Outgoing Call");
        tab2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_outgoing_second_level, 0, 0);
        tabs.getTabAt(2).setCustomView(tab2);

        //tabs.addOnTabSelectedListener(this.tabselectedlistener);

        return mainLayout;

    }

    private void setUpPager() {

        SecondLevelFragmentPagerAdapter adp = new SecondLevelFragmentPagerAdapter(getChildFragmentManager(), getActivity());

        Bundle args = new Bundle();

        MissedCallsFragment mc = new MissedCallsFragment();
        IncomingCallsFragment ic = new IncomingCallsFragment();
        OutgoingCallsFragment oc = new OutgoingCallsFragment();
        args.putInt("key", 1);
        mc.setArguments(args);
        ic.setArguments(args);
        oc.setArguments(args);


        List<Call> missedCalls = new ArrayList<>();
        List<Call> incomingCalls = new ArrayList<>();
        List<Call> outgoingCalls = new ArrayList<>();

        missedCalls.add(new Call("Unanswered Sales", "", "seperator", ""));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Raza Ahmad", "0332-5404943", "missed", "10 mins ago"));
        missedCalls.add(new Call("Kashif Naeem", "03xx-yyzzxxx", "missed", "2 hours ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Unanswered Collegue", "", "seperator", ""));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));


        missedCalls.add(new Call("Unanswered Sales", "", "seperator", ""));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Raza Ahmad", "0332-5404943", "missed", "10 mins ago"));
        missedCalls.add(new Call("Kashif Naeem", "03xx-yyzzxxx", "missed", "2 hours ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Unanswered Collegue", "", "seperator", ""));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));

        missedCalls.add(new Call("Unanswered Sales", "", "seperator", ""));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Raza Ahmad", "0332-5404943", "missed", "10 mins ago"));
        missedCalls.add(new Call("Kashif Naeem", "03xx-yyzzxxx", "missed", "2 hours ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Unanswered Collegue", "", "seperator", ""));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));

        missedCalls.add(new Call("History", "", "seperator", ""));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        missedCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));

        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));

        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));
        outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));


        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mc.setList(missedCalls);
        ic.setList(incomingCalls);
        oc.setList(outgoingCalls);

        adp.addFrag(mc, "Missed");
        adp.addFrag(ic, "Incoming");
        adp.addFrag(oc, "Outgoing");

        vpCalls.setAdapter(adp);
        tabs.setupWithViewPager(vpCalls);
    }

/*
    public class tabSelectedListener implements TabLayout.OnTabSelectedListener {


        @Override
        public void onTabSelected(TabLayout.Tab tab) {


            switch (tab.getPosition()) {

                case 0:
                    //((TextView) tab0.findViewById(R.id.tab)).setTextColor("#ffffff");
                    tab0.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_missed_second_level_selected, 0, 0);
                    tab.setCustomView(tab0);
                    break;

                case 1:
                    tab1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_incoming_second_level_selected, 0, 0);
                    tab.setCustomView(tab1);
                    break;

                case 2:
                    tab2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_outgoing_second_level_selected, 0, 0);
                    tab.setCustomView(tab2);
                    break;


            }


        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

            switch (tab.getPosition()) {

                case 0:

                    //((TextView) tab0.findViewById(R.id.tab)).setTextColor("#ffffff");
                    tab0.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_missed_second_level, 0, 0);
                    tab.setCustomView(tab0);


                    break;

                case 1:
                    tab1.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_incoming_second_level, 0, 0);
                    tab.setCustomView(tab1);
                    break;

                case 2:
                    tab2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_icon_outgoing_second_level, 0, 0);
                    tab.setCustomView(tab2);
                    break;


            }


        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            //int position = tab.getPosition();
        }

    }*/

}
