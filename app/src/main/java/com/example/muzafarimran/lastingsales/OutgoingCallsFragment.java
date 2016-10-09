package com.example.muzafarimran.lastingsales;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OutgoingCallsFragment extends Fragment {


    private List<Call> outgoingCalls = new ArrayList<>();


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
        CallsAdapter adapter = new CallsAdapter(getContext(), outgoingCalls);
        listView.setAdapter(adapter);
        return view;
    }

}
