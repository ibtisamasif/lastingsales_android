package com.example.muzafarimran.lastingsales;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by MUZAFAR IMRAN on 9/18/2016.
 */
public  class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int TAB_COUNT = 4;
    private String tabTitles[] = new String[]{"Home", "Calls", "Contacts", "More"};
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        TabFragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new CallTabsFragment();
                break;
            case 2:
                fragment = new ContactsFragment();
                break;
            case 3:
                fragment = new MoreFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return "";
    }
}

