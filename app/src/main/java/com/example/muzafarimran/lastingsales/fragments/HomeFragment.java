package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.FrameActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends TabFragment {

    private TextView tvUntaggedContacts;
    private TextView tvPendingProspectValue;
    private TextView tvInactiveLeadsValue;
    private TextView tvFollowupsDue;
    private TextView tvFollowupsDone;
    private LinearLayout llUntaggedContainer;
    private LinearLayout llFollowupsTodayContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvUntaggedContacts = (TextView) view.findViewById(R.id.untagged_contacts_val);
        tvPendingProspectValue = (TextView) view.findViewById(R.id.tvPendingProspectValue);
        tvInactiveLeadsValue = (TextView) view.findViewById(R.id.tvInactiveLeadsValue);
        tvFollowupsDue = (TextView) view.findViewById(R.id.tvFollowUpsDue);
        tvFollowupsDone = (TextView) view.findViewById(R.id.tvFollowupsDone);
        llUntaggedContainer = (LinearLayout) view.findViewById(R.id.llUntaggedContactsContainer);
        llFollowupsTodayContainer = (LinearLayout) view.findViewById(R.id.llFollowupsTodayContainer);
        ArrayList<LSCall> allUniqueCallsWithoutContact = LSCall.getUniqueCallsWithoutContacts();
        ArrayList<LSContact> allContactsAsProspects = (ArrayList<LSContact>) LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        ArrayList<LSContact> allInactiveLeads = (ArrayList<LSContact>) LSContact.getAllInactiveLeadContacts();
        if (allUniqueCallsWithoutContact != null) {
            tvUntaggedContacts.setText("( " + allUniqueCallsWithoutContact.size() + " )");
        } else {
            tvUntaggedContacts.setText("( " + 0 + " )");
        }
        if (allContactsAsProspects != null) {
            tvPendingProspectValue.setText("( " + allContactsAsProspects.size() + " )");
        } else {
            tvPendingProspectValue.setText("( " + 0 + " )");
        }
        if (allInactiveLeads != null) {
            tvInactiveLeadsValue.setText("( " + allContactsAsProspects.size() + " )");
        } else {
            tvInactiveLeadsValue.setText("( " + 0 + " )");
        }
        ArrayList<TempFollowUp> allFollowUps = (ArrayList<TempFollowUp>) TempFollowUp.listAll(TempFollowUp.class);
        Calendar now = Calendar.getInstance();
        Calendar beginingOfToday = Calendar.getInstance();
        beginingOfToday.set(Calendar.HOUR_OF_DAY, 0);
        beginingOfToday.set(Calendar.MINUTE, 0);
        Calendar endOfToday = Calendar.getInstance();
        endOfToday.add(Calendar.DAY_OF_MONTH, 1);
        endOfToday.set(Calendar.HOUR_OF_DAY, 0);
        endOfToday.set(Calendar.MINUTE, 0);
        ArrayList<TempFollowUp> followupsInToday = new ArrayList<>();
        for (TempFollowUp oneFollowup : allFollowUps) {
            if (oneFollowup.getDateTimeForFollowup() > beginingOfToday.getTimeInMillis() && oneFollowup.getDateTimeForFollowup() < endOfToday.getTimeInMillis()) {
                followupsInToday.add(oneFollowup);
            }
        }
        ArrayList<TempFollowUp> followupsDue = new ArrayList<>();
        ArrayList<TempFollowUp> followUpsDone = new ArrayList<>();
        for (TempFollowUp oneFollowup : followupsInToday) {
            if (oneFollowup.getDateTimeForFollowup() < now.getTimeInMillis()) {
                followUpsDone.add(oneFollowup);
            } else {
                followupsDue.add(oneFollowup);
            }
        }
        tvFollowupsDone.setText("( " + followUpsDone.size() + " )");
        tvFollowupsDue.setText("( " + followupsDue.size() + " )");
        llUntaggedContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, UntaggedContactsCallsFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Untagged Contacts");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
                intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        llFollowupsTodayContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsTodayListFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Followups List");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
                Intent intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return view;
    }
}