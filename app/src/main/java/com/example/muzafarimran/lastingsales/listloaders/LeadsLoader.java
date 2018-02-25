package com.example.muzafarimran.lastingsales.listloaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ChipItem;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 11/7/2017.
 */

public class LeadsLoader extends AsyncTaskLoader<List<Object>> {
    private String leadsToLoad;
    private List<Object> list = new ArrayList<Object>();
    Bundle bundle;

    public LeadsLoader(Context context, Bundle args) {
        super(context);
        bundle = args;
        if (bundle != null) {
            leadsToLoad = bundle.getString("whichLeads");
        } else {
            leadsToLoad = "All";
        }
    }

    @Override
    public List<Object> loadInBackground() {
//        AddLeadItem addLeadItem = new AddLeadItem();
//        list.add(addLeadItem);

        if (leadsToLoad != null && leadsToLoad.equals("All")) {
            Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
            if (contacts != null && contacts.size() > 0) {

                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

//                list.addAll(listHome);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 4;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                list.addAll(contacts);

                SeparatorItem separatorSpace = new SeparatorItem();
                separatorSpace.text = "";
                list.add(separatorSpace);
                list.add(separatorSpace);

            } else {
                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 3;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
                ErrorItem erItem = new ErrorItem();
                erItem.message = "Nothing in leads";
                erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
                listError.add(erItem);
                list.addAll(listError);
            }

            return list;

        } else if (leadsToLoad != null && leadsToLoad.equals("InProgress")) {

            List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
            if (contacts != null && contacts.size() > 0) {

                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

//                list.addAll(listHome);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 4;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                list.addAll(contacts);

                SeparatorItem separatorSpace = new SeparatorItem();
                separatorSpace.text = "";
                list.add(separatorSpace);
                list.add(separatorSpace);

            } else {
                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 3;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
                ErrorItem erItem = new ErrorItem();
                erItem.message = "Nothing in InProgress";
                erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
                listError.add(erItem);
                list.addAll(listError);
            }
            return list;

        } else if (leadsToLoad != null && leadsToLoad.equals("Won")) {

            List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
            if (contacts != null && contacts.size() > 0) {

                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

//                list.addAll(listHome);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 4;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                list.addAll(contacts);

                SeparatorItem separatorSpace = new SeparatorItem();
                separatorSpace.text = "";
                list.add(separatorSpace);
                list.add(separatorSpace);

            } else {
                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 3;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
                ErrorItem erItem = new ErrorItem();
                erItem.message = "Nothing in Won";
                erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
                listError.add(erItem);
                list.addAll(listError);
            }
            return list;

        } else if (leadsToLoad != null && leadsToLoad.equals("Lost")) {

            List<LSContact> contacts = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
            if (contacts != null && contacts.size() > 0) {

                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

//                list.addAll(listHome);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 4;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                list.addAll(contacts);

                SeparatorItem separatorSpace = new SeparatorItem();
                separatorSpace.text = "";
                list.add(separatorSpace);
                list.add(separatorSpace);

            } else {
                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 3;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
                ErrorItem erItem = new ErrorItem();
                erItem.message = "Nothing in Lost";
                erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
                listError.add(erItem);
                list.addAll(listError);
            }
            return list;

        } else if (leadsToLoad != null && leadsToLoad.equals("InActive")) {

            Collection<LSContact> contacts = LSContact.getAllInactiveLeadContacts();
            if (contacts != null && contacts.size() > 0) {

                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

//                list.addAll(listHome);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 4;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                list.addAll(contacts);

                SeparatorItem separatorSpace = new SeparatorItem();
                separatorSpace.text = "";
                list.add(separatorSpace);
                list.add(separatorSpace);

            } else {
                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";
                list.add(separatorItem);

                ChipItem chipItem = new ChipItem();
//                chipItem.selected = 3;
                chipItem.totalButtons = 5;
                list.add(chipItem);

                Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
                ErrorItem erItem = new ErrorItem();
                erItem.message = "Nothing in InActive";
                erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
                listError.add(erItem);
                list.addAll(listError);
            }
            return list;

        } else {
            Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
            if (contacts != null && contacts.size() > 0) {

                SeparatorItem separatorItem = new SeparatorItem();
                separatorItem.text = "Leads";

                ChipItem chipItem = new ChipItem();
                chipItem.selected = 1;
                chipItem.totalButtons = 5;

                list.add(separatorItem);
//                list.addAll(listHome);
                list.add(chipItem);
                list.addAll(contacts);

                SeparatorItem separatorSpace = new SeparatorItem();
                separatorSpace.text = "";

                list.add(separatorSpace);
                list.add(separatorSpace);

            } else {
                Collection<ErrorItem> listError = new ArrayList<ErrorItem>();
                ErrorItem erItem = new ErrorItem();
                erItem.message = "Nothing in Leads";
                erItem.drawable = R.drawable.ic_unlableled_empty_xxxhdpi;
                listError.add(erItem);
                list.addAll(listError);
            }
            return list;
        }
    }
}
