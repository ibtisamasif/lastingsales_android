package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.providers.loaders.InquiriesLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class newInquiriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<LSInquiry>> {


    public static final String TAG = "newInquiriesFragment";

    private List<Object> list = new ArrayList<Object>();
    private TinyBus bus;
    private ErrorScreenView errorScreenView;
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;

    public static newInquiriesFragment newInstance(int page, String title) {
        newInquiriesFragment fragmentFirst = new newInquiriesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setRetainInstance(true);
        adapter = new MyRecyclerViewAdapter(getContext(), list);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        bus.unregister(this);
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            Collection<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
            list.clear();
            list.addAll(inquiries);
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onInquiryDeletedEventModel(InquiryDeletedEventModel event) {
        List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        list.clear();
        list.addAll(inquiries);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
//        List<LSInquiry> contacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//        setList(contacts);
        getLoaderManager().initLoader(1, null, this).forceLoad();
//        new ListPopulateAsync().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.activity_main_recycler, container, false);
//        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
//        errorScreenView.setErrorImage(R.drawable.delight_lost);
//        errorScreenView.setErrorText(this.getResources().getString(R.string.em_inquiries_delight));

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        homeAdapter = new HomeAdapter(getActivity(), list);

        adapter = new MyRecyclerViewAdapter(getActivity(), list);

        mRecyclerView.setAdapter(adapter);


//        mRecyclerView.setEmptyView(errorScreenView);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
    }

    @Override
    public Loader<List<LSInquiry>> onCreateLoader(int i, Bundle bundle) {
        return new InquiriesLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<LSInquiry>> loader, List<LSInquiry> lsInquiries) {
        Log.d(TAG, "onLoadFinished: ");
        list.clear();
        list.addAll(lsInquiries);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<LSInquiry>> loader) {
        Log.d(TAG, "onLoaderReset: ");
//        adapter.setList(new ArrayList<LSInquiry>());
        list.clear();
//        list.addAll(new Collection<LSInquiry>());
        adapter.notifyDataSetChanged();
    }
}
