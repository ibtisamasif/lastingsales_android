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
import com.example.muzafarimran.lastingsales.carditems.SettingItem;
import com.mixpanel.android.mpmetrics.MixpanelAPI;


/**
 * Created by ibtisam on 11/8/2017.
 */

public class ViewHolderSettingCard extends RecyclerView.ViewHolder {
    private ConstraintLayout cl_container_setting_item;
    private TextView textView;
    private ImageView imageView;
    SessionManager sessionManager;

    public ViewHolderSettingCard(View v) {
        super(v);
        cl_container_setting_item = v.findViewById(R.id.cl_container_setting_item);
        textView = v.findViewById(R.id.tvTitle);
        imageView = v.findViewById(R.id.imageView);

    }

    public void bind(Object item, int position, Context mContext) {

        final SettingItem settingItem = (SettingItem) item;
        textView.setText(settingItem.text);
        if (settingItem.drawable != 0){
            imageView.setImageResource(settingItem.drawable);
        }
        cl_container_setting_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (settingItem.text) {

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
                        if(settingItem.goAt != null){
                            mContext.startActivity(new Intent(mContext, settingItem.goAt));
                        }
                        break;
                }
            }
        });
    }
}
