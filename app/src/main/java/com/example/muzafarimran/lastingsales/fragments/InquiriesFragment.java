package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.listloaders.InquiryLoader;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

public class InquiriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    public static final String TAG = "InquiriesFragment";
    public static final int INQU_LOADER_ID = 1;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private List<Object> list = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;
    private TinyBus bus;

    public InquiriesFragment() {
    }

    public static InquiriesFragment newInstance(String param1, String param2) {
        Log.d(TAG, "newInstance: ");
        InquiriesFragment fragment = new InquiriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        if (getActivity() != null)
            bus = TinyBus.from(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_inquiries, container, false);
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
        getLoaderManager().initLoader(INQU_LOADER_ID, null, InquiriesFragment.this);
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
    public void onInquiryDeletedEventModel(InquiryDeletedEventModel event) {
        Log.d(TAG, "onInquiryDeletedEventModel: ");
        getLoaderManager().restartLoader(INQU_LOADER_ID, null, InquiriesFragment.this);
    }

    @Subscribe
    public void onMissedCallEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEventModel: ");
        getLoaderManager().restartLoader(INQU_LOADER_ID, null, InquiriesFragment.this);
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        list.clear();
        LoadingItem loadingItem = new LoadingItem();
        loadingItem.text = "Loading items...";
        list.add(loadingItem);
        adapter.notifyDataSetChanged();

        switch (id) {
            case INQU_LOADER_ID:
                return new InquiryLoader(getActivity());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        Log.d(TAG, "onLoadFinished: ");
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
        Log.d(TAG, "onLoaderReset: ");
        list.clear();
        list.addAll(new ArrayList<Object>());
        adapter.notifyDataSetChanged();
    }
}
