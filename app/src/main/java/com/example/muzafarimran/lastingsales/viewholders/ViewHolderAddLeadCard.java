package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.carditems.AddLeadItem;
import com.example.muzafarimran.lastingsales.carditems.HomeItem;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderAddLeadCard extends RecyclerView.ViewHolder {

    private Button bAddLead;



    public ViewHolderAddLeadCard(View v) {
        super(v);

        bAddLead = v.findViewById(R.id.bAddLead);

    }

    public void bind(Object item, int position, Context mContext) {

        final AddLeadItem addLeadItem = (AddLeadItem) item;
        bAddLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, AddEditLeadActivity.class);
                intent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
                mContext.startActivity(intent);

//                Intent intent1 = new Intent(mContext, AddEditLeadActivity.class);
//                intent1.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_IMPORT_CONTACT);
//                intent1.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_FAB);
//                mContext.startActivity(intent1);

            }
        });

    }
}
