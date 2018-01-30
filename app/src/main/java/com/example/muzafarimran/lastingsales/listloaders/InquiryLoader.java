package com.example.muzafarimran.lastingsales.listloaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.carditems.HomeItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
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

        Collection<LSInquiry> inquiriesContacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        if (!inquiriesContacts.isEmpty()) {

            Collection<HomeItem> listHome = new ArrayList<HomeItem>();
            HomeItem itemInquiry = new HomeItem();
            itemInquiry.text = "INQUIRIES";
            itemInquiry.value = "" + inquiriesContacts.size();
            itemInquiry.drawable = R.drawable.bg_inquiry_cardxxxhdpi;
            listHome.add(itemInquiry);

            SeparatorItem separatorItem = new SeparatorItem();
            separatorItem.text = "Inquiries";

            list.addAll(listHome);
            list.add(separatorItem);
            list.addAll(inquiriesContacts);

            SeparatorItem separatorSpace = new SeparatorItem();
            separatorSpace.text = "";

            list.add(separatorSpace);
            list.add(separatorSpace);

        } else {
            Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
            ErrorItem erItem = new ErrorItem();
            erItem.message = "Nothing in inquiries";
            erItem.drawable = R.drawable.ic_inquiries_empty_xxxhdpi;
            listError.add(erItem);
            list.addAll(listError);
        }
        return list;
    }
}
