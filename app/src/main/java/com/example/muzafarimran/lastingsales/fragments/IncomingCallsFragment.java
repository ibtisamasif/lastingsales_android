package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.Events.IncomingCallEventModel;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
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
public class IncomingCallsFragment extends Fragment {

    public static final String TAG = "IncomingCallFragment";

    private List<LSCall> incomingCalls = new ArrayList<>();
    CallsAdapter callsadapter;
    ListView listView = null;
    private Bus mBus;
    private TinyBus bus;


    public void setList(List<LSCall> inComingCalls) {
        if (callsadapter != null) {
            callsadapter.setList(inComingCalls);
        }
    }

    public static IncomingCallsFragment newInstance(int page, String title) {
        IncomingCallsFragment fragmentFirst = new IncomingCallsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: move to async thread
//        Collections.sort(call_logs, comparing(Call::getType));
        setRetainInstance(true);
//        mBus = TinyBus.from(getActivity().getApplicationContext());
        callsadapter = new CallsAdapter(getContext());
        callsadapter.setList(incomingCalls);
    }

    @Override
    public void onStart() {
        super.onStart();
//        mBus.register(this);

        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }


    @Override
    public void onStop() {
//        mBus.unregister(this);
        bus.unregister(this);
        Log.d(TAG, "onStop() called");

        super.onStop();

    }

    @Subscribe
    public void onIncommingCallReceivedEvent(IncomingCallEventModel event) {
        Log.d(TAG, "onIncomingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == IncomingCallEventModel.CALL_TYPE_INCOMING) {

            List<LSCall> incommingCalls = LSCall.getCallsByType(LSCall.CALL_TYPE_INCOMING);
            setList(incommingCalls);
        }
//        TinyBus.from(getActivity().getApplicationContext()).unregister(event);

    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSCall> missedCalls = LSCall.getCallsByType(LSCall.CALL_TYPE_INCOMING);

        setList(missedCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.e("DEBUG", "onResume of LoginFragment");

        View view = null;
        view = inflater.inflate(R.layout.fragment_incoming_calls, container, false);
        listView = (ListView) view.findViewById(R.id.incoming_calls_list);
        listView.setAdapter(callsadapter);

        return view;
    }

}
