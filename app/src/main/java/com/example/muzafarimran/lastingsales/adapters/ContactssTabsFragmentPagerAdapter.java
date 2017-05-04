package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.BusinessFragment;
import com.example.muzafarimran.lastingsales.fragments.IgnoredFragment;
import com.example.muzafarimran.lastingsales.fragments.SalesFragment;
import com.example.muzafarimran.lastingsales.fragments.UnlabeledFragment;

@Deprecated
public class ContactssTabsFragmentPagerAdapter extends FragmentPagerAdapter {


    static final int NUM_ITEMS = 4;

    public ContactssTabsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SalesFragment.newInstance(0, "Missed Calls");
            case 1:
                return BusinessFragment.newInstance(1, "Incoming Calls");
            case 2:
                return IgnoredFragment.newInstance(2, "Outgoing Calls");
            case 3:
                return UnlabeledFragment.newInstance(2, "Outgoing Calls");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
         switch (position) {
            case 0:
                return "SALES";
            case 1:
                return "COLLEAGUES";
            case 2:
                return "NON-BUSINESS";
            case 3:
                return "UNTAGGED";
            default:
                return null;
        }
    }
}