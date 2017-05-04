package com.example.muzafarimran.lastingsales.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.InquiriesAdapter;
import com.example.muzafarimran.lastingsales.customview.ErrorScreenView;
import com.example.muzafarimran.lastingsales.events.InquiryDeletedEventModel;
import com.example.muzafarimran.lastingsales.events.MissedCallEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;

import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class InquiriesFragment extends SearchFragment {

    public static final String TAG = "InquiriesFragment";
    InquiriesAdapter inquiriesAdapter;
    ListView listView = null;
    private TinyBus bus;
    private ErrorScreenView errorScreenView;

    public static InquiriesFragment newInstance(int page, String title) {
        InquiriesFragment fragmentFirst = new InquiriesFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public void setList(List<LSInquiry> inquiries) {
        if (inquiriesAdapter != null) {
            inquiriesAdapter.setList(inquiries);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        inquiriesAdapter = new InquiriesAdapter(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        bus.unregister(this);
    }

    @Subscribe
    public void onCallReceivedEventModel(MissedCallEventModel event) {
        Log.d(TAG, "onMissedCallEvent() called with: event = [" + event + "]");
        if (event.getState() == MissedCallEventModel.CALL_TYPE_MISSED) {
            List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
            setList(inquiries);
        }
    }

    @Subscribe
    public void onInquiryDeletedEventModel(InquiryDeletedEventModel event) {
        List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        setList(inquiries);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
        setList(inquiries);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        errorScreenView = (ErrorScreenView) view.findViewById(R.id.ivleads_contacts_custom);
        errorScreenView.setErrorImage(R.drawable.delight_lost);
        errorScreenView.setErrorText(this.getResources().getString(R.string.em_inquiries_delight));
        listView = (ListView) view.findViewById(R.id.calls_list);
        listView.setAdapter(inquiriesAdapter);
        listView.setEmptyView(errorScreenView);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        if(materialSearchView!=null) {
            materialSearchView.setMenuItem(item);
        }
    }

    @Override
    protected void onSearch(String query) {
        inquiriesAdapter.getFilter().filter(query);
    }
}