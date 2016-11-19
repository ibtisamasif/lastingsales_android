package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.Events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

import static java.util.Comparator.comparing;


/**
 * A simple {@link Fragment} subclass.
 */
public class MissedCallsFragment extends TabFragment {

    public static final String TAG = "MissedCallFragment";
    private List<LSCall> missedCalls = new ArrayList<>();
    CallsAdapter callsadapter;
    ListView listView = null;
    private Bus mBus;
    private TinyBus bus;



    public static MissedCallsFragment newInstance(int page, String title) {
        MissedCallsFragment fragmentFirst = new MissedCallsFragment();
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
//        mBus = TinyBus.from(getActivity().getApplicationContext());
        callsadapter = new CallsAdapter(getContext());
        callsadapter.setList(missedCalls);
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
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {

            List<LSCall> missedCalls = LSCall.getCallsByType(LSCall.CALL_TYPE_MISSED);
            setList(missedCalls);
        }
//        TinyBus.from(getActivity().getApplicationContext()).unregister(event);

    }


    @Override
    public void onResume() {
        super.onResume();
        List<LSCall> missedCalls = LSCall.getCallsByType(LSCall.CALL_TYPE_MISSED);

        setList(missedCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = null;
        view = inflater.inflate(R.layout.fragment_calls, container, false);
        listView = (ListView) view.findViewById(R.id.calls_list);
        listView.setAdapter(callsadapter);


        return view;
    }
}