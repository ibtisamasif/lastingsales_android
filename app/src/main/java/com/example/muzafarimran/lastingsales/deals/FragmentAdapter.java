package com.example.muzafarimran.lastingsales.deals;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private Context ctx;
    private List<LSDynamicDealPipeline> data;
    private Fragment[] fragments;

    public FragmentAdapter(Context ctx, FragmentManager fm, List<LSDynamicDealPipeline> data) {
        super(fm);
        this.ctx = ctx;
        this.data = data;
        fragments = new Fragment[data.size()];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        LSDynamicDealPipeline items = (LSDynamicDealPipeline) data.get(position);


        DynamicFragment dynamicFragment = new DynamicFragment();
        dynamicFragment.setDetail(items.getName());
        fragment = dynamicFragment;

        if (fragments[position] == null) {
            fragments[position] = fragment;
        }
        return fragments[position];
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (data != null) {
            return data.get(position).getName();
        }
        return null;
    }
}