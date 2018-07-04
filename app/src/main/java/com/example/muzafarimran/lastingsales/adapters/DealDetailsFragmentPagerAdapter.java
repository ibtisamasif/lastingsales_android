package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.IndividualDealDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.NotesInDealDetailsFragment;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class DealDetailsFragmentPagerAdapter extends FragmentPagerAdapter {
    final int TAB_COUNT = 2;
    private Long id;

    public DealDetailsFragmentPagerAdapter(FragmentManager fm, Long id) {
        super(fm);
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = null;
        switch (position) {
            case 0:
                fragment = IndividualDealDetailsFragment.newInstance(0, IndividualDealDetailsFragment.TAG, id);
                break;
            case 1:
                LSDeal lsDeal = LSDeal.getDealFromId(id + "");
                if (lsDeal != null) {
//                    LSContact lsContact = lsDeal.getContact();
//                    long contactIdlong;
//                    if (lsContact != null) {
//                        contactIdlong = lsContact.getId();
                        //                fragment = IndividualDealDetailsFragment.newInstance(0, IndividualDealDetailsFragment.TAG, id);
                        fragment = NotesInDealDetailsFragment.newInstance(1, NotesInDealDetailsFragment.TAG, lsDeal.getId());
//                    }
                }
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
                return "Notes";
            default:
                return null;
        }
    }
}
