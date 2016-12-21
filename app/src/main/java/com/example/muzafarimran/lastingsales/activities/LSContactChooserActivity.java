package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ImportContactsListAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;

public class LSContactChooserActivity extends AppCompatActivity {
    public static final String ACTIVITY_LAUNCH_MODE = "activity_launch_mode";

    public static final String LAUNCH_MODE_ALL_CONTACTS = "launch_mode_all_contacts";
    public static final String LAUNCH_MODE_COLLEGUES_AND_SALES = "launch_mode_collegues_and_sales";
    public static final String CONTACT_TYPE_TO_DISPLAY = "contact_type";
    public static ArrayList<LSContact> salesAndColleguesContacts;
    ListView listView = null;

    String launchMode = LAUNCH_MODE_ALL_CONTACTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lscontact_chooser);
        listView = (ListView) findViewById(R.id.contacts_list_contact_chooser);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getString(ACTIVITY_LAUNCH_MODE);
        }
        if (launchMode.equals(LAUNCH_MODE_COLLEGUES_AND_SALES)) {
            ImportContactsListAdapter2 importContactsListAdapter = new ImportContactsListAdapter2(getApplicationContext(), R.layout.import_contact_list_item, (ArrayList<LSContact>) LSContact.getSalesAndColleguesContacts(), this);
            listView.setAdapter(importContactsListAdapter);
        } else {
            ImportContactsListAdapter2 importContactsListAdapter = new ImportContactsListAdapter2(getApplicationContext(), R.layout.import_contact_list_item, getAllContacts(), this);
            listView.setAdapter(importContactsListAdapter);
        }
    }

    public static ArrayList<LSContact> getAllContacts () {
        salesAndColleguesContacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);
        return salesAndColleguesContacts;
    }
}