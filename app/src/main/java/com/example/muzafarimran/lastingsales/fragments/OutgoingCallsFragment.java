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
public class OutgoingCallsFragment extends Fragment {


    private List<Call> outgoingCalls = new ArrayList<>();
    private int outgoingCallsType = 0;


    public void setList(List<Call> missedCalls){
        this.outgoingCalls = missedCalls;
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

        View view = inflater.inflate(R.layout.fragment_outgoing_calls, container, false);
        ListView listView = (ListView) view.findViewById(R.id.outgoing_calls_list);
//        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.calls_text_view, call_logs);
//        listView.setAdapter(adapter);

        //CallsAdapter adapter = null;


        view = inflater.inflate(R.layout.fragment_outgoing_calls, container, false);
        listView = (ListView) view.findViewById(R.id.outgoing_calls_list);
        CallsAdapter callsadapter = new CallsAdapter(getContext(), outgoingCalls);
        listView.setAdapter(callsadapter);


        return view;
    }

}
