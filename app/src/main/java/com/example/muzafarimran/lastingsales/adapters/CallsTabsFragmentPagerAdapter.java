package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.Events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.fragments.AllCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.IncomingCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.MissedCallsFragment;
import com.example.muzafarimran.lastingsales.fragments.OutgoingCallsFragment;

import java.util.ArrayList;
import java.util.List;


public class CallsTabsFragmentPagerAdapter extends FragmentPagerAdapter {


    static final int NUM_ITEMS = 4;

    public CallsTabsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MissedCallsFragment.newInstance(0, MissedCallsFragment.TAG);
            case 1:
                return IncomingCallsFragment.newInstance(1, IncomingCallsFragment.TAG);
            case 2:
                return OutgoingCallsFragment.newInstance(2, OutgoingCallsFragment.TAG);
            case 3:
                return AllCallsFragment.newInstance(3, AllCallsFragment.TAG);
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
                return "MISSED";
            case 1:
                return "INCOMING";
            case 2:
                return "OUTGOING";
             case 3:
                return "ALL";
            default:
                return null;
        }
    }
}