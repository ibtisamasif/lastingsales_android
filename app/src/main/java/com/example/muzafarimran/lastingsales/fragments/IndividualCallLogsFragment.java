package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.IndividualContactCallAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/30/2016.
 */

public class IndividualCallLogsFragment extends TabFragment{

    public static final String TAG = "IndividualCallLogsFragment";
    ListView listView = null;
    IndividualContactCallAdapter individualContactCallAdapter;
    MaterialSearchView searchView;
    private TinyBus bus;
    private Long contactIDLong;
    LSContact mContact;
    String number = "";

    public static IndividualCallLogsFragment newInstance(int page, String title , Long id) {
        IndividualCallLogsFragment fragmentFirst = new IndividualCallLogsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSCall> calls) {
        if (individualContactCallAdapter != null) {
            individualContactCallAdapter.setList(calls);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
        number = mContact.getPhoneOne();
        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();
        individualContactCallAdapter = new IndividualContactCallAdapter(getActivity(), allCalls);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
//        Bundle bundle = this.getArguments();
//        contactIDLong = bundle.getLong("someId");
//        mContact = LSContact.findById(LSContact.class, contactIDLong);
//        number = mContact.getPhoneOne();
//        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();
//        setList(allCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_call_details_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.calls_list);
        listView.setAdapter(individualContactCallAdapter);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
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
