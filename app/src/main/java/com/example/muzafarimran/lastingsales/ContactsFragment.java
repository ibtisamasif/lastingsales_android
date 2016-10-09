package com.example.muzafarimran.lastingsales;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends TabFragment {




    ArrayList<Contact> contacts = new ArrayList<Contact>()
    {
        {
            add(new Contact("Prospects", null, "seperator", null, null, null, null));
            add(new Contact("Kashif Naeem", "03xx-yyzzxxx", "prospect", "20","10", "20 mins", "2 days ago"));
            add(new Contact("Salman Bukhari", "0323-4433108", "prospect", "30","40", "40 mins", "1 days ago"));
            add(new Contact("Leads", null, "seperator", null, null, null, null));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));


        }
    };

    public ContactsFragment() {


    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ListView listView = (ListView) view.findViewById(R.id.contacts_list);


        ContactsAdapter adapter = new ContactsAdapter(getContext(),contacts);
        listView.setAdapter(adapter);

        return view;
    }

}
