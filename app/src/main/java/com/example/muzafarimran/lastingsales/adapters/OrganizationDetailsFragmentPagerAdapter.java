package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.IndividualOrganizationDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.NotesInOrganizationDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class OrganizationDetailsFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int TAB_COUNT = 2;
    private Long id;

    public OrganizationDetailsFragmentPagerAdapter(FragmentManager fm, Long id) {
        super(fm);
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = null;
        switch (position) {
            case 0:
                fragment = IndividualOrganizationDetailsFragment.newInstance(0, IndividualOrganizationDetailsFragment.TAG, id);
                break;
            case 1:
                fragment = NotesInOrganizationDetailsFragment.newInstance(1, NotesInOrganizationDetailsFragment.TAG, id);
                break;
//            case 2:
//                fragment = CallLogsInContactDetailsFragment.newInstance(3, CallLogsInContactDetailsFragment.TAG, id);
//                break;
//            case 3:
//                fragment = CommentsInDealDetailsFragment.newInstance(4, CommentsInDealDetailsFragment.TAG, id);
//                break;
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
                return "Notes";
//            case 2:
//                return "Calls";
//            case 3:
//                return "Comments";
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
