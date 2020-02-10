package com.example.muzafarimran.lastingsales.providers.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.List;

public class WonLoader extends AsyncTaskLoader<List<LSContact>> {
    public WonLoader(Context context) {
		super(context);
    }
	@Override
    public List<LSContact> loadInBackground() {
		List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
		return contacts;
    }
}