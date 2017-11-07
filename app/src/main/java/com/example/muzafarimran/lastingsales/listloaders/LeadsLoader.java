package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.home.ErrorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class LeadsLoader  extends AsyncTaskLoader<List<Object>> {
    private List<Object> list = new ArrayList<Object>();

    public LeadsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        list.clear();
        Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();

        if(contacts != null){
            list.addAll(contacts);
        }else {
            Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
            ErrorItem erItem = new ErrorItem();
            erItem.message = "NOTHING TO DISPLAY";
            listError.add(erItem);
            list.addAll(listError);
        }

        return list;
    }
}
