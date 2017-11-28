package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.R;
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

        Collection<LSInquiry> inquiriesContacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        HomeItem item2 = new HomeItem();
        item2.text = "Inquiries";
        item2.value = "" + inquiriesContacts.size();
        list.add(item2);

        Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
        HomeItem item3 = new HomeItem();
        item3.text = "Leads";
        item3.value = "" + contacts.size();

        list.add(item3);

        Collection<LSContact> unlabeledContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
        if (!unlabeledContacts.isEmpty()) {

            HomeItem item = new HomeItem();
            item.text = "Unlabeled contacts";
            item.value = "" + unlabeledContacts.size();

            SeparatorItem separatorItem = new SeparatorItem();
            separatorItem.text = "Recent unlabeled contacts";

//        Collection<LoadingItem> listLoading = new ArrayList<LoadingItem>();
//        LoadingItem loadingItem = new LoadingItem();
//        loadingItem.text = "Loading items...";
//        listLoading.add(loadingItem);

            list.add(item);

//        list.addAll(inquiriesContacts);
            list.add(separatorItem);
            list.addAll(unlabeledContacts);
//        list.addAll(listLoading);

        } else {
            ErrorItem erItem = new ErrorItem();
            erItem.message = "Nothing in Unlabeled";
            erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
            list.add(erItem);
        }
        return list;
    }
}