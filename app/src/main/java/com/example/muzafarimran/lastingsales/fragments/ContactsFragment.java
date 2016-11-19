package com.example.muzafarimran.lastingsales.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.activities.AddContactActivity;
import com.example.muzafarimran.lastingsales.activities.ContactCallDetails;
import com.example.muzafarimran.lastingsales.adapters.ContactssTabsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.providers.models.Contact;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends TabFragment {

    CoordinatorLayout mainLayout;
    TabLayout tabs;
    ViewPager vpConatcs;
    showAddContactForm showaddcontactform = new showAddContactForm();

    FloatingActionButton addContactCta = null;

    public ContactsFragment() {
    }


    @Override
    public void onResume() {
        /*Toast.makeText(getActivity(), "Resume",
                Toast.LENGTH_LONG).show();*/
        //setUpPager();
        super.onResume();
    }

    @Override
    public void onPause() {
       /* Toast.makeText(getActivity(), "Pause",
                Toast.LENGTH_LONG).show();*/
        super.onPause();
    }


    @Override
    public void onStop() {

        /*Toast.makeText(getActivity(), "stop",
                Toast.LENGTH_LONG).show();*/
        super.onStop();
    }

    @Override
    public void onDestroyView() {

       /* Toast.makeText(getActivity(), "destroy view",
                Toast.LENGTH_LONG).show();*/
        super.onDestroyView();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.mainLayout = (CoordinatorLayout) inflater.inflate(R.layout.fragment_contacts, container, false);
        this.addContactCta = (FloatingActionButton) mainLayout.findViewById(R.id.add_contact_cta);

        this.addContactCta.setOnClickListener(this.showaddcontactform);
        this.tabs = (TabLayout) mainLayout.findViewById(R.id.contacts_types);
        this.vpConatcs = (ViewPager) mainLayout.findViewById(R.id.vp_contacts_types);

        setUpPager();

        return this.mainLayout;
    }

    private void setUpPager() {

//        SecondLevelFragmentPagerAdapter adp = new SecondLevelFragmentPagerAdapter(getChildFragmentManager(), getActivity());
        ContactssTabsFragmentPagerAdapter adp = new ContactssTabsFragmentPagerAdapter(getChildFragmentManager());
/*
        SalesFragment sc = new SalesFragment(); //sales contacts fragment
        CollegueFragment cc = new CollegueFragment();
        NonbusinessFragment pc = new NonbusinessFragment();
        UntaggedFragment uc = new UntaggedFragment();


        List<Contact> salesContacts = new ArrayList<>();
        List<Contact> nonbusinessContacts = new ArrayList<>();
        List<Contact> colleagueContacts = new ArrayList<>();
        List<Contact> untaggedContacts = new ArrayList<>();

        // Dummy Data
        salesContacts.add(new Contact("Prospects", null, "separator", null, null, null, null, null, null));
        salesContacts.add(new Contact("Kashif Naeem", "kashif@haditelecom.com", "sales", "0301-3839383", null, null, null, null, "prospect"));
        salesContacts.add(new Contact("Salman Bukhari", "sbukhari828@gmail.com", "sales", "0323-4433108", null, null, null, null, "prospect"));

        salesContacts.add(new Contact("Leads", null, "separator", null, null, null, null, null, null));
        salesContacts.add(new Contact("Raza Ahmad", "sra0nasir@gmail.com", "sales", "0332-5404943", null, null, null, null, "lead"));
//TODO: add them later on
//        salesContacts.add(new Contact("Closed - Won", null, "separator", null, null, null, null, null, null));
//        salesContacts.add(new Contact("Grit Denker", "grit_denker1@gmail.com", "sales", "408-898-9146", null, null, null, null, "closed-won"));
//        salesContacts.add(new Contact("Miryung Kim", "miryung_kim@gmail.com", "sales", "446-324-1779", null, null, null, null, "closed-won"));
//
//        salesContacts.add(new Contact("Closed-Lost", null, "separator", null, null, null, null, null, null));
//        salesContacts.add(new Contact("Ken Adams", "ken_adams@hotmail.com", "sales", "418-198-9196", null, null, null, null, "closed-lost"));
//        salesContacts.add(new Contact("Phoebe Buffay", "phoebe_buffay@gmail.com", "sales", "111-3130-1119", null, null, null, null, "closed-lost"));

        nonbusinessContacts.add(new Contact("Malcom X", "malcolmx@yahoo.com", "nonbusiness", "650-540-9865", null, null, null, null, null));
        nonbusinessContacts.add(new Contact("Courtney Cox", "courtney_cox@live.com", "nonbusiness", "0332-5404943", null, null, null, null, null));

        colleagueContacts.add(new Contact("John Snow", "johnsnow@yahoo.com", "colleague", "546-654-7135", null, null, null, null, null));
        colleagueContacts.add(new Contact("Alastar Cook", "alastar_cook@ymail.com", "colleague", "615-736-5445", null, null, null, null, null));

        untaggedContacts.add(new Contact("Rachel Greene", "rachel_greene@gmail.com", "untagged", "654-857-9332", null, null, null, null, null));
        untaggedContacts.add(new Contact("Ted Mosby", "tedmosby1@yahoo.com", "untagged", "141-785-1233", null, null, null, null, null));
        untaggedContacts.add(new Contact("Garfield Sobers", "garysobers@gmail.com", "untagged", "691-337-1285", null, null, null, null, null));


        ArrayList<LSContact> allContacts = (ArrayList<LSContact>) LSContact.listAll(LSContact.class);


        ArrayList<LSContact> listOfSalesContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_SALES);
        ArrayList<LSContact> listOfColleagueContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        ArrayList<LSContact> listOfPersonalContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_PERSONAL);


        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        sc.setList(listOfSalesContacts);
        cc.setList(listOfColleagueContacts);
        pc.setList(listOfPersonalContacts);
        uc.setList(allContacts);
//
//        //tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
//        sc.setList(salesContacts);
//        cc.setList(colleagueContacts);
//        pc.setList(nonbusinessContacts);
//        uc.setList(untaggedContacts);

        adp.addFrag(sc, "SALES");
        adp.addFrag(cc, "COLLEAGUES");
        adp.addFrag(pc, "NON-BUSINESS");
        adp.addFrag(uc, "UNTAGGED");

      */  vpConatcs.setAdapter(adp);
        tabs.setupWithViewPager(vpConatcs);
    }

    /*
    * event handler for click on add contact cta
    * */
    public class showAddContactForm implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(getActivity(), AddContactActivity.class);
            //myIntent.putExtra("number",(String) v.getTag());
            getActivity().startActivity(myIntent);
        }

    }


}
