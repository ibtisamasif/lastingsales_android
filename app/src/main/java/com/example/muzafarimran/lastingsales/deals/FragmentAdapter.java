package com.example.muzafarimran.lastingsales.deals;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.muzafarimran.lastingsales.providers.models.LSStage;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private Context ctx;
    private List<LSStage> data;
    private Fragment[] fragments;

    public FragmentAdapter(Context ctx, FragmentManager fm, List<LSStage> data) {
        super(fm);
        this.ctx = ctx;
        this.data = data;
        fragments = new Fragment[data.size()];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        LSStage steps = (LSStage) data.get(position);

        DynamicFragment dynamicFragment = new DynamicFragment();
        dynamicFragment.setDetail(steps.getName() + "" + steps.getServerId());
        dynamicFragment.setStepId(steps.getServerId() + "");
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