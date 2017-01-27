package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddLeadActivity;
import com.example.muzafarimran.lastingsales.adapters.LeadsTabsFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.events.LeadContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.listeners.TabSelectedListener;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/29/2016.
 */

public class LeadTabsFragment extends TabFragment implements TabSelectedListener {
    private static final String TAG = "LeadTabsFragment";
    FloatingActionButton floatingActionButtonAdd, floatingActionButtonImport;
    FloatingActionMenu floatingActionMenu;
    private TabLayout tabs;
    private ViewPager vpLeads;
    private Bus mBus;
    private TinyBus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leads_tabs, container, false);
        tabs = (TabLayout) view.findViewById(R.id.lead_types);
        vpLeads = (ViewPager) view.findViewById(R.id.vp_leads_types);
        LeadsTabsFragmentPagerAdapter adp = new LeadsTabsFragmentPagerAdapter(getChildFragmentManager());
        vpLeads.setAdapter(adp);
        tabs.setupWithViewPager(vpLeads);

        updateTabFigues();

        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_add);
        floatingActionButtonImport = (FloatingActionButton) view.findViewById(R.id.material_design_floating_action_menu_import);
        floatingActionMenu.setClosedOnTouchOutside(true);

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), AddLeadActivity.class);
                intent.putExtra(AddLeadActivity.ACTIVITY_LAUNCH_MODE, AddLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddLeadActivity.class);
                intent.putExtra(AddLeadActivity.ACTIVITY_LAUNCH_MODE, AddLeadActivity.LAUNCH_MODE_IMPORT_CONTACT);
                startActivity(intent);

            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateTabFigues();
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onStop() {

        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    @Override
    public void onTabSelectedEvent(int position, String tag) {
        vpLeads.setCurrentItem(position);
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(LeadContactDeletedEventModel event) {
        Log.d(TAG, "LeadContactDeletedEventModel() called with: event = [" + event + "]");
        updateTabFigues();
    }

    private void updateTabFigues() {
        //List Taken To Filter out Colleagues
        ArrayList<LSContact> allCollegues = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        //Prospects List
        ArrayList<LSContact> allProspects = (ArrayList<LSContact>) LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        allProspects.removeAll(allCollegues);
        //Leads List
        List<LSContact> allLeads = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_LEAD);
        allLeads.removeAll(allCollegues);
        //All Won
        List<LSContact> allWon = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        allWon.removeAll(allCollegues);
        //All Lost
        List<LSContact> allLost = LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
        allLost.removeAll(allCollegues);
        //InActive Leads List
        ArrayList<LSContact> allInactiveLeads = (ArrayList<LSContact>) LSContact.getAllInactiveLeadContacts();
        if (allProspects != null) {
            if (allProspects.size() > 0) {
                tabs.getTabAt(0).setText("Prospects" + "(" + allProspects.size() + ")");
            } else {
                tabs.getTabAt(0).setText("Prospects" + "(" + 0 + ")");
            }
        }
        if (allLeads != null) {
            if (allLeads.size() > 0) {
                tabs.getTabAt(1).setText("Leads" + "(" + allLeads.size() + ")");
            } else {
                tabs.getTabAt(1).setText("Leads" + "(" + 0 + ")");
            }
        }
        if (allWon != null) {
            if (allWon.size() > 0) {
                tabs.getTabAt(2).setText("Won" + "(" + allWon.size() + ")");
            } else {
                tabs.getTabAt(2).setText("Won" + "(" + 0 + ")");
            }
        }
        if (allLost != null) {
            if (allLost.size() > 0) {
                tabs.getTabAt(3).setText("Lost" + "(" + allLost.size() + ")");
            } else {
                tabs.getTabAt(3).setText("Lost" + "(" + 0 + ")");
            }
        }
        if (allInactiveLeads != null) {
            if (allInactiveLeads.size() > 0) {
                tabs.getTabAt(4).setText("InActive" + "(" + allInactiveLeads.size() + ")");
            } else {
                tabs.getTabAt(4).setText("InActive" + "(" + 0 + ")");
            }
        }
    }
}