package com.example.muzafarimran.lastingsales.providers.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.List;

public class UnlabeledLoader extends AsyncTaskLoader<List<LSContact>> {
    public UnlabeledLoader(Context context) {
        super(context);
    }

    @Override
    public List<LSContact> loadInBackground() {
        List<LSContact> contacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
        return contacts;
    }
}