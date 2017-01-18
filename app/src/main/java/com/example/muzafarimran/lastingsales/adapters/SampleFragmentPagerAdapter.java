package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.HomeFragment;
import com.example.muzafarimran.lastingsales.fragments.LeadTabsFragment;
import com.example.muzafarimran.lastingsales.fragments.MissedCallsFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MUZAFAR IMRAN on 9/18/2016.
 */

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int TAB_COUNT = 3;
    private String tabTitles[] = new String[]{"Home", "Inquiries", "Sales"};
    private List<Fragment> mFragments = new ArrayList<>();
    private Context context;


    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        init();
    }

    private void init() {
        mFragments.add(new HomeFragment());
        mFragments.add(new MissedCallsFragment());
        mFragments.add(new LeadTabsFragment());
    }

    @Override
    public Fragment getItem(int position) {
        if (position > mFragments.size()) {
            throw new IllegalArgumentException("Invalid Position");
        }
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return tabTitles[position];
//    }
}