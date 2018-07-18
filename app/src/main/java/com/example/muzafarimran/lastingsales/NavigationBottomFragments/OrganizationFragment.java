package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.recycleradapter.OrganizationRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.view.MenuItemCompat.getActionView;

public class OrganizationFragment extends TabFragment  {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private List<LSOrganization> list;
//    FloatingActionButton addOrganizationFab;

    public OrganizationFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.organization_fragment, container, false);

//        addOrganizationFab = view.findViewById(R.id.addOrganization);

//        addOrganizationFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addOrganizationDialogBox();
//            }
//        });

        recyclerView = view.findViewById(R.id.org_rec);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = LSOrganization.listAll(LSOrganization.class);

        adapter = new OrganizationRecyclerAdapter(list, getActivity());

        recyclerView.setAdapter(adapter);

        return view;
    }

}

