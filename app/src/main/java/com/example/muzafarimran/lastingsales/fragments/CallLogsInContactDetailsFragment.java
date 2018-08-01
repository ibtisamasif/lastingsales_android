package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 12/30/2016.
 */

public class CallLogsInContactDetailsFragment extends TabFragment {

    public static final String TAG = "CallLogsInContactDetailsFragment";
    private static Bundle args;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private List<Object> list = new ArrayList<Object>();
    private TinyBus bus;
    private Long contactIDLong;
    LSContact mContact;
    String number = "";

    public static CallLogsInContactDetailsFragment newInstance(int page, String title, Long id) {
        CallLogsInContactDetailsFragment fragmentFirst = new CallLogsInContactDetailsFragment();
        args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        contactIDLong = args.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
        number = mContact.getPhoneOne();
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
//        contactIDLong = args.getLong("someId");
//        mContact = LSContact.findById(LSContact.class, contactIDLong);
//        number = mContact.getPhoneOne();
//        ArrayList<LSCall> allCalls = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();
//        setList(allCalls);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_call_details_fragment_new, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Collection<LSCall> allCalls = Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();

        list.clear();
        if (!allCalls.isEmpty()) {
            list.addAll(allCalls);
        } else {
            ErrorItem erItem = new ErrorItem();
            erItem.message = "NOTHING TO DISPLAY";
            list.add(erItem);
        }

        adapter = new MyRecyclerViewAdapter(getActivity(), list);

        mRecyclerView.setAdapter(adapter);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView = null;
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
