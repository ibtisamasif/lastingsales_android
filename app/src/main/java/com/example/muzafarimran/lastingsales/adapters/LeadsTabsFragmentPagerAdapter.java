package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.ActiveInActiveLeadsFragment;
import com.example.muzafarimran.lastingsales.fragments.InActiveLeadsFragment;
import com.example.muzafarimran.lastingsales.fragments.LostFragment;
import com.example.muzafarimran.lastingsales.fragments.PendingProspectsFragment;
import com.example.muzafarimran.lastingsales.fragments.SalesFragment;
import com.example.muzafarimran.lastingsales.fragments.WonFragment;

/**
 * Created by ibtisam on 12/28/2016.
 */

public class LeadsTabsFragmentPagerAdapter extends FragmentPagerAdapter {

    static final int NUM_ITEMS = 6;

    public LeadsTabsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PendingProspectsFragment.newInstance(0, PendingProspectsFragment.TAG);
            case 1:
                return ActiveInActiveLeadsFragment.newInstance(1, ActiveInActiveLeadsFragment.TAG);
            case 2:
                return WonFragment.newInstance(2, WonFragment.TAG);
            case 3:
                return LostFragment.newInstance(3, LostFragment.TAG);
            case 4:
                return InActiveLeadsFragment.newInstance(4, InActiveLeadsFragment.TAG);
            case 5:
                return SalesFragment.newInstance(5, SalesFragment.TAG);
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
                return "Prospects";
            case 1:
                return "Leads";
            case 2:
                return "Won";
            case 3:
                return "Lost";
            case 4:
                return "InActive";
            case 5:
                return "All";
            default:
                return null;
        }
    }
}
