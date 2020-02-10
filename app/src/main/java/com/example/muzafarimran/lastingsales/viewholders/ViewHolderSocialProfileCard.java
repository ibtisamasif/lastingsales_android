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

    private TextView tvNameFromProfileTitle;
    private TextView tvCityFromProfileTitle;
    private TextView tvCountryFromProfileTitle;
    private TextView tvWorkFromProfileTitle;
    private TextView tvCompanyFromProfileTitle;
    private TextView tvWhatsappFromProfileTitle;
    private TextView tvTweeterFromProfileTitle;
    private TextView tvLinkdnFromProfileTitle;
    private TextView tvFbFromProfileTitle;
//    private SimpleDraweeView user_avatar_ind;


    public ViewHolderSocialProfileCard(View v) {
        super(v);
//        user_avatar_ind = v.findViewById(R.id.user_avatar_ind);
        tvNameFromProfile = v.findViewById(R.id.tvNameFromProfile);
        tvNameFromProfileTitle = v.findViewById(R.id.tvNameFromProfileTitle);
        tvCityFromProfile = v.findViewById(R.id.tvCityFromProfile);
        tvCityFromProfileTitle = v.findViewById(R.id.tvCityFromProfileTitle);
        tvCountryFromProfile = v.findViewById(R.id.tvCountryFromProfile);
        tvCountryFromProfileTitle = v.findViewById(R.id.tvCountryFromProfileTitle);
        tvWorkFromProfile = v.findViewById(R.id.tvWorkFromProfile);
        tvWorkFromProfileTitle = v.findViewById(R.id.tvWorkFromProfileTitle);
        tvCompanyFromProfile = v.findViewById(R.id.tvCompanyFromProfile);
        tvCompanyFromProfileTitle = v.findViewById(R.id.tvCompanyFromProfileTitle);
        tvWhatsappFromProfile = v.findViewById(R.id.tvWhatsappFromProfile);
        tvWhatsappFromProfileTitle = v.findViewById(R.id.tvWhatsappFromProfileTitle);
        tvTweeterFromProfile = v.findViewById(R.id.tvTweeterFromProfile);
        tvTweeterFromProfileTitle = v.findViewById(R.id.tvTweeterFromProfileTitle);
        tvLinkdnFromProfile = v.findViewById(R.id.tvLinkdnFromProfile);
        tvLinkdnFromProfileTitle = v.findViewById(R.id.tvLinkdnFromProfileTitle);
        tvFbFromProfile = v.findViewById(R.id.tvFbFromProfile);
        tvFbFromProfileTitle = v.findViewById(R.id.tvFbFromProfileTitle);
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
            if (lsContactProfile.getFirstName() != null && !lsContactProfile.getFirstName().equals("")) {
                tvNameFromProfile.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
            }else {
                tvNameFromProfile.setVisibility(View.GONE);
                tvNameFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getCity() != null && !lsContactProfile.getCity().equals("")) {
                tvCityFromProfile.setText(lsContactProfile.getCity());
            } else {
                tvCityFromProfile.setVisibility(View.GONE);
                tvCityFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getCountry() != null && !lsContactProfile.getCountry().equals("")) {
                tvCountryFromProfile.setText(lsContactProfile.getCountry());
            } else {
                tvCountryFromProfile.setVisibility(View.GONE);
                tvCountryFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getWork() != null && !lsContactProfile.getWork().equals("")) {
                tvWorkFromProfile.setText(lsContactProfile.getWork());
            } else {
                tvWorkFromProfile.setVisibility(View.GONE);
                tvWorkFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getCompany() != null && !lsContactProfile.getCompany().equals("")) {
                tvCompanyFromProfile.setText(lsContactProfile.getCompany());
            } else {
                tvCompanyFromProfile.setVisibility(View.GONE);
                tvCompanyFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getWhatsapp() != null && !lsContactProfile.getWhatsapp().equals("")) {
                tvWhatsappFromProfile.setText(lsContactProfile.getWhatsapp());
            } else {
                tvWhatsappFromProfile.setVisibility(View.GONE);
                tvWhatsappFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getTweet() != null && !lsContactProfile.getTweet().equals("")) {
                tvTweeterFromProfile.setText(lsContactProfile.getTweet());
            } else {
                tvTweeterFromProfile.setVisibility(View.GONE);
                tvTweeterFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getLinkd() != null && !lsContactProfile.getLinkd().equals("")) {
                tvLinkdnFromProfile.setText(lsContactProfile.getLinkd());
            } else {
                tvLinkdnFromProfile.setVisibility(View.GONE);
                tvLinkdnFromProfileTitle.setVisibility(View.GONE);
            }
            if (lsContactProfile.getFb() != null && !lsContactProfile.getFb().equals("")) {
                tvFbFromProfile.setText(lsContactProfile.getFb());
            } else {
                tvFbFromProfile.setVisibility(View.GONE);
                tvFbFromProfileTitle.setVisibility(View.GONE);
            }
        }

    }
}
