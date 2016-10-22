package com.example.muzafarimran.lastingsales;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
        this.outgoingCallsType = (int)getArguments().getInt("key");

        switch(this.outgoingCallsType) {
            case 1 :
                // all missed calls

                view = inflater.inflate(R.layout.fragment_outgoing_calls, container, false);
                listView = (ListView) view.findViewById(R.id.outgoing_calls_list);
                CallsAdapter callsadapter = new CallsAdapter(getContext(), outgoingCalls);
                listView.setAdapter(callsadapter);


                break;

            case 2 :
                // missed calls for an individual

                view = inflater.inflate(R.layout.list_view_contact_call_details, container, false);

                CallClickListener callClickListener = new CallClickListener(getActivity());

                ((TextView)(view.findViewById(R.id.call_numbe_ind))).setText((String)getArguments().getString("number"));
                ((TextView)(view.findViewById(R.id.contact_name_ind))).setText((String)getArguments().getString("name"));
                ((ImageView)(view.findViewById(R.id.call_icon_ind))).setTag((String)getArguments().getString("number"));
                ((ImageView)(view.findViewById(R.id.call_icon_ind))).setOnClickListener(callClickListener);

                //hide tag button if name is not stored
                if (((String)getArguments().getString("name")) != null || !(((String)getArguments().getString("name")).isEmpty())){
                    ((Button)(view.findViewById(R.id.tag_button_ind))).setVisibility(View.GONE);
                }


                listView = (ListView) view.findViewById(R.id.calls_list_contact_ind);
                IndividualConatactCallAdapter indadapter = new IndividualConatactCallAdapter(getContext(), outgoingCalls, "outgoing");
                listView.setAdapter(indadapter);

                break;


        }

        return view;
    }

}
