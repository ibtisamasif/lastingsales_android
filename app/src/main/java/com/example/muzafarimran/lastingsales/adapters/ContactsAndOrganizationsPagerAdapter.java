package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.ContactsFragment;
import com.example.muzafarimran.lastingsales.fragments.OrganizationsFragment;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class ContactsAndOrganizationsPagerAdapter extends FragmentPagerAdapter {
    private final int TAB_COUNT = 2;
//    private Long id;
//    private String number;

    public ContactsAndOrganizationsPagerAdapter(FragmentManager fm) {
        super(fm);
//        this.id = id;
//        this.number = number;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ContactsFragment();
                break;
            case 1:
                fragment = new OrganizationsFragment();
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
                return "Contacts";
            case 1:
                return "Organization";
            default:
                return null;
        }
    }
}
