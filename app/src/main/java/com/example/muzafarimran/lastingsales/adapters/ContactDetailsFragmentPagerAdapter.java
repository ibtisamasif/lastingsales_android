package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.FollowupInContactDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.HomeFragment;
import com.example.muzafarimran.lastingsales.fragments.IndividualCallLogsFragment;
import com.example.muzafarimran.lastingsales.fragments.NotesByContactsFragment;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class ContactDetailsFragmentPagerAdapter extends FragmentPagerAdapter{
    final int TAB_COUNT = 4;
    private String tabTitles[] = new String[]{"Logs", "Details", "Followup" , "Notes"};
    private String number;

    public ContactDetailsFragmentPagerAdapter(FragmentManager fm , String number) {
        super(fm);
        this.number = number;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = null;
        switch (position) {
            case 0:
                fragment = IndividualCallLogsFragment.newInstance(0 , IndividualCallLogsFragment.TAG , number);
                break;
            case 1:
                fragment = new HomeFragment();
                break;
            case 2:
                fragment = FollowupInContactDetailsFragment.newInstance(2 , FollowupInContactDetailsFragment.TAG , number);
                break;
            case 3:
                fragment = NotesByContactsFragment.newInstance(3 , NotesByContactsFragment.TAG , number);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Calls";
            case 1:
                return "Details";
            case 2:
                return "Followups";
            case 3:
                return "Notes";
            default:
                return null;
        }
    }
}
