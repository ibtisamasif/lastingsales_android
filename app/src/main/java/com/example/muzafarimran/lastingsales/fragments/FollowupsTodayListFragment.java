package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.FollowupsListAdapter2;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ahmad on 10-Dec-16.
 */

public class FollowupsTodayListFragment extends Fragment {
    public static final String TAG = "MissedCallFragment";
    FollowupsListAdapter2 followupsAdapter;
    ListView listView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        ArrayList<TempFollowUp> followUps = getFollowupsOFToday();
        followupsAdapter = new FollowupsListAdapter2(getContext(),followUps);
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
        List<TempFollowUp> followUps = getFollowupsOFToday();
        setList(followUps);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followups_list, container, false);
        listView = (ListView) view.findViewById(R.id.followups_list_view_in_fragment);
        listView.setAdapter(followupsAdapter);
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

    private ArrayList<TempFollowUp> getFollowupsOFToday()
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
        return followupsInToday;
    }
}
