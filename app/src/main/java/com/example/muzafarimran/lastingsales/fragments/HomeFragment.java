package com.example.muzafarimran.lastingsales.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddLeadActivity;
import com.example.muzafarimran.lastingsales.activities.FrameActivity;
import com.example.muzafarimran.lastingsales.events.ContactTaggedFromUntaggedContactEventModel;
import com.example.muzafarimran.lastingsales.events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.listeners.TabSelectedListener;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends TabFragment {
    private static final String TAG = "HomeFragment";
    private TextView tvInquiriesValue;
    private TextView tvInactiveLeadsValue;
    private TextView tvUntaggedContacts;
    private LinearLayout llInActiveLeadsContainer;
    private LinearLayout llUntaggedContainer;
    private LinearLayout llinquriesContainer;
    //    private LinearLayout llshadow1;
//    private LinearLayout llshadow2;
//    private LinearLayout llshadow3;
    private LinearLayout llshadow4;
    private FrameLayout followupsListHolderFrameLayout;
    private FollowupsTodayListFragment followupsTodayListFragment;
    private FloatingActionButton floatingActionButtonAdd, floatingActionButtonImport;
    private FloatingActionMenu floatingActionMenu;

    private TinyBus bus;
//    public ViewPager mViewPager, vpLeads;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvInquiriesValue = (TextView) view.findViewById(R.id.tvInquriesValue);
        tvUntaggedContacts = (TextView) view.findViewById(R.id.tvUntaggeContactsVal);
        tvInactiveLeadsValue = (TextView) view.findViewById(R.id.tvInactiveLeadsValue);

        llinquriesContainer = (LinearLayout) view.findViewById(R.id.llinquriesContainer);
//        llshadow1 = (LinearLayout) view.findViewById(R.id.llshadow1);
        llUntaggedContainer = (LinearLayout) view.findViewById(R.id.llUntaggedContactsContainer);
//        llshadow2 = (LinearLayout) view.findViewById(R.id.llshadow2);
        llInActiveLeadsContainer = (LinearLayout) view.findViewById(R.id.llInActiveLeadsContactsContainer);
//        llshadow3 = (LinearLayout) view.findViewById(R.id.llshadow3);
        llshadow4 = (LinearLayout) view.findViewById(R.id.llshadow4);

        followupsListHolderFrameLayout = (FrameLayout) view.findViewById(R.id.followupsListHolderFrameLayout);

        updateHomeFigures();

        llInActiveLeadsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = 2;
                ((TabSelectedListener) getActivity()).onTabSelectedEvent(position, "InActiveLeads");
//                Intent intent;
//                Bundle bundle = new Bundle();
//                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, InActiveLeadsFragment.class.getName()); // Change
//                bundle.putString(FrameActivity.ACTIVITY_TITLE, "InActive Leads");
//                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, true);
//                intent = new Intent(getContext(), FrameActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
        llUntaggedContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, UntaggedContactsCallsFragment.class.getName());
                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Unlabeled Contacts");
                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
                intent = new Intent(getContext(), FrameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        llinquriesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = 0;
                ((TabSelectedListener) getActivity()).onTabSelectedEvent(position, "Inquiries");
//                mViewPager.setCurrentItem(1,true);
//                Bundle bundle = new Bundle();
//                bundle.putString(FrameActivity.FRAGMENT_NAME_STRING, FollowupsTodayListFragment.class.getName());
//                bundle.putString(FrameActivity.ACTIVITY_TITLE, "Followups Today");
//                bundle.putBoolean(FrameActivity.INFLATE_OPTIONS_MENU, false);
//                Intent intent = new Intent(getContext(), FrameActivity.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });


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

//                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
//                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
//                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_SALES);
//                startActivity(intent);
            }
        });
        floatingActionButtonImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getContext(), AddLeadActivity.class);
                intent.putExtra(AddLeadActivity.ACTIVITY_LAUNCH_MODE, AddLeadActivity.LAUNCH_MODE_IMPORT_CONTACT);
                startActivity(intent);
//                Intent intent = new Intent(getContext(), TagNumberAndAddFollowupActivity.class);
//                intent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_IMPORT_CONTACT);
//                intent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_TYPE , LSContact.CONTACT_TYPE_SALES);
//                startActivity(intent);

            }
        });
        //Bundle bundle = new Bundle();
        //bundle.putString(NotesFragmentNew.CONTACT_ID, selectedContact.getId().toString());
        followupsTodayListFragment = new FollowupsTodayListFragment();
        //followupsTodayListFragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.followupsListHolderFrameLayout, followupsTodayListFragment);
        transaction.commit();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHomeFigures();
    }

    private void updateHomeFigures() {
        ArrayList<LSContact> allUntaggedContacts = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_UNTAGGED);
        ArrayList<LSContact> allCollegues = (ArrayList<LSContact>) LSContact.getContactsByType(LSContact.CONTACT_TYPE_COLLEAGUE);
        ArrayList<LSContact> allFilteredContactsAsProspects = (ArrayList<LSContact>) LSContact.getContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        allFilteredContactsAsProspects.removeAll(allCollegues);
        ArrayList<LSContact> allInactiveLeads = (ArrayList<LSContact>) LSContact.getAllInactiveLeadContacts();

        List<LSInquiry> allInquiries = LSInquiry.getAllInquiriesInDescendingOrder();

        if (allInquiries != null) {
            if (allInquiries.size() > 0) {
                llinquriesContainer.setVisibility(View.VISIBLE);
                tvInquiriesValue.setText("" + allInquiries.size());
            } else {
                llinquriesContainer.setVisibility(View.GONE);
//                llshadow1.setVisibility(View.GONE);
            }
        } else {
            tvInquiriesValue.setText(0);
        }

        if (allUntaggedContacts != null) {
            if (allUntaggedContacts.size() > 0) {
                llUntaggedContainer.setVisibility(View.VISIBLE);
                tvUntaggedContacts.setText("" + allUntaggedContacts.size());
            } else {
                llUntaggedContainer.setVisibility(View.GONE);
//                llshadow2.setVisibility(View.GONE);
            }
        } else {
            tvUntaggedContacts.setText(0);
        }

        if (allInactiveLeads != null) {
            if (allInactiveLeads.size() > 0) {
                llInActiveLeadsContainer.setVisibility(View.VISIBLE);
                tvInactiveLeadsValue.setText("" + allInactiveLeads.size());
            } else {
                llInActiveLeadsContainer.setVisibility(View.GONE);
//                llshadow3.setVisibility(View.GONE);
            }
        } else {
            tvInactiveLeadsValue.setText(0);
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
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
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