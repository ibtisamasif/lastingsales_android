package com.example.muzafarimran.lastingsales.deals;

import android.content.Context;
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
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.events.DealAddedEventModel;
import com.example.muzafarimran.lastingsales.listloaders.DealsLoader;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

public class DealDynamicFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    public static final String TAG = "DealDynamicFragment";
    public static final String DEALS_WORKFLOW_STAGE_ID = "deals_workflow_stage_id";
    public static final int DEAL_LOADER_ID = 4;

//    private TextView tvDetailFragment;

    private String pageTitle;

    private String stageId;

    private List<Object> listLoader = new ArrayList<Object>();

    private MyRecyclerViewAdapter adapter;
    private Bundle args;
    private TinyBus bus;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        if (getActivity() != null)
            bus = TinyBus.from(getActivity().getApplicationContext());
        return inflater.inflate(R.layout.fragment_deal_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
//        tvDetailFragment = (TextView) view.findViewById(R.id.tvFragTitle);
//        tvDetailFragment.setText(getPageTitle());

        adapter = new MyRecyclerViewAdapter(getActivity(), listLoader); //TODO potential bug getActivity can be null.
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);

        String stepId = getStageId();
        Log.d(TAG, "onViewCreated: stageId: " + stepId);
        args = new Bundle();
        args.putString(DEALS_WORKFLOW_STAGE_ID, getStageId());
        getLoaderManager().initLoader(DEAL_LOADER_ID, args, DealDynamicFragment.this);
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

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getStageId() {
        return stageId;
    }

    public void setStageId(String stepId) {
        this.stageId = stepId;
    }

    @Subscribe
    public void onDealEventModel(DealAddedEventModel event) {
        Log.d(TAG, "onDealEventModel: ");
        getLoaderManager().restartLoader(DEAL_LOADER_ID, args, DealDynamicFragment.this);
    }

    @Override
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        listLoader.clear();
        LoadingItem loadingItem = new LoadingItem();
        loadingItem.text = "Loading items...";
        listLoader.add(loadingItem);
        adapter.notifyDataSetChanged();

        switch (id) {
            case DEAL_LOADER_ID:
                return new DealsLoader(getActivity(), args);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        Log.d(TAG, "onLoadFinished: ");
        if (data != null) {
            if (!data.isEmpty()) {
                listLoader.clear();
                listLoader.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        Log.d(TAG, "onLoaderReset: ");
        listLoader.clear();
        listLoader.addAll(new ArrayList<Object>());
        adapter.notifyDataSetChanged();
    }

}