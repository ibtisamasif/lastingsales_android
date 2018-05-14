package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.events.ContactDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;
import com.example.muzafarimran.lastingsales.listloaders.LeadsLoader;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

public class BlankFragment3_5 extends TabFragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    public static final String TAG = "BlankFragment3_5";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int LEADS_LOADER_ID = 3;


    private String mParam1;
    private String mParam2;

    private List<Object> list = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;
    private TinyBus bus;
    private Bundle args;

    public BlankFragment3_5() {
    }

    public static BlankFragment3_5 newInstance() {
        BlankFragment3_5 fragment = new BlankFragment3_5();
        Bundle args = new Bundle();
//        args.putInt("someInt", page);
//        args.putString("someTitle", title);
//        args.putLong("someId", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null)
            bus = TinyBus.from(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_blank3_1, container, false);
        adapter = new MyRecyclerViewAdapter(getActivity(), list); //TODO potential bug getActivity can be null.
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        args = new Bundle();
        args.putString(LeadsLoader.KEY_LEADS_TO_LOAD, LeadsLoader.INACTIVE_LEADS);
        getLoaderManager().initLoader(LEADS_LOADER_ID, args, BlankFragment3_5.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus.register(this);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop() called");
        try {
            bus.unregister(this);
        }catch (Exception e){
        }
        super.onStop();
    }

    @Subscribe
    public void onSaleContactAddedEventModel(LeadContactAddedEventModel event) {
        Log.d(TAG, "onSaleContactAddedEventModel: ");
        getLoaderManager().restartLoader(LEADS_LOADER_ID, args, BlankFragment3_5.this);
    }

    @Subscribe
    public void onLeadContactDeletedEventModel(ContactDeletedEventModel event) {
        Log.d(TAG, "onLeadContactDeletedEventModel: ");
        getLoaderManager().restartLoader(LEADS_LOADER_ID, args, BlankFragment3_5.this);
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        list.clear();
        LoadingItem loadingItem = new LoadingItem();
        loadingItem.text = "Loading items...";
        list.add(loadingItem);
        adapter.notifyDataSetChanged();

        switch (id) {
            case LEADS_LOADER_ID:
                return new LeadsLoader(getActivity(), args);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        if (data != null) {
            if (!data.isEmpty()) {
                list.clear();
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        list.clear();
        list.addAll(new ArrayList<Object>());
        adapter.notifyDataSetChanged();
    }
}