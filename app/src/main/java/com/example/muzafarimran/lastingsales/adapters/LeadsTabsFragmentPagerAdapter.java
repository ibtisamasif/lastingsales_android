package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.AllFragment;
import com.example.muzafarimran.lastingsales.fragments.InProgressFragment;
import com.example.muzafarimran.lastingsales.fragments.InActiveLeadsFragment;
import com.example.muzafarimran.lastingsales.fragments.LostFragment;
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
                return AllFragment.newInstance(0, AllFragment.TAG);
            case 1:
                return InProgressFragment.newInstance(1, InProgressFragment.TAG);
            case 2:
                return WonFragment.newInstance(2, WonFragment.TAG);
            case 3:
                return LostFragment.newInstance(3, LostFragment.TAG);
            case 4:
                return InActiveLeadsFragment.newInstance(4, InActiveLeadsFragment.TAG);
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
                return "All";
            case 1:
                return "InProgress";
            case 2:
                return "Won";
            case 3:
                return "Lost";
            case 4:
                return "InActive";

            default:
                return null;
        }
    }
}
