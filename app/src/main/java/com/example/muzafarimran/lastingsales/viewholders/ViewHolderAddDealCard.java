package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddDealActivity;
import com.example.muzafarimran.lastingsales.carditems.AddDealItem;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderAddDealCard extends RecyclerView.ViewHolder {

    private Button bAddDeal;
    private String leadLocalId;

    public ViewHolderAddDealCard(View v) {
        super(v);
        bAddDeal = v.findViewById(R.id.bAddDeal);
    }

    public void bind(Object item, int position, Context mContext) {

        final AddDealItem addDealItem = (AddDealItem) item;
        leadLocalId = addDealItem.leadLocalId;

        bAddDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddDealActivity.class);
                intent.putExtra(AddDealActivity.TAG_LAUNCH_MODE_CONTACT_ID, leadLocalId);
                mContext.startActivity(intent);
            }
        });
    }
}
