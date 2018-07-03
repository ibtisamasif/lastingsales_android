package com.example.muzafarimran.lastingsales.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.recycleradapter.OrganizationRecyclerAdapter;

import java.util.List;

public class OrganizationFragment extends TabFragment {


    View view;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private List<LSOrganization> list;

    public OrganizationFragment() {
    }

    android.support.design.widget.FloatingActionButton addOrganizationBtn;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.organization_fragment, container, false);

        addOrganizationBtn = view.findViewById(R.id.addOrganization);

        addOrganizationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrganization();
            }
        });

        recyclerView = view.findViewById(R.id.org_rec);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initOrgationRow();

        adapter = new OrganizationRecyclerAdapter(list, getActivity());

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void addOrganization() {
        Dialog addOrgDialog = new Dialog(getActivity());
        addOrgDialog.setContentView(R.layout.add_organization);
        addOrgDialog.setCancelable(true);
        addOrgDialog.show();

        Button insertOrg = (Button) addOrgDialog.findViewById(R.id.bSaveAddOrg);
        Button cancel = (Button) addOrgDialog.findViewById(R.id.bCancelAddOrg);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrgDialog.dismiss();
            }
        });

        insertOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText getOrgName = addOrgDialog.findViewById(R.id.etNameAddOrg);

                if (getOrgName.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter Organization Name!!!!", Toast.LENGTH_SHORT).show();
                } else {
                    LSOrganization lsOrganization = new LSOrganization();
                    lsOrganization.setName(getOrgName.getText().toString());

                    if (lsOrganization.save() > 0) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error not saved something went wrong", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }


    private void initOrgationRow() {
        //list.clear();
        list = LSOrganization.listAll(LSOrganization.class);
    }

}
