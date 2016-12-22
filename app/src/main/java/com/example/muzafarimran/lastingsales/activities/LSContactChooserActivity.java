package com.example.muzafarimran.lastingsales.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ImportContactsListAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class LSContactChooserActivity extends AppCompatActivity  {
    public static final String LAUNCH_MODE_ALL_CONTACTS = "launch_mode_all_contacts";
    public static final String LAUNCH_MODE_CONTACTS_COLLEGUES_AND_SALES = "collegues_and_sales";
    public static final String LAUNCH_MODE_CONTACTS_NOT_HAVING_NOTES = "not_having_notes";
    public static final String CONTACT_TYPE_TO_DISPLAY = "contact_type";
    public static ArrayList<LSContact> salesAndColleguesContacts;
    ListView listView = null;
    boolean inflateOptionsMenu = true;
    String launchMode = LAUNCH_MODE_ALL_CONTACTS;
    MaterialSearchView searchView;

    public static ArrayList<LSContact> getAllContacts() {
        salesAndColleguesContacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);
        return salesAndColleguesContacts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lscontact_chooser);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Select Contact");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.contacts_list_contact_chooser);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launchMode = bundle.getString(CONTACT_TYPE_TO_DISPLAY);
        }
        if (launchMode.equals(LAUNCH_MODE_CONTACTS_NOT_HAVING_NOTES)) {
            ImportContactsListAdapter2 importContactsListAdapter = new ImportContactsListAdapter2(getApplicationContext(), R.layout.import_contact_list_item, (ArrayList<LSContact>) LSContact.getAllContactsNotHavingNotes(), this);
            listView.setAdapter(importContactsListAdapter);
        } else if (launchMode.equals(LAUNCH_MODE_CONTACTS_COLLEGUES_AND_SALES)) {
            ImportContactsListAdapter2 importContactsListAdapter = new ImportContactsListAdapter2(getApplicationContext(), R.layout.import_contact_list_item, (ArrayList<LSContact>) LSContact.getSalesAndColleguesContacts(), this);
            listView.setAdapter(importContactsListAdapter);
        } else {
            ImportContactsListAdapter2 importContactsListAdapter = new ImportContactsListAdapter2(getApplicationContext(), R.layout.import_contact_list_item, getAllContacts(), this);
            listView.setAdapter(importContactsListAdapter);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(this, "Searching..", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}