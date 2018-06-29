package com.example.muzafarimran.lastingsales.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment2_1;
import com.example.muzafarimran.lastingsales.NavigationBottomFragments.BlankFragment2_2;
import com.example.muzafarimran.lastingsales.fragments.OrganizationFragment;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class MyUnlabeledPagerAdapter extends FragmentPagerAdapter {
    private final int TAB_COUNT = 2;
//    private Long id;
//    private String number;

    public MyUnlabeledPagerAdapter(FragmentManager fm) {
        super(fm);
//        this.id = id;
//        this.number = number;
    }

    @Override
    public Fragment getItem(int position) {
        TabFragment fragment = null;
        switch (position) {
            case 0:
                fragment = new BlankFragment2_2();
                break;
            case 1:
                fragment = new OrganizationFragment();
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
