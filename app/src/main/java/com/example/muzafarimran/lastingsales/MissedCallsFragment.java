package com.example.muzafarimran.lastingsales;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
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
    private  int missedCallsType = 0;


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

        View view = null;
        ListView listView = null;
        //CallsAdapter adapter = null;
        this.missedCallsType = (int)getArguments().getInt("key");



        switch(this.missedCallsType) {
            case 1 :
                // all missed calls

                view = inflater.inflate(R.layout.fragment_calls, container, false);
                listView = (ListView) view.findViewById(R.id.calls_list);
                CallsAdapter callsadapter = new CallsAdapter(getContext(), missedCalls);
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
                IndividualConatactCallAdapter indadapter = new IndividualConatactCallAdapter(getContext(), missedCalls);
                listView.setAdapter(indadapter);

                break;


        }

        return view;
    }
}
