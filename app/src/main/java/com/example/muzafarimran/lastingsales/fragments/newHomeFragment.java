package com.example.muzafarimran.lastingsales.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class newHomeFragment extends TabFragment {
    private static final String TAG = "newHomeFragment";
    private TinyBus bus;
    private SessionManager sessionManager;

    private RecyclerView mRecyclerView;
//    HomeAdapter homeAdapter;

//    private List<LSContact> untaggedContacts = new ArrayList<>();

    private List<Object> list = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getActivity());
        setRetainInstance(true);
        setHasOptionsMenu(true);

        parseResult();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_recycler, container, false);
//        View view = inflater.inflate(R.layout.fragment_home_new, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//        homeAdapter = new HomeAdapter(getActivity(), list);

        adapter = new MyRecyclerViewAdapter(getActivity(), list);

        mRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);
//        List<LSContact> unlabeledContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
//        this.list = (List<Object>) (LSContact) unlabeledContacts;
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
        bus.unregister(this);
        Log.d(TAG, "onStop() called");
        super.onStop();
    }

    private void parseResult() {

        Collection<SeparatorItem> listSeparator = new ArrayList<SeparatorItem>();

        SeparatorItem spItem = new SeparatorItem();
        spItem.text = "unlabeled contacts";
        listSeparator.add(spItem);

        Collection<LSContact> unlabeledContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);

        Collection<LSInquiry> inquiriesContacts = LSInquiry.getAllPendingInquiriesInDescendingOrder();

        list.addAll(inquiriesContacts);
        list.addAll(listSeparator);
        list.addAll(unlabeledContacts);


    }
}