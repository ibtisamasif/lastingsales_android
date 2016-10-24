package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo 1 on 10/8/2016.
 */
public class SecondLevelFragmentPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();

    private Context context;

    public SecondLevelFragmentPagerAdapter(FragmentManager fm, Context context) {

        super(fm);
        this.context = context;
    }

    public void addFrag(Fragment f, String title) {
        fragList.add(f);
        titleList.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
