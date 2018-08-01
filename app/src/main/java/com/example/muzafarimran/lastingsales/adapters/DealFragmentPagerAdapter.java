package com.example.muzafarimran.lastingsales.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.muzafarimran.lastingsales.fragments.DealDynamicFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;

import java.util.List;

public class DealFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private Context ctx;
    private List<LSStage> data;
    private Fragment[] fragments;
    private LSStage currentStageVisible;

    public DealFragmentPagerAdapter(Context ctx, FragmentManager fm, List<LSStage> data) {
        super(fm);
        this.ctx = ctx;
        this.data = data;
        fragments = new Fragment[data.size()];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        LSStage stage = (LSStage) data.get(position);
        currentStageVisible = stage;
        DealDynamicFragment dealDynamicFragment = new DealDynamicFragment();
        dealDynamicFragment.setPageTitle(stage.getName());
        dealDynamicFragment.setStageId(stage.getServerId());

        fragment = dealDynamicFragment;
        if (fragments[position] == null) {
            fragments[position] = fragment;
        }
        return fragments[position];
    }

    public LSStage getStage() {
        return currentStageVisible;
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