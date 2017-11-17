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

public class LeadsLoader  extends AsyncTaskLoader<List<Object>> {
    private List<Object> list = new ArrayList<Object>();

    public LeadsLoader(Context context) {
        super(context);
    }

    @Override
    public List<Object> loadInBackground() {
        Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
        if(contacts != null){

            Collection<HomeItem> listHome = new ArrayList<HomeItem>();
            HomeItem item = new HomeItem();
            item.value = "" + contacts.size();
            item.text = "LEADS";
            listHome.add(item);

            SeparatorItem separatorItem = new SeparatorItem();
            separatorItem.text = "Leads";

            list.addAll(listHome);
            list.add(separatorItem);
            list.addAll(contacts);
        }else {
            Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
            ErrorItem erItem = new ErrorItem();
            erItem.message = "Nothing in leads";
            erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
            listError.add(erItem);
            list.addAll(listError);
        }

        return list;
    }
}
