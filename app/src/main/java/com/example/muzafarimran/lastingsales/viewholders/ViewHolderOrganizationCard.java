package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddDealActivity;
import com.example.muzafarimran.lastingsales.activities.OrganizationDetailsTabActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;

import java.util.Collection;

/**
 * Created by ibtisam on 11/1/2017.
 */

public class ViewHolderOrganizationCard extends RecyclerView.ViewHolder {
    public static final String TAG = "ViewHolderContactCard";

    private final ConstraintLayout cl;
    private final TextView org_name, org_phone;
    private final ImageView add_deal_icon;


    public ViewHolderOrganizationCard(View v) {
        super(v);
        cl =  v.findViewById(R.id.cl);
        org_name =  v.findViewById(R.id.org_name);
        org_phone =  v.findViewById(R.id.org_phone);
        add_deal_icon =  v.findViewById(R.id.add_deal_icon);
    }

    public void bind(Object item, int position, Context mContext) {
        LSOrganization organization = (LSOrganization) item;
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailsActivityIntent = new Intent(mContext, OrganizationDetailsTabActivity.class);
                long organizationId = organization.getId();
                detailsActivityIntent.putExtra(OrganizationDetailsTabActivity.KEY_ORGANIZATION_ID, organizationId + "");
                mContext.startActivity(detailsActivityIntent);
            }
        });
        org_phone.setText(organization.getPhone());
        org_name.setText(organization.getName());

        Collection<LSDeal> deals = organization.getAllDeals();
        if (deals != null && deals.size() > 0) {
            add_deal_icon.setImageResource(R.drawable.ic_monetization_on_grey_24dp);
        }else {
            add_deal_icon.setImageResource(R.drawable.ic_monetization_on_24dp);
        }
        add_deal_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddDealActivity.class);
                intent.putExtra(AddDealActivity.TAG_LAUNCH_MODE_ORGANIZATION_ID, organization.getId());
                mContext.startActivity(intent);
            }
        });
    }

}