package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.carditems.HomeItem;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

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

        list.clear();
        Collection<LSContact> unlabeledContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
        if (!unlabeledContacts.isEmpty()) {

            Collection<HomeItem> listHome = new ArrayList<HomeItem>();
            HomeItem item = new HomeItem();
            item.text = "HOME CARD";
            item.value = "" + unlabeledContacts.size();
            listHome.add(item);

            SeparatorItem separatorItem = new SeparatorItem();
            separatorItem.text = "Unlabeled contacts";

//        Collection<LoadingItem> listLoading = new ArrayList<LoadingItem>();
//        LoadingItem loadingItem = new LoadingItem();
//        loadingItem.text = "Loading items...";
//        listLoading.add(loadingItem);

            list.addAll(listHome);
//        list.addAll(inquiriesContacts);
            list.add(separatorItem);
            list.addAll(unlabeledContacts);
//        list.addAll(listLoading);

        } else {
            Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
            ErrorItem erItem = new ErrorItem();
            erItem.message = "NOTHING TO DISPLAY";
            listError.add(erItem);
            list.addAll(listError);
        }
        return list;
    }
}
