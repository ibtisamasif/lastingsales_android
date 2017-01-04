package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.LeadsTabsFragmentPagerAdapter;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class LeadTabsFragment extends TabFragment {

    LinearLayout mainLayout;
    TabLayout tabs;
    ViewPager vpLeads;
    TextView tab0 = null;
    TextView tab1 = null;
    TextView tab2 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_leads_tabs, container, false);
        tabs = (TabLayout) mainLayout.findViewById(R.id.lead_types);
        vpLeads = (ViewPager) mainLayout.findViewById(R.id.vp_leads_types);
        setUpPager();
        return mainLayout;
    }

    private void setUpPager() {
        LeadsTabsFragmentPagerAdapter adp = new LeadsTabsFragmentPagerAdapter(getChildFragmentManager());
        vpLeads.setAdapter(adp);
        tabs.setupWithViewPager(vpLeads);
    }
}
