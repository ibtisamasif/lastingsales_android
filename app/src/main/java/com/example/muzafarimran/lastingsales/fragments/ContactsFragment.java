package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.example.muzafarimran.lastingsales.Contact;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.SecondLevelFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends TabFragment {

    LinearLayout mainLayout;
    TabLayout tabs;
    ViewPager vpConatcs;

    public ContactsFragment() {}



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainLayout = (LinearLayout) inflater.inflate(R.layout.fragment_contacts, container, false);
        tabs = (TabLayout) mainLayout.findViewById(R.id.contacts_types);
        vpConatcs = (ViewPager) mainLayout.findViewById(R.id.vp_contacts_types);
        setUpPager();


        /*View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        ListView listView = (ListView) view.findViewById(R.id.contacts_list);


        ContactsAdapter adapter = new ContactsAdapter(getContext(),contacts);
        listView.setAdapter(adapter);*/

        return mainLayout;
    }

    private void setUpPager() {

        SecondLevelFragmentPagerAdapter adp = new SecondLevelFragmentPagerAdapter(getChildFragmentManager(), getActivity());


        SalesFragment sc    = new SalesFragment(); //sales contacts fragment
        CollegueFragment cc = new CollegueFragment();
        PersonalFragment pc = new PersonalFragment();
        UntaggedFragment uc = new UntaggedFragment();


        List<Contact> salesContacts = new ArrayList<>();
        List<Contact> personalContacts = new ArrayList<>();
        List<Contact> collegueContacts = new ArrayList<>();
        List<Contact> untaggedContacts = new ArrayList<>();

        //outgoingCalls.add(new Call("Salman Bukhari", "0323-4433108", "missed", "1 min ago"));

        salesContacts.add(new Contact("Prospects", null, "seperator", null, null, null, null));
        salesContacts.add(new Contact("Kashif Naeem", "03xx-yyzzxxx", "prospect", "20","10", "20 mins", "2 days ago"));
        salesContacts.add(new Contact("Salman Bukhari", "0323-4433108", "prospect", "30","40", "40 mins", "1 days ago"));
        salesContacts.add(new Contact("Leads", null, "seperator", null, null, null, null));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        salesContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));

        personalContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        personalContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        personalContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        personalContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        personalContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));

        collegueContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        collegueContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        collegueContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        collegueContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        collegueContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));

        untaggedContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        untaggedContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        untaggedContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        untaggedContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        untaggedContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        untaggedContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));
        untaggedContacts.add(new Contact("Raza Ahmad", "0332-5404943", "lead", "10","60", "30 mins", "5 days ago"));

        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        sc.setList(salesContacts);
        cc.setList(collegueContacts);
        pc.setList(personalContacts);
        uc.setList(untaggedContacts);

        adp.addFrag(sc, "SALES");
        adp.addFrag(cc, "COLLEGUES");
        adp.addFrag(pc, "PERSONAL");
        adp.addFrag(uc, "UNTAGGED");

        vpConatcs.setAdapter(adp);
        tabs.setupWithViewPager(vpConatcs);
    }



}
