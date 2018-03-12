package com.example.muzafarimran.lastingsales.viewholders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.activities.AboutActivity;
import com.example.muzafarimran.lastingsales.activities.LogInActivity;
import com.example.muzafarimran.lastingsales.activities.SettingsActivity;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.carditems.MoreItem;
import com.mixpanel.android.mpmetrics.MixpanelAPI;


/**
 * Created by ibtisam on 11/8/2017.
 */

public class ViewHolderMoreCard extends RecyclerView.ViewHolder {


    private final TextView tvTitle;
    private final TextView tvDescription;
    private final ConstraintLayout cl;
    private final ImageView ivCardBackground;
    private  SessionManager sessionManager;

    public ViewHolderMoreCard(View v) {
        super(v);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvDescription = v.findViewById(R.id.tvTaskDescription);
        cl = v.findViewById(R.id.cl);
        ivCardBackground = v.findViewById(R.id.ivCardBackground);
    }

    public void bind(Object item, int position, Context mContext) {
        MoreItem moreItem = (MoreItem) item;
        tvTitle.setText(moreItem.text);
        tvDescription.setText(moreItem.description);
        ivCardBackground.setImageResource(moreItem.drawable);

        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (moreItem.text) {

                    case "About":
                        mContext.startActivity(new Intent(mContext, AboutActivity.class));
                        break;
                    case "Settings":
                        mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                        break;
                    case "Logout":
                        sessionManager = new SessionManager(mContext);
                        sessionManager.logoutUser();
                        mContext.startActivity(new Intent(mContext, LogInActivity.class));
                        ((Activity) mContext).finish();
                        String projectToken = MixpanelConfig.projectToken;
                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
                        mixpanel.track("User Logged Out");
                        break;
                    default:
                        if(moreItem.goAt != null){
                            mContext.startActivity(new Intent(mContext, moreItem.goAt));
                        }
                        break;
                }
            }
        });
    }
}