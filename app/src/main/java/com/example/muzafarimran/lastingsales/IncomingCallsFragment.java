package com.example.muzafarimran.lastingsales;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

   /* @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");
        incomingCalls.add(new Call("Kashif klkj", "03xx-yyzzxxx", "missed", "2 hours ago"));
        incomingCalls.add(new Call("Salman lkj", "0323-4433108", "missed", "1 min ago"));
        incomingCalls.add(new Call("Raza klj", "0332-5404943", "incoming", "10 mins ago"));
        super.onResume();
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: move to async thread
//        Collections.sort(call_logs, comparing(Call::getType));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("DEBUG", "onResume of LoginFragment");

        View view = inflater.inflate(R.layout.fragment_incoming_calls, container, false);
        ListView listView = (ListView) view.findViewById(R.id.incoming_calls_list);
//        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.calls_text_view, call_logs);
//        listView.setAdapter(adapter);
        CallsAdapter adapter = new CallsAdapter(getContext(), incomingCalls);
        listView.setAdapter(adapter);
        return view;
    }

}
