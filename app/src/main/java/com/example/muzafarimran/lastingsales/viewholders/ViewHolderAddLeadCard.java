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
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.carditems.AddLeadItem;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderAddLeadCard extends RecyclerView.ViewHolder {

    private Button bAddLead;
    private Button bImport;

    public ViewHolderAddLeadCard(View v) {
        super(v);
        bAddLead = v.findViewById(R.id.bAddLead);
        bImport = v.findViewById(R.id.bImport);
    }

    public void bind(Object item, int position, Context mContext) {

        final AddLeadItem addLeadItem = (AddLeadItem) item;
        bAddLead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, AddEditLeadActivity.class);
                intent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                intent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_CARD);
                mContext.startActivity(intent);
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
                mixpanel.track("Create lead dialog - opened");
            }
        });

        bImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(mContext, AddEditLeadActivity.class);
                intent1.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_IMPORT_CONTACT);
                intent1.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_CARD);
                mContext.startActivity(intent1);
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
                mixpanel.track("Create lead dialog - opened");
            }
        });

    }
}
