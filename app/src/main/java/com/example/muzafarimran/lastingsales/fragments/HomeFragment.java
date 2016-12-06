package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends TabFragment {

    private TextView tvUntaggedContacts;
    private TextView tvPendingProspectValue;
    private TextView tvInactiveLeadsValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvUntaggedContacts = (TextView) view.findViewById(R.id.untagged_contacts_val);
        tvPendingProspectValue = (TextView) view.findViewById(R.id.tvPendingProspectValue);
        tvInactiveLeadsValue = (TextView) view.findViewById(R.id.tvInactiveLeadsValue);
        ArrayList<LSCall> allUniqueCallsWithoutContact = LSCall.getUniqueCallsWithoutContacts();
        ArrayList<LSContact> allContactsAsProspects = (ArrayList<LSContact>) LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        ArrayList<LSContact> allInactiveLeads = (ArrayList<LSContact>) LSContact.getAllInactiveLeadContacts();
        if (allUniqueCallsWithoutContact != null) {
            tvUntaggedContacts.setText("( " + allUniqueCallsWithoutContact.size() + " )");
        } else {
            tvUntaggedContacts.setText("( " + 0 + " )");
        }
        if (allContactsAsProspects != null) {
            tvPendingProspectValue.setText("( " + allContactsAsProspects.size() + " )");
        } else {
            tvPendingProspectValue.setText("( " + 0 + " )");
        }
        if (allInactiveLeads != null) {
            tvInactiveLeadsValue.setText("( " + allContactsAsProspects.size() + " )");
        } else {
            tvInactiveLeadsValue.setText("( " + 0 + " )");
        }
        return view;
    }
}