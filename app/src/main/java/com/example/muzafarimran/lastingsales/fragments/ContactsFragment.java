package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.ContactsTabsFragmentPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */

@Deprecated
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
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
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
        ContactsTabsFragmentPagerAdapter adp = new ContactsTabsFragmentPagerAdapter(getChildFragmentManager());
        vpConatcs.setAdapter(adp);
        tabs.setupWithViewPager(vpConatcs);
    }

    /*
    * event handler for click on add contact cta
    * */
    public class showAddContactForm implements View.OnClickListener {

        @Override
        public void onClick(View v) {
//            Intent myIntent = new Intent(getActivity(), TagNumberAndAddFollowupActivity.class);
//            myIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE , TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
//            getActivity().startActivity(myIntent);
        }
    }
}