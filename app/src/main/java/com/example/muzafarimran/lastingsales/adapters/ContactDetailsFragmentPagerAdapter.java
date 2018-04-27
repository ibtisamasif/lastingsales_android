package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.CallLogsInContactDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.CommentsInDealDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.IndividualContactDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.NotesInContactDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class ContactDetailsFragmentPagerAdapter extends FragmentPagerAdapter {
    final int TAB_COUNT = 4;
    private Long id;

    public ContactDetailsFragmentPagerAdapter(FragmentManager fm, Long id) {
        super(fm);
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = null;
        switch (position) {
            case 0:
                fragment = IndividualContactDetailsFragment.newInstance(0, IndividualContactDetailsFragment.TAG, id);
                break;
            case 1:
                fragment = NotesInContactDetailsFragment.newInstance(1, NotesInContactDetailsFragment.TAG, id);
                break;
            case 2:
                fragment = CallLogsInContactDetailsFragment.newInstance(3, CallLogsInContactDetailsFragment.TAG, id);
                break;
            case 3:
                fragment = CommentsInDealDetailsFragment.newInstance(4, CommentsInDealDetailsFragment.TAG, id);
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
                return "Details";
            case 1:
                return "Notes & Followup";
            case 2:
                return "Calls";
            case 3:
                return "Comments";
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
