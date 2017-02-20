package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.FollowupsTodayListAdapter;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class FollowupsTodayListFragment extends TabFragment {
    public static final String TAG = "FollowupsTodayListFragment";
    FollowupsTodayListAdapter followupsAdapter;
    ExpandableStickyListHeadersListView listView = null;
    ArrayList<TempFollowUp> DueFollowups;
    ArrayList<TempFollowUp> DoneFollowups;
    Long selectedContactID = 0l;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            selectedContactID = bundle.getLong("contactID");
        }
//        if(selectedContactID != null){
//            Toast.makeText(getActivity(), "Not NUll "+selectedContactID, Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(getActivity(), "NUll " +selectedContactID, Toast.LENGTH_SHORT).show();
//        }
        ArrayList<TempFollowUp> followUps = getFollowupsOFToday();
        followupsAdapter = new FollowupsTodayListAdapter(getContext(),followUps);
        setHasOptionsMenu(true);
    }
    public void setList(List<TempFollowUp> followUps) {
        if (followupsAdapter != null) {
            followupsAdapter.setList(followUps);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
//        if(selectedContactID != null){
//            Toast.makeText(getActivity(), "NotNUll", Toast.LENGTH_SHORT).show();
//            ArrayList<TempFollowUp> followUps = getFollowupsOFToday();
//            setList(followUps);
//        }
//        else {
            ArrayList<TempFollowUp> followUps = getFollowupsOFToday();
            setList(followUps);
//        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followups_today_list, container, false);
        listView = (ExpandableStickyListHeadersListView) view.findViewById(R.id.followups_list_view_in_fragment);
        listView.setAdapter(followupsAdapter);
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
                if (listView.isHeaderCollapsed(headerId)) {
                    listView.expand(headerId);
                } else {
                    listView.collapse(headerId);
                }
            }
        });
        return view;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public ArrayList<TempFollowUp> getFollowupsOFToday()
    {
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
        ArrayList<TempFollowUp> dueFollowups = new ArrayList<>();
        for (TempFollowUp oneFollowup : followupsInToday) {
            if (oneFollowup.getDateTimeForFollowup() > now.getTimeInMillis()) {
                dueFollowups.add(oneFollowup);
            }
        }
        ArrayList<TempFollowUp> doneFollowups = new ArrayList<>();
        for (TempFollowUp oneFollowup : followupsInToday) {
            if (oneFollowup.getDateTimeForFollowup() < now.getTimeInMillis()) {
                doneFollowups.add(oneFollowup);
            }
        }
        Collections.sort(dueFollowups, new Comparator<TempFollowUp>() {
            @Override
            public int compare(TempFollowUp tempFollowUp, TempFollowUp t1) {
                return tempFollowUp.getDateTimeForFollowup().compareTo(t1.getDateTimeForFollowup());
            }
        });
        Collections.sort(doneFollowups, new Comparator<TempFollowUp>() {
            @Override
            public int compare(TempFollowUp tempFollowUp, TempFollowUp t1) {
                return tempFollowUp.getDateTimeForFollowup().compareTo(t1.getDateTimeForFollowup());
            }
        });
        setDueFollowups(dueFollowups);
        setDoneFollowups(doneFollowups);
        ArrayList<TempFollowUp> allTodaysFollowupsOrdered = new ArrayList<>();
        for (TempFollowUp oneFollowup : dueFollowups) {
                allTodaysFollowupsOrdered.add(oneFollowup);
        }
        for (TempFollowUp oneFollowup : doneFollowups) {
                allTodaysFollowupsOrdered.add(oneFollowup);
        }
        return allTodaysFollowupsOrdered;
    }

    public ArrayList<TempFollowUp> getDueFollowups() {
        return DueFollowups;
    }

    public void setDueFollowups(ArrayList<TempFollowUp> dueFollowups) {
        DueFollowups = dueFollowups;
    }

    public ArrayList<TempFollowUp> getDoneFollowups() {
        return DoneFollowups;
    }

    public void setDoneFollowups(ArrayList<TempFollowUp> doneFollowups) {
        DoneFollowups = doneFollowups;
    }
}
