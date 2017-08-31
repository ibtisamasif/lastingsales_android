package com.example.muzafarimran.lastingsales.providers.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import java.util.List;

public class InquiriesLoader extends AsyncTaskLoader<List<LSInquiry>> {
    public InquiriesLoader(Context context) {
        super(context);
    }

    @Override
    public List<LSInquiry> loadInBackground() {
        List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        return inquiries;
    }
}