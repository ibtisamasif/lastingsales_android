package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ErrorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderSocialProfileCard extends RecyclerView.ViewHolder {

    private TextView tvNameFromProfile;
    private TextView tvCityFromProfile;
    private TextView tvCountryFromProfile;
    private TextView tvWorkFromProfile;
    private TextView tvCompanyFromProfile;
    private TextView tvWhatsappFromProfile;
    private TextView tvTweeterFromProfile;
    private TextView tvLinkdnFromProfile;
    private TextView tvFbFromProfile;
//    private SimpleDraweeView user_avatar_ind;


    public ViewHolderSocialProfileCard(View v) {
        super(v);
//        user_avatar_ind = v.findViewById(R.id.user_avatar_ind);
        tvNameFromProfile = v.findViewById(R.id.tvNameFromProfile);
        tvCityFromProfile = v.findViewById(R.id.tvCityFromProfile);
        tvCountryFromProfile = v.findViewById(R.id.tvCountryFromProfile);
        tvWorkFromProfile = v.findViewById(R.id.tvWorkFromProfile);
        tvCompanyFromProfile = v.findViewById(R.id.tvCompanyFromProfile);
        tvWhatsappFromProfile = v.findViewById(R.id.tvWhatsappFromProfile);
        tvTweeterFromProfile = v.findViewById(R.id.tvTweeterFromProfile);
        tvLinkdnFromProfile = v.findViewById(R.id.tvLinkdnFromProfile);
        tvFbFromProfile = v.findViewById(R.id.tvFbFromProfile);
    }

    public void bind(Object item, int position, Context mContext) {

        LSContactProfile lsContactProfile = (LSContactProfile) item;

        tvTweeterFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvLinkdnFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvFbFromProfile.setMovementMethod(LinkMovementMethod.getInstance());

        if (lsContactProfile != null) {
//            if (lsContactProfile.getSocial_image() != null) {
//                MyDateTimeStamp.setFrescoImage(user_avatar_ind, lsContactProfile.getSocial_image());
//            }
            if (lsContactProfile.getFirstName() != null) {
                tvNameFromProfile.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
            }
            if (lsContactProfile.getCity() != null) {
                tvCityFromProfile.setText(lsContactProfile.getCity());
            }
            if (lsContactProfile.getCountry() != null) {
                tvCountryFromProfile.setText(lsContactProfile.getCountry());
            }
            if (lsContactProfile.getWork() != null) {
                tvWorkFromProfile.setText(lsContactProfile.getWork());
            }
            if (lsContactProfile.getCompany() != null) {
                tvCompanyFromProfile.setText(lsContactProfile.getCompany());
            }
            if (lsContactProfile.getWhatsapp() != null) {
                tvWhatsappFromProfile.setText(lsContactProfile.getWhatsapp());
            }
            if (lsContactProfile.getTweet() != null) {
                tvTweeterFromProfile.setText(lsContactProfile.getTweet());
            }
            if (lsContactProfile.getLinkd() != null) {
                tvLinkdnFromProfile.setText(lsContactProfile.getLinkd());
            }
            if (lsContactProfile.getFb() != null) {
                tvFbFromProfile.setText(lsContactProfile.getFb());
            }
        }

    }
}
