package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.activities.ColleagueActivity;
import com.example.muzafarimran.lastingsales.activities.IgnoredActivity;
import com.example.muzafarimran.lastingsales.carditems.SettingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class MoreLoader  extends AsyncTaskLoader<List<Object>>{
    private List<Object> list = new ArrayList<Object>();

    public MoreLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        list.clear();

        SettingItem settingItemAbout = new SettingItem("Colleague", ColleagueActivity.class);
        SettingItem settingItemSetting = new SettingItem("Ignored", IgnoredActivity.class);
        SettingItem settingItemLogout = new SettingItem("Settings");

        list.add(settingItemAbout);
        list.add(settingItemSetting);
        list.add(settingItemLogout);

        return list;
    }
}
