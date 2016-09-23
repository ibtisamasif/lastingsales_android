package com.example.muzafarimran.lastingsales;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends TabFragment {




    ArrayList<Contact> contacts = new ArrayList<Contact>()
    {
        {
            add(new Contact("Prospects", null, "seperator"));
            add(new Contact("Kashif Naeem", "03xx-yyzzxxx", "prospect"));
            add(new Contact("Salman Bukhari", "0323-4433108", "prospect"));
            add(new Contact("Leads", null, "seperator"));
            add(new Contact("Raza Ahmad", "0332-5404943", "lead"));
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
