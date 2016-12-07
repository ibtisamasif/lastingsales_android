package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.Events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class UntaggedContactsCallsFragment extends SearchFragment {

    private static final String TAG = "UntaggedCallFragment";
    CallsAdapter callsadapter;
    ListView listView = null;
    private List<LSCall> untaggedCalls = new ArrayList<>();
    private Bus mBus;
    private TinyBus bus;

    public static UntaggedContactsCallsFragment newInstance(int page, String title) {
        UntaggedContactsCallsFragment fragmentFirst = new UntaggedContactsCallsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSCall> missedCalls) {
        if (callsadapter != null) {
            callsadapter.setList(missedCalls);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        callsadapter = new CallsAdapter(getContext());
        callsadapter.setList(untaggedCalls);
        setHasOptionsMenu(true);
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

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onUntaggedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            updateCallsList();
        }
    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {
            updateCallsList();
        }
    }

    @Subscribe
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onOutgoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            updateCallsList();
        }
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    private void updateCallsList() {

        List<LSCall> untaggedCalls = new ArrayList<>();
        List<LSCall> allCalls = LSCall.listAll(LSCall.class);
        for (LSCall oneCall : allCalls) {
            if (oneCall.getContact() == null) {
                untaggedCalls.add(oneCall);
            }
        }
        this.untaggedCalls = untaggedCalls;
        setList(untaggedCalls);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateCallsList();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        listView = (ListView) view.findViewById(R.id.calls_list);
        listView.setAdapter(callsadapter);
        return view;
    }

    @Override
    protected void onSearch(String query) {
        callsadapter.getFilter().filter(query);
    }
}