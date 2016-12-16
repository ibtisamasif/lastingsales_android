package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.FrameActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends TabFragment {
    private static final String TAG = "HomeFragment";
    private TextView tvUntaggedContacts;
    private TextView tvPendingProspectValue;
    private TextView tvInactiveLeadsValue;
    private LinearLayout llInActiveLeadsContainer;
    private LinearLayout llUntaggedContainer;
    private LinearLayout llPendingProspectsContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvUntaggedContacts = (TextView) view.findViewById(R.id.untagged_contacts_val);
        tvPendingProspectValue = (TextView) view.findViewById(R.id.tvPendingProspectValue);
        tvInactiveLeadsValue = (TextView) view.findViewById(R.id.tvInactiveLeadsValue);
        llInActiveLeadsContainer = (LinearLayout) view.findViewById(R.id.llInActiveLeadsContactsContainer);
        llUntaggedContainer = (LinearLayout) view.findViewById(R.id.llUntaggedContactsContainer);
        llPendingProspectsContainer = (LinearLayout) view.findViewById(R.id.llPendingProspectsContactsContainer);
        ArrayList<LSCall> allUniqueCallsWithoutContact = LSCall.getUniqueCallsWithoutContacts();
        ArrayList<LSContact> allPendingProspects = (ArrayList<LSContact>) LSContact.getAllPendingProspectsContacts();
        ArrayList<LSContact> allInactiveLeads = (ArrayList<LSContact>) LSContact.getAllInactiveLeadContacts();
        if (allInactiveLeads != null) {
            tvInactiveLeadsValue.setText("( " + allInactiveLeads.size() + " )");
        } else {
            tvInactiveLeadsValue.setText("( " + 0 + " )");
        }

        if (allUniqueCallsWithoutContact != null) {
            tvUntaggedContacts.setText("( " + allUniqueCallsWithoutContact.size() + " )");
        } else {
            tvUntaggedContacts.setText("( " + 0 + " )");
        }

        if (allPendingProspects != null) {
            tvPendingProspectValue.setText("( " + allPendingProspects.size() + " )");
            Log.d(TAG, "PendingPros : "+allPendingProspects.size());
        } else {
            tvPendingProspectValue.setText("( " + 0 + " )");
        }
        llInActiveLeadsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsListFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "InActive Leads");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
                intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        llUntaggedContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsListFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Untagged Contacts");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
                intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        llPendingProspectsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsListFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Pending Prospects");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
                intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }
}