package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.ColleagueActivity;
import com.example.muzafarimran.lastingsales.activities.IgnoredActivity;
import com.example.muzafarimran.lastingsales.carditems.MoreItem;

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
        moreItemColleague.drawable = R.drawable.bg_collegue_cardxxxhdpi;
        moreItemColleague.description = "Contacts that are not your leads i.e vendors, colleagues and business contacts.";

        MoreItem moreItemIgnored = new MoreItem("Ignored", IgnoredActivity.class);
        moreItemIgnored.drawable = R.drawable.bg_ignord_cardxxxhdpi;
        moreItemIgnored.description = "Contacts that you don't want to be tracked i.e friends and family.";

        MoreItem moreItemSetting = new MoreItem("Settings");
        moreItemSetting.drawable = R.drawable.bg_setting_cardcopyxxxhdpi;
        moreItemSetting.description = "Manage your accounts and notification preferences.";

//        MoreItem moreItemCompany = new MoreItem("Company", CompanyActivity.class);
//        moreItemCompany.drawable = R.drawable.bg_ignord_cardxxxhdpi;
//        moreItemCompany.description = "Contacts that you don't want to be tracked i.e friends and family.";

        list.add(moreItemColleague);
        list.add(moreItemIgnored);
        list.add(moreItemSetting);
//        list.add(moreItemCompany);

        return list;
    }
}
