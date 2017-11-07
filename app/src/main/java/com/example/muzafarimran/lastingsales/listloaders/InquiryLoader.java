package com.example.muzafarimran.lastingsales.listloaders;


import android.app.LauncherActivity;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;

import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.home.ErrorItem;
import com.example.muzafarimran.lastingsales.home.SeparatorItem;
import com.example.muzafarimran.lastingsales.providers.loaders.InquiriesLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class InquiryLoader extends AsyncTaskLoader<List<Object>> {

    private List<Object> list = new ArrayList<Object>();

    public InquiryLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        list.clear();
        Collection<LSInquiry> inquiriesContacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//        if (!inquiriesContacts.isEmpty()) {
            list.addAll(inquiriesContacts);
//        } else {
//            Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
//            ErrorItem erItem = new ErrorItem();
//            erItem.message = "NOTHING TO DISPLAY";
//            listError.add(erItem);
//            list.addAll(listError);
//        }
        return list;
    }
}
