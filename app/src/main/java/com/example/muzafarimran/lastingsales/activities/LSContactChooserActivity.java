package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ImportContactsListAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;

public class LSContactChooserActivity extends AppCompatActivity {
    public static ArrayList<LSContact> salesContacts;
    ListView listView = null;

    public static ArrayList<LSContact> getAllContacts() {
        salesContacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);
        return salesContacts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lscontact_chooser);
        listView = (ListView) findViewById(R.id.contacts_list_contact_chooser);

        ImportContactsListAdapter2 importContactsListAdapter = new ImportContactsListAdapter2(getApplicationContext(), R.layout.import_contact_list_item, getAllContacts(), this);
        listView.setAdapter(importContactsListAdapter);
    }
}