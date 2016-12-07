package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.Events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;

import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllCallsFragment extends Fragment {

    public static final String TAG = "AllCallFragment";
    CallsAdapter callsadapter;
    ListView listView = null;
    private Bus mBus;
    private TinyBus bus;

    public static AllCallsFragment newInstance(int page, String title) {
        AllCallsFragment fragmentFirst = new AllCallsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSCall> outgoingCalls) {
        if (callsadapter != null) {
            callsadapter.setList(outgoingCalls);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        callsadapter = new CallsAdapter(getContext());
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
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onAnyOutGoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
            setList(allCalls);
        }
        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onAnyMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
            setList(allCalls);
        }
    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onAnyIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {
            List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
            setList(allCalls);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSCall> allCalls = LSCall.getAllCallsInDescendingOrder();
        setList(allCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_incoming_calls, container, false);
        listView = (ListView) view.findViewById(R.id.incoming_calls_list);
        listView.setAdapter(callsadapter);

        return view;
    }
}