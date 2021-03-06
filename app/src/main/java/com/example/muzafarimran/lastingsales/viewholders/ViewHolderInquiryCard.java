package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.activities.LargeImageActivity;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.net.URLEncoder;
import java.util.Calendar;

import static android.view.View.GONE;


/**
 * Created by ibtisam on 11/1/2017.
 */

public class ViewHolderInquiryCard extends RecyclerView.ViewHolder {
    public static final String TAG = "ViewHolderInquiryCard";

    private final CardView cv_item;
    private final SimpleDraweeView user_avatar;
    private final TextView name;
    private final TextView time;
    private final ImageView whatsapp_icon;
    private final ImageView call_icon;
    private final TextView number;
    //    private final Button bIgnore;
    private final Button bTag;
    //    private TextView inquireyCount;
    private View.OnClickListener callClickListener = null;
    private LSContactProfile lsContactProfile;


    public ViewHolderInquiryCard(View view) {
        super(view);
        this.cv_item = view.findViewById(R.id.cv_item);
        this.user_avatar = view.findViewById(R.id.user_avatar);
        this.name = view.findViewById(R.id.tvContactName);
        this.time = view.findViewById(R.id.call_time);
        this.whatsapp_icon = view.findViewById(R.id.whatsapp_icon);
        this.call_icon = view.findViewById(R.id.call_icon);
        this.number = view.findViewById(R.id.tvNumber);
//        this.bIgnore = view.findViewById(R.id.bIgnore);
//        this.inquireyCount = view.findViewById(R.id.inquireyCount);
        this.bTag = view.findViewById(R.id.bTag);
        this.bTag.setVisibility(GONE);
    }

    public void bind(Object item, int position, Context mContext) {
        final LSInquiry inquiryCall = (LSInquiry) item;
        final String number = inquiryCall.getContactNumber();

        //        LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(number); // performance drawbacks with this function
        lsContactProfile = inquiryCall.getContactProfile();
        if (lsContactProfile == null) {
            Log.d(TAG, "createOrUpdate: Not Found in LSInquiry Table now getting from ContactProfileProvider");
//            ContactProfileProvider contactProfileProvider = new ContactProfileProvider(mContext);
//            contactProfileProvider.getContactProfile(inquiryCall.getContactNumber(), new LSContactProfileCallback() {
//                @Override
//                public void onSuccess(LSContactProfile result) {
//                    Log.d(TAG, "onSuccess: CALLBACK RECEIVED");
//                    lsContactProfile = result;
//                }
//            });
            lsContactProfile = LSContactProfile.getProfileFromNumber(number);
        } else {
            Log.d(TAG, "createOrUpdate: Found in LSInquiry Table");
        }
        if (inquiryCall.getContact() == null) {
            Log.d(TAG, "getView: inquiryCall.getContact() == null: " + number);
            // TAG button visibility
//            this.bTag.setVisibility(View.VISIBLE);
            if (inquiryCall.getContactName() != null) {
                this.name.setText(inquiryCall.getContactName());      //Name from App
            } else {
                Log.d(TAG, "getView: else 1: " + number);
                this.name.setText(inquiryCall.getContactNumber());
                String phoneBookContactName = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(mContext, number);
                if (phoneBookContactName != null) {                     //Name from PhoneBook
                    Log.d(TAG, "getView: else 1 nested if: " + number);
                    this.name.setText(phoneBookContactName);
                } else if (lsContactProfile != null) {     //Name from Profile
                    Log.d(TAG, "getView: Name from Profile");
                    this.name.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
                }
            }
        } else {
            // TAG button visibility
//            if (!inquiryCall.getContact().getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
//                Log.d(TAG, "getView: not a sale contact: " + number);
//                this.bTag.setVisibility(View.VISIBLE);
//            }
            if (inquiryCall.getContact().getContactName() != null) {
                Log.d(TAG, "getView: inquiryCall.getContact().getContactName() != null " + number);
                this.name.setText(inquiryCall.getContact().getContactName());
            } else {
                Log.d(TAG, "getView: inquiryCall.getContact().getContactName() == null " + number);
                String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(mContext, number);
                if (name != null) {
                    this.name.setText(name);
                } else {
                    if (lsContactProfile != null) {
                        this.name.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
                    } else {
                        this.name.setText(inquiryCall.getContactNumber());
                    }
                }
            }
            if (inquiryCall.getContact().getContactProfile() != null) {
                Log.d(TAG, "getView: Inquiry Profile Exists");
            }
        }

        if (lsContactProfile != null) {
            if (lsContactProfile.getSocial_image() != null) {
                imageFunc(this.user_avatar, lsContactProfile.getSocial_image(), mContext);
            } else {
                this.user_avatar.setImageResource(R.drawable.ic_account_circle);
            }
        } else {
            this.user_avatar.setImageResource(R.drawable.ic_account_circle);
        }

        this.cv_item.setTag(number);
//        this.bIgnore.setTag(number);
        this.number.setText(number);
//        this.call_name_time.setTag(position);

        long callTimeMillis = inquiryCall.getBeginTime();
        long now = Calendar.getInstance().getTimeInMillis();
        long agoTimestamp = now - callTimeMillis;

        if (agoTimestamp > 300000) {
            this.time.setTextColor(Color.parseColor("#ff0000"));
        } else {
            this.time.setTextColor(Color.parseColor("#d0898989"));
        }
        this.time.setText(PhoneNumberAndCallUtils.getTimeAgo(inquiryCall.getBeginTime(), mContext));

//        if (inquiryCall.getCountOfInquiries() > 0) {
//            this.inquireyCount.setText(inquiryCall.getCountOfInquiries() + "");
//        }
        this.callClickListener = new CallClickListener(mContext);
        this.call_icon.setOnClickListener(this.callClickListener);
        this.call_icon.setTag(inquiryCall.getContactNumber());

        this.cv_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationBottomMainActivity navigationBottomMainActivity = (NavigationBottomMainActivity) mContext;
                navigationBottomMainActivity.openInquiryBottomSheetCallback((String) view.getTag());
            }
        });

        this.bTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LSContact checkContact = LSContact.getContactFromNumber(number);
                if (checkContact == null) {
                    Intent myIntent = new Intent(mContext, AddEditLeadActivity.class);
                    myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_ADD_NEW_CONTACT);
                    myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, number);
                    myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_INQUIRY);
                    mContext.startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent(mContext, AddEditLeadActivity.class);
                    myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                    myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, number);
                    myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, "");
                    myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_INQUIRY);
                    mContext.startActivity(myIntent);
                }
            }
        });

        this.whatsapp_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager packageManager = mContext.getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode("", "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        mContext.startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MixpanelAPI.getInstance(mContext, MixpanelConfig.projectToken).track("Whatsapp Clicked");
            }
        });

        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lsContactProfile != null && lsContactProfile.getSocial_image() != null && !lsContactProfile.getSocial_image().equals("")) {
                    mContext.startActivity(new Intent(mContext, LargeImageActivity.class).putExtra(LargeImageActivity.IMAGE_URL, lsContactProfile.getSocial_image()));
                    //"https://scontent.fkhi1-1.fna.fbcdn.net/v/t1.0-1/p240x240/18485518_1354609427965668_4711169246440864616_n.jpg?_nc_eui2=v1%3AAeFsCbGYLqNrQxQt94LYc1q0t2Z3XYGYUVQgYorL9vgCcLW33wn7pHoAhdZCi1QMrEWj3QZXdvHRtkKtkx_-h4_ztNtaj9uiXSjKttxU2gB6VA&oh=90841f4b57e67147d6f2868e15a23aa1&oe=5AAD2CFB"
//                mContext.startActivity(new Intent(mContext, ZoomActivity.class));
                }
            }
        });

//        this.bIgnore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LSContact tempContact = LSContact.getContactFromNumber(number);
//                String oldType = tempContact.getContactType();
//                tempContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
//                tempContact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
//                tempContact.save();
//                String newType = LSContact.CONTACT_TYPE_IGNORED;
//                TypeManager.ConvertTo(mContext, tempContact, oldType, newType);
//                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
//                dataSenderAsync.run();
//                List<LSInquiry> inquiries = LSInquiry.getAllPendingInquiriesInDescendingOrder();
//                Toast.makeText(mContext, "Added to Ignored Contact!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void imageFunc(SimpleDraweeView imageView, String url, Context context) {
        Uri uri = Uri.parse(url);
        imageView.setImageURI(uri);

//        //Downloading using Glide Library
//        Glide.with(context)
//                .load(url)
////                .override(48, 48)
////                .placeholder(R.drawable.placeholder)
//                .error(R.drawable.ic_account_circle)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(imageView);
    }
}
