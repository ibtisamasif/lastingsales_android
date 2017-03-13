package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.FollowupsAllListAdapter;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by ahmad on 10-Dec-16.
 */

public class FollowupsListFragment extends Fragment {
    public static final String TAG = "MissedCallFragment";
    FollowupsAllListAdapter followupsAdapter;
    ExpandableStickyListHeadersListView listView = null;
    ArrayList<TempFollowUp> todayFollowups;
    ArrayList<TempFollowUp> upcomingFollowups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        ArrayList<TempFollowUp> followUps = getTodaysAndUpcomingFollowups();
        followupsAdapter = new FollowupsAllListAdapter(getContext(), followUps);
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
        List<TempFollowUp> followUps = getTodaysAndUpcomingFollowups();
        setList(followUps);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followups_list, container, false);
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
    public ArrayList<TempFollowUp> getTodaysAndUpcomingFollowups()
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
        ArrayList<TempFollowUp> followupsAfterToday = new ArrayList<>();
        for (TempFollowUp oneFollowup : allFollowUps) {
            if ( oneFollowup.getDateTimeForFollowup() > endOfToday.getTimeInMillis()) {
                followupsAfterToday.add(oneFollowup);
            }
        }
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
        ArrayList<TempFollowUp> allTodaysFollowupsOrdered = new ArrayList<>();
        for (TempFollowUp oneFollowup : dueFollowups) {
            allTodaysFollowupsOrdered.add(oneFollowup);
        }
        for (TempFollowUp oneFollowup : doneFollowups) {
            allTodaysFollowupsOrdered.add(oneFollowup);
        }
        ArrayList<TempFollowUp> allTodaysAndUpcomingFollowupsOrdered = new ArrayList<>();
        Collections.sort(allTodaysFollowupsOrdered, new Comparator<TempFollowUp>() {
            @Override
            public int compare(TempFollowUp TempFollowUp, TempFollowUp t1) {
                return TempFollowUp.getDateTimeForFollowup().compareTo(t1.getDateTimeForFollowup());
            }
        });
        Collections.sort(followupsAfterToday, new Comparator<TempFollowUp>() {
            @Override
            public int compare(TempFollowUp TempFollowUp, TempFollowUp t1) {
                return TempFollowUp.getDateTimeForFollowup().compareTo(t1.getDateTimeForFollowup());
            }
        });

        for (TempFollowUp oneFollowup : allTodaysFollowupsOrdered) {
            allTodaysAndUpcomingFollowupsOrdered.add(oneFollowup);
        }
        for (TempFollowUp oneFollowup : followupsAfterToday) {
            allTodaysAndUpcomingFollowupsOrdered.add(oneFollowup);
        }


        setTodayFollowups(allTodaysFollowupsOrdered);
        setUpcomingFollowups(doneFollowups);
        return allTodaysAndUpcomingFollowupsOrdered;
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

    public ArrayList<TempFollowUp> getTodayFollowups() {
        return todayFollowups;
    }

    public void setTodayFollowups(ArrayList<TempFollowUp> todayFollowups) {
        this.todayFollowups = todayFollowups;
    }

    public ArrayList<TempFollowUp> getUpcomingFollowups() {
        return upcomingFollowups;
    }

    public void setUpcomingFollowups(ArrayList<TempFollowUp> upcomingFollowups) {
        this.upcomingFollowups = upcomingFollowups;
    }
}