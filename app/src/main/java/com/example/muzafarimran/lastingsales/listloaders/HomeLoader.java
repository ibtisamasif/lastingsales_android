package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.home.SeparatorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class HomeLoader  extends AsyncTaskLoader<List<Object>> {
    private List<Object> list = new ArrayList<Object>();

    public HomeLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        list.clear();
        Collection<LSInquiry> inquiriesContacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();

        Collection<SeparatorItem> listSeparator = new ArrayList<SeparatorItem>();
        SeparatorItem spItem = new SeparatorItem();
        spItem.text = "unlabeled contacts";
        listSeparator.add(spItem);

        Collection<LSContact> unlabeledContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);

        list.addAll(inquiriesContacts);
        list.addAll(listSeparator);
        list.addAll(unlabeledContacts);
        return list;
    }
}
