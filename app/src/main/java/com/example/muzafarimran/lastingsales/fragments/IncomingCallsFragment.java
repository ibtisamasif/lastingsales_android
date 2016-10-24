package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.Call;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.CallsAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class IncomingCallsFragment extends Fragment {


    private List<Call> incomingCalls = new ArrayList<>();


    public void setList(List<Call> missedCalls){
        this.incomingCalls = missedCalls;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: move to async thread
//        Collections.sort(call_logs, comparing(Call::getType));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.e("DEBUG", "onResume of LoginFragment");

        View view = null;
        ListView listView = null;
        //CallsAdapter adapter = null;

        view = inflater.inflate(R.layout.fragment_incoming_calls, container, false);
        listView = (ListView) view.findViewById(R.id.incoming_calls_list);
        CallsAdapter callsadapter = new CallsAdapter(getContext(), incomingCalls);
        listView.setAdapter(callsadapter);

        return view;
    }

}
