package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.ColleagueActivity;
import com.example.muzafarimran.lastingsales.activities.IgnoredActivity;
import com.example.muzafarimran.lastingsales.carditems.MoreItem;
import com.example.muzafarimran.lastingsales.carditems.SettingItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class MoreLoader extends AsyncTaskLoader<List<Object>> {
    private List<Object> list = new ArrayList<Object>();

    public MoreLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        list.clear();

        MoreItem moreItemColleague = new MoreItem("Colleague", ColleagueActivity.class);
        moreItemColleague.drawable = R.drawable.bg_collegeue_card_xxxhdpi;
        MoreItem moreItemIgnored = new MoreItem("Ignored", IgnoredActivity.class);
        moreItemIgnored.drawable = R.drawable.bg_ignore_card_xxxhdpi;
        MoreItem moreItemSetting = new MoreItem("Settings");
        moreItemSetting.drawable = R.drawable.bg_ignore_card_xxxhdpi;

        list.add(moreItemColleague);
        list.add(moreItemIgnored);
        list.add(moreItemSetting);

        return list;
    }
}
