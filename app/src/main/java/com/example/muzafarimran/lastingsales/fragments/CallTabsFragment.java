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

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsTabsFragmentPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class CallTabsFragment extends TabFragment {

    LinearLayout mainLayout;
    TabLayout tabs;
    ViewPager vpCalls;
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
        mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_call_tabs, container, false);
        tabs = (TabLayout) mainLayout.findViewById(R.id.call_types);
        vpCalls = (ViewPager) mainLayout.findViewById(R.id.vp_call_types);
        setUpPager();
        return mainLayout;
    }

    private void setUpPager() {
        CallsTabsFragmentPagerAdapter adp = new CallsTabsFragmentPagerAdapter(getChildFragmentManager());
        vpCalls.setAdapter(adp);
        tabs.setupWithViewPager(vpCalls);
    }
}