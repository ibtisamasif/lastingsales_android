package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.events.OutgoingCallEventModel;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import de.halfbit.tinybus.Bus;
import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class OutgoingCallsFragment extends Fragment {

    public static final String TAG = "OutgoingCallFragment";
    CallsAdapter callsadapter;
    ListView listView = null;
    MaterialSearchView searchView;
    private Bus mBus;
    private TinyBus bus;

    public static OutgoingCallsFragment newInstance(int page, String title) {
        OutgoingCallsFragment fragmentFirst = new OutgoingCallsFragment();
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
    public void onOutgoingCallEventModel(OutgoingCallEventModel event) {
        Log.d(TAG, "onOutgoingCallEvent() called with: event = [" + event + "]");
        if (event.getState() == OutgoingCallEventModel.CALL_TYPE_OUTGOING) {
            List<LSCall> outgoingCalls = LSCall.getCallsByTypeInDescendingOrder(LSCall.CALL_TYPE_OUTGOING);
            setList(outgoingCalls);
        }
//        TinyBus.from(getActivity().getApplicationContext()).unregister(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSCall> outgoingCalls = LSCall.getCallsByTypeInDescendingOrder(LSCall.CALL_TYPE_OUTGOING);
        setList(outgoingCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incoming_calls, container, false);
        listView = (ListView) view.findViewById(R.id.incoming_calls_list);
        listView.setAdapter(callsadapter);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
//                Toast.makeText(getContext(), "submitted text = " + query, Toast.LENGTH_SHORT).show();
                callsadapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
//                Toast.makeText(getContext(), "text changed to= " + newText, Toast.LENGTH_SHORT).show();
                callsadapter.getFilter().filter(newText);
                return false;
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
}