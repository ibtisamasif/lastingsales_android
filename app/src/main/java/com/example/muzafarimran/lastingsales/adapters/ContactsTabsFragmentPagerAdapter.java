package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.CollegueFragment;
import com.example.muzafarimran.lastingsales.fragments.NonbusinessFragment;
import com.example.muzafarimran.lastingsales.fragments.SalesFragment;
import com.example.muzafarimran.lastingsales.fragments.UnlabeledContactsCallsFragment;

@Deprecated
public class ContactsTabsFragmentPagerAdapter extends FragmentPagerAdapter {

    static final int NUM_ITEMS = 4;

    public ContactsTabsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SalesFragment.newInstance(0, "sales");
            case 1:
                return UnlabeledContactsCallsFragment.newInstance(2, "Outgoing Calls");
            case 2:
                return CollegueFragment.newInstance(1, "colleagues");
            case 3:
                return NonbusinessFragment.newInstance(2, "nonbusiness");
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
                return "Sales";
            case 1:
                return "Untagged";
            case 2:
                return "Colleagues";
            case 3:
                return "Non Business";
            default:
                return null;
        }
    }
}