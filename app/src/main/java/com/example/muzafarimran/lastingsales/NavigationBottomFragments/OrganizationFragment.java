package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.TabFragment;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.recycleradapter.OrganizationRecyclerAdapter;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;

import java.util.List;

public class OrganizationFragment extends TabFragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    private List<LSOrganization> list;
    FloatingActionButton addOrganizationFab;

    public OrganizationFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organization_fragment, container, false);

        addOrganizationFab = view.findViewById(R.id.addOrganization);

        addOrganizationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrganizationDialogBox();
            }
        });

        recyclerView = view.findViewById(R.id.org_rec);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = LSOrganization.listAll(LSOrganization.class);

        adapter = new OrganizationRecyclerAdapter(list, getActivity());

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void addOrganizationDialogBox() {
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
                EditText nameAddOrg = addOrgDialog.findViewById(R.id.etNameAddOrg);
                EditText emailAddOrg = addOrgDialog.findViewById(R.id.etEmailAddOrg);
                EditText phoneAddOrg = addOrgDialog.findViewById(R.id.etPhoneAddOrg);

                if (nameAddOrg.getText().toString().isEmpty()) {
                    nameAddOrg.setError("Please enter  Name!");
//                    Toast.makeText(getActivity(), "Please enter  Name!", Toast.LENGTH_SHORT).show();
                } else if (emailAddOrg.getText().toString().isEmpty()) {
                    emailAddOrg.setError("Please enter  Email!");
//                    Toast.makeText(getActivity(), "Please enter  Email!", Toast.LENGTH_SHORT).show();
                } else if (phoneAddOrg.getText().toString().isEmpty()) {
                    phoneAddOrg.setError("Please enter  Phone!");
//                    Toast.makeText(getActivity(), "Please enter Phone!", Toast.LENGTH_SHORT).show();
                } else {
                    LSOrganization lsOrganization = new LSOrganization();
                    lsOrganization.setName(nameAddOrg.getText().toString());
                    lsOrganization.setEmail(emailAddOrg.getText().toString());
                    lsOrganization.setPhone(phoneAddOrg.getText().toString());
                    lsOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_ORGANIZATION_ADD_NOT_SYNCED);

                    if (lsOrganization.save() > 0) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Organization saved", Toast.LENGTH_SHORT).show();
                        addOrgDialog.dismiss();
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(addOrgDialog.getContext());
                        dataSenderAsync.run();
                    } else {
                        Toast.makeText(getActivity(), "Error not saved something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
