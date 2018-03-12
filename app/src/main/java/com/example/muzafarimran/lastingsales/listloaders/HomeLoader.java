package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.carditems.HomeItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class HomeLoader extends AsyncTaskLoader<List<Object>> {
    private List<Object> list = new ArrayList<Object>();

    public HomeLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        Collection<LSContact> unlabeledContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
        if (!unlabeledContacts.isEmpty()) {

            HomeItem homeItem = new HomeItem();
            homeItem.text = "UNLABELED";
            homeItem.value = "" + unlabeledContacts.size();
            homeItem.drawable = R.drawable.bg_unlabeled_cardxxxhdpi;

//            StatisticsItem statisticsItem = new StatisticsItem();
//            statisticsItem.artValue = 0;
//            statisticsItem.leadsValue = 0;
//            statisticsItem.inquiriesValue = 0;
//            statisticsItem.callsValue = 0;

            SeparatorItem separatorItem = new SeparatorItem();
            separatorItem.text = "Recent unlabeled contacts";

            list.add(homeItem);
//            list.add(statisticsItem);
            list.add(separatorItem);
            list.addAll(unlabeledContacts);

            SeparatorItem separatorSpace = new SeparatorItem();
            separatorSpace.text = "";

            list.add(separatorSpace);
            list.add(separatorSpace);

        } else {
            ErrorItem erItem = new ErrorItem();
            erItem.message = "Nothing in Unlabeled";
            erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
            list.add(erItem);
        }
        return list;
    }
}