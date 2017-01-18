package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.LeadsTabsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.listeners.TabSelectedListener;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class LeadTabsFragment extends TabFragment implements TabSelectedListener{

    private TabLayout tabs;
    private ViewPager vpLeads;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leads_tabs, container, false);
        tabs = (TabLayout) view.findViewById(R.id.lead_types);
        vpLeads = (ViewPager) view.findViewById(R.id.vp_leads_types);
        LeadsTabsFragmentPagerAdapter adp = new LeadsTabsFragmentPagerAdapter(getChildFragmentManager());
        vpLeads.setAdapter(adp);
        tabs.setupWithViewPager(vpLeads);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onTabSelectedEvent(int position, String tag) {
        vpLeads.setCurrentItem(position);
    }
}
