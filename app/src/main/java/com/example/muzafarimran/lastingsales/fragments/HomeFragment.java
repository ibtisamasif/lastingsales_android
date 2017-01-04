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

import com.example.muzafarimran.lastingsales.Events.ContactTaggedFromUntaggedContactEventModel;
import com.example.muzafarimran.lastingsales.Events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.Events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.FrameActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends TabFragment {
    private static final String TAG = "HomeFragment";
    private TextView tvInactiveLeadsValue;
    private TextView tvUntaggedContacts;
    private TextView tvPendingProspectValue;
    private LinearLayout llInActiveLeadsContainer;
    private LinearLayout llUntaggedContainer;
    private LinearLayout llinquriesContainer;
    private LinearLayout llPendingProspectsContainer;
    private Bus mBus;
    private TinyBus bus;

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
        llUntaggedContainer = (LinearLayout) view.findViewById(R.id.llUntaggedContactsContainer);
        llinquriesContainer = (LinearLayout) view.findViewById(R.id.llinquriesContainer);
        updateHomeFigures();
        
        llInActiveLeadsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, InActiveLeadsFragment.class.getName()); // Change
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
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, UntaggedContactsCallsFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Untagged Contacts");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
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
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, PendingProspectsFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Pending Prospects");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
                intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        llinquriesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsTodayListFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Followups Today");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
                Intent intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        return view;
    }

    private void updateHomeFigures() {
        ArrayList<LSContact> allUntaggedContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_UNTAGGED);
        ArrayList<LSContact> allCollegues = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        ArrayList<LSContact> allFilteredContactsAsProspects = (ArrayList<LSContact>) LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_PROSTPECT);
        allFilteredContactsAsProspects.removeAll(allCollegues);
        ArrayList<LSContact> allInactiveLeads = (ArrayList<LSContact>) LSContact.getAllInactiveLeadContacts();

        if (allInactiveLeads != null) {
            tvInactiveLeadsValue.setText("( " + allInactiveLeads.size() + " )");
        } else {
            tvInactiveLeadsValue.setText("( " + 0 + " )");
        }
        if (allUntaggedContacts != null) {
            tvUntaggedContacts.setText("( " + allUntaggedContacts.size() + " )");
        } else {
            tvUntaggedContacts.setText("( " + 0 + " )");
        }
        if (allFilteredContactsAsProspects != null) {
            tvPendingProspectValue.setText("( " + allFilteredContactsAsProspects.size() + " )");
        } else {
            tvPendingProspectValue.setText("( " + 0 + " )");
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
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onUntaggedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            updateHomeFigures();
        }
    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {
            updateHomeFigures();
        }
    }

    @Subscribe
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onOutgoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            updateHomeFigures();
        }
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }
    @Subscribe
    public void onContactTaggedFromUntaggedContactEventModel(ContactTaggedFromUntaggedContactEventModel event) {
        Log.d(TAG, "onNoteAddedEvent() called with: event = [" + event + "]");
        updateHomeFigures();
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();

    }

}