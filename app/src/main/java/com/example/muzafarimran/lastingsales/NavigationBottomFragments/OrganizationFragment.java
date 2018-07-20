package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.recycleradapter.OrganizationRecyclerAdapter;

import java.util.List;

import de.halfbit.tinybus.TinyBus;

public class OrganizationFragment extends TabFragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private List<LSOrganization> list;
    private Context mContext;
    private TinyBus bus;
//    FloatingActionButton addOrganizationFab;

    public OrganizationFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organization_fragment, container, false);

        recyclerView = view.findViewById(R.id.org_rec);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = LSOrganization.listAll(LSOrganization.class);

        adapter = new OrganizationRecyclerAdapter(list, getActivity());

        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        bus = TinyBus.from(mContext.getApplicationContext());
//        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
//        bus.unregister(mContext.getApplicationContext());
        super.onStop();
    }
}
