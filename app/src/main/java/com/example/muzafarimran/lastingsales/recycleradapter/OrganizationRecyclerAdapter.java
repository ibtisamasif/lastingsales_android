package com.example.muzafarimran.lastingsales.recycleradapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;

import java.util.List;

public class OrganizationRecyclerAdapter extends RecyclerView.Adapter<OrganizationRecyclerAdapter.OrganizationViewHolder> {


    List<LSOrganization> organizationList;

    public OrganizationRecyclerAdapter(List<LSOrganization> organizationList, Context context) {
        this.organizationList = organizationList;
        this.context = context;
    }

    Context context;


    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organization_row,parent,false);

        return new OrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {

            LSOrganization cur_org=organizationList.get(position);

            holder.org_phone.setText(cur_org.getPhone());
            holder.org_name.setText(cur_org.getName());



    }



    @Override
    public int getItemCount() {
        return organizationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {


        TextView org_name,org_phone;


        public OrganizationViewHolder(View itemView) {
            super(itemView);

            org_name=itemView.findViewById(R.id.org_name);
            org_phone=itemView.findViewById(R.id.org_phone);
        }
    }


}
