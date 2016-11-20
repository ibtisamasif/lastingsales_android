package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.CollegueFragment;
import com.example.muzafarimran.lastingsales.fragments.IncomingCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.MissedCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.NonbusinessFragment;
import com.example.muzafarimran.lastingsales.fragments.OutgoingCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.SalesFragment;
import com.example.muzafarimran.lastingsales.fragments.UntaggedContactsCallsFragment;


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
                return CollegueFragment.newInstance(1, "Incoming Calls");
            case 2:
                return NonbusinessFragment.newInstance(2, "Outgoing Calls");
            case 3:
                return UntaggedContactsCallsFragment.newInstance(2, "Outgoing Calls");
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