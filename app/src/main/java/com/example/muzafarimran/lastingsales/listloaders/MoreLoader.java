package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.example.muzafarimran.lastingsales.home.SeparatorItem;
import java.util.ArrayList;
import java.util.Collection;
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
//        Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();

        Collection<SeparatorItem> listSeparator = new ArrayList<SeparatorItem>();

        SeparatorItem spItem = new SeparatorItem();
        spItem.text = "More Element";
        listSeparator.add(spItem);

        list.addAll(listSeparator);
        return list;
    }
}
