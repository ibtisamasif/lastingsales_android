package com.example.muzafarimran.lastingsales;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;


/**
 * A simple {@link Fragment} subclass.
 */
public class MissedCallsFragment extends TabFragment {

    private List<Call> missedCalls = new ArrayList<>();


    public void setList(List<Call> missedCalls){
        this.missedCalls = missedCalls;
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

        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        ListView listView = (ListView) view.findViewById(R.id.calls_list);
//        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.calls_text_view, call_logs);
//        listView.setAdapter(adapter);
        CallsAdapter adapter = new CallsAdapter(getContext(), missedCalls);
        listView.setAdapter(adapter);
        return view;
    }
}
