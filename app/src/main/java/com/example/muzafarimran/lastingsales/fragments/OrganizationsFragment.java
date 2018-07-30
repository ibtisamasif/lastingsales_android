package com.example.muzafarimran.lastingsales.fragments;

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
import com.example.muzafarimran.lastingsales.adapters.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.events.OrganizationEventModel;
import com.example.muzafarimran.lastingsales.providers.listloaders.OrganizationsLoader;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

public class OrganizationsFragment extends TabFragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    public static final String TAG = "OrganizationsFragment";
    public static final int HOME_LOADER2_ID = 22;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private List<Object> list = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;
    private TinyBus bus;

    public OrganizationsFragment() {
    }

    public static OrganizationsFragment newInstance() {
        OrganizationsFragment fragment = new OrganizationsFragment();
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
        View view = inflater.inflate(R.layout.fragment_organizations, container, false);
        adapter = new MyRecyclerViewAdapter(getActivity(), list); //TODO potential bug getActivity can be null.
        RecyclerView mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        getLoaderManager().initLoader(HOME_LOADER2_ID, null, OrganizationsFragment.this);
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
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onOrganizationEventModel(OrganizationEventModel event) {
        Log.d(TAG, "onOrganizationEventModel: ");
        getLoaderManager().restartLoader(HOME_LOADER2_ID, null, OrganizationsFragment.this);
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        list.clear();
        LoadingItem loadingItem = new LoadingItem();
        loadingItem.text = "Loading items...";
        list.add(loadingItem);
        adapter.notifyDataSetChanged();

        switch (id) {
            case HOME_LOADER2_ID:
                return new OrganizationsLoader(getActivity());
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