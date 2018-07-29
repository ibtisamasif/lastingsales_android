package com.example.muzafarimran.lastingsales.recycleradapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddDealActivity;
import com.example.muzafarimran.lastingsales.activities.OrganizationDetailsTabActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;

import java.util.Collection;
import java.util.List;

public class OrganizationRecyclerAdapter extends RecyclerView.Adapter<OrganizationRecyclerAdapter.OrganizationViewHolder> {

    private Context mContext;
    private List<LSOrganization> organizationList;

    public OrganizationRecyclerAdapter(List<LSOrganization> organizationList, Context mContext) {
        this.organizationList = organizationList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrganizationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_organization, parent, false);
        return new OrganizationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationViewHolder holder, int position) {
        LSOrganization organization = organizationList.get(position);
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsActivityIntent = new Intent(mContext, OrganizationDetailsTabActivity.class);
                long organizationId = organization.getId();
                detailsActivityIntent.putExtra(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID, organizationId + "");
                mContext.startActivity(detailsActivityIntent);
            }
        });
        holder.org_phone.setText(organization.getPhone());
        holder.org_name.setText(organization.getName());

        Collection<LSDeal> deals = organization.getAllDeals();
        if (deals != null && deals.size() > 0) {
            holder.add_deal_icon.setImageResource(R.drawable.ic_monetization_on_grey_24dp);
        } else {
            holder.add_deal_icon.setImageResource(R.drawable.ic_monetization_on_24dp);
        }
        holder.add_deal_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddDealActivity.class);
                intent.putExtra(AddDealActivity.TAG_LAUNCH_MODE_ORGANIZATION_ID, organization.getId());
                mContext.startActivity(intent);
            }
        });
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
        ConstraintLayout cl;
        private final TextView org_name, org_phone;
        private final ImageView add_deal_icon;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.cl);
            org_name = itemView.findViewById(R.id.org_name);
            org_phone = itemView.findViewById(R.id.org_phone);
            add_deal_icon = itemView.findViewById(R.id.add_deal_icon);
        }
    }


}
