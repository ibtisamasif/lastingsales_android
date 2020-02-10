package com.example.muzafarimran.lastingsales.providers.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;

public class InProgressLoader extends AsyncTaskLoader<List<LSContact>> {
    public InProgressLoader(Context context) {
		super(context);
    }
	@Override
    public List<LSContact> loadInBackground() {
		List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
		return contacts;
    }
}