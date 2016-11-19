package com.example.muzafarimran.lastingsales.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.adapters.ContactsAdapter;
import com.example.muzafarimran.lastingsales.adapters.ImportContactsListAdapter;
import com.example.muzafarimran.lastingsales.adapters.ImportContactsListAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;

public class LSContactChooserActivity extends AppCompatActivity {
    ListView listView = null;
    public static ArrayList<LSContact> salesContacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lscontact_chooser);
        listView = (ListView) findViewById(R.id.contacts_list_contact_chooser);

        ImportContactsListAdapter2 importContactsListAdapter = new ImportContactsListAdapter2(getApplicationContext(), R.layout.import_contact_list_item, getAllContacts(), this);
        listView.setAdapter(importContactsListAdapter);
    }


    public static ArrayList<LSContact> getAllContacts() {
     /*   salesContacts = new ArrayList<>();
        // Dummy Data
        salesContacts.add(new Contact("Kashif Naeem", "kashif@haditelecom.com", "sales", "0301-3839383", null, null, null, null, "prospect"));
        salesContacts.add(new Contact("Salman Bukhari", "sbukhari828@gmail.com", "sales", "0323-4433108", null, null, null, null, "prospect"));
        salesContacts.add(new Contact("Shoaib Gondal", "shoaib.gondal@gmail.com", "sales", "0323-4433444", null, null, null, null, "prospect"));
        salesContacts.add(new Contact("Raza Ahmad", "sra0nasir@gmail.com", "sales", "0332-5404943", null, null, null, null, "lead"));
*/


        salesContacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);


        return salesContacts;
    }
}