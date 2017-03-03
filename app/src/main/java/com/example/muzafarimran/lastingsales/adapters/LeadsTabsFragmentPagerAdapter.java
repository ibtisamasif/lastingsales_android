package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.InProgressFragment;
import com.example.muzafarimran.lastingsales.fragments.InActiveLeadsFragment;
import com.example.muzafarimran.lastingsales.fragments.LostFragment;
import com.example.muzafarimran.lastingsales.fragments.SalesFragment;
import com.example.muzafarimran.lastingsales.fragments.WonFragment;

/**
 * Created by ibtisam on 12/28/2016.
 */

public class LeadsTabsFragmentPagerAdapter extends FragmentPagerAdapter {

    static final int NUM_ITEMS = 5;

    public LeadsTabsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return InProgressFragment.newInstance(0, InProgressFragment.TAG);
            case 1:
                return WonFragment.newInstance(1, WonFragment.TAG);
            case 2:
                return LostFragment.newInstance(2, LostFragment.TAG);
            case 3:
                return InActiveLeadsFragment.newInstance(3, InActiveLeadsFragment.TAG);
            case 4:
                return SalesFragment.newInstance(4, SalesFragment.TAG);
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
                return "Leads";
            case 1:
                return "Won";
            case 2:
                return "Lost";
            case 3:
                return "InActive";
            case 4:
                return "All";
            default:
                return null;
        }
    }
}
