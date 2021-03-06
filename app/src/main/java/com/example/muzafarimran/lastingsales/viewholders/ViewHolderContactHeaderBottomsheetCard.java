package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.activities.LargeImageActivity;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.carditems.ContactHeaderBottomsheetItem;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.listeners.CloseContactBottomSheetEvent;
import com.example.muzafarimran.lastingsales.listeners.CloseInquiryBottomSheetEvent;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/1/2017.
 */

public class ViewHolderContactHeaderBottomsheetCard extends RecyclerView.ViewHolder {
    public static final String TAG = "ViewHolderContactCard";
    //    private final CardView cv_item;
    private final ConstraintLayout cl;
    private final LinearLayout llTypeRibbon;
    private SimpleDraweeView user_avatar;
    private TextView name;
    private ImageView call_icon;
    private ImageView imSmartBadge;
    private TextView numberDetailTextView;
    private Button bIgnore;
    private Button bSales;
    private LSContactProfile lsContactProfile;

    public ViewHolderContactHeaderBottomsheetCard(View v) {
        super(v);
        this.cl = v.findViewById(R.id.cl);
        this.user_avatar = v.findViewById(R.id.user_avatar);
        this.imSmartBadge = v.findViewById(R.id.imSmartBadge);
        this.llTypeRibbon = v.findViewById(R.id.llTypeRibbon);
        this.name = v.findViewById(R.id.tvContactName);
        this.call_icon = v.findViewById(R.id.call_icon);
        this.numberDetailTextView = v.findViewById(R.id.tvNumber);
        this.bSales = v.findViewById(R.id.bSalesUtaggedItem);
        this.bIgnore = v.findViewById(R.id.bIgnore);
    }

    public void bind(Object item, int position, Context mContext) {
        final ContactHeaderBottomsheetItem contactHeaderBottomsheetItem = (ContactHeaderBottomsheetItem) item;
        final LSContact contact = contactHeaderBottomsheetItem.lsContact;
        final String number = contact.getPhoneOne();

        final String contactType = contact.getContactType();

        lsContactProfile = contact.getContactProfile();
        if (lsContactProfile == null) {
            imSmartBadge.setVisibility(View.GONE);
            Log.d(TAG, "createOrUpdate: Not Found in contact Table now getting from ContactProfileProvider: " + contact.toString());
            lsContactProfile = LSContactProfile.getProfileFromNumber(number);
        } else {
            imSmartBadge.setVisibility(View.VISIBLE);
            Log.d(TAG, "createOrUpdate: Found in contact Table: " + contact);
        }
        if (contact.getContactName() != null) {
            if (contact.getContactName().equals("null")) {
                this.name.setText("");
            } else if (contact.getContactName().equals("Unlabeled Contact") || contact.getContactName().equals("Ignored Contact")) {
                String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(mContext, contact.getPhoneOne());
                if (name != null) {
                    this.name.setText(name);
                } else {
                    if (lsContactProfile != null) {
                        this.name.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
                    } else {
                        this.name.setText(contact.getPhoneOne());
                    }
                }
            } else {
                this.name.setText(contact.getContactName());
            }
        } else {
            String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(mContext, contact.getPhoneOne());
            if (name != null) {
                this.name.setText(name);
            } else {
                if (lsContactProfile != null) {
                    this.name.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
                } else {
                    this.name.setText(contact.getPhoneOne());
                }
            }
        }
        if (lsContactProfile != null) {
            if (lsContactProfile.getSocial_image() != null && !lsContactProfile.getSocial_image().equals("")) {
                imageFunc(this.user_avatar, lsContactProfile.getSocial_image(), mContext);
            } else {
                this.user_avatar.setImageResource(R.drawable.ic_account_circle);
                Drawable placeholderDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_account_circle);
                this.user_avatar.setHierarchy(
                        GenericDraweeHierarchyBuilder.newInstance(mContext.getResources())
                                .setPlaceholderImage(placeholderDrawable)
                                .build());
            }
        } else {
            this.user_avatar.setImageResource(R.drawable.ic_account_circle);
            Drawable placeholderDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_account_circle);
            this.user_avatar.setHierarchy(
                    GenericDraweeHierarchyBuilder.newInstance(mContext.getResources())
                            .setPlaceholderImage(placeholderDrawable)
                            .build());
        }

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

        this.bIgnore.setTag(number);
        this.cl.setTag(contact.getId());
        this.numberDetailTextView.setText(number);
//        this.time.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(contact.getBeginTime()));
        View.OnClickListener callClickListener = new CallClickListener(mContext);
        this.call_icon.setOnClickListener(callClickListener);
        this.call_icon.setTag(contact.getPhoneOne());
        this.bSales.setOnClickListener(view -> {
            if (contactHeaderBottomsheetItem.place.equals("contact")) {
                if (contactType.equals(LSContact.CONTACT_TYPE_UNLABELED)) {
                    Intent myIntent = new Intent(mContext, AddEditLeadActivity.class);
                    myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                    myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, contact.getPhoneOne() + "");
                    myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, "");
                    myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_UNLABELED);
                    mContext.startActivity(myIntent);
                } else if (contactType.equals(LSContact.CONTACT_TYPE_BUSINESS)) {
                    Intent myIntent = new Intent(mContext, AddEditLeadActivity.class);
                    myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                    myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact.getId() + "");
                    myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_COLLEAGUE);
                    mContext.startActivity(myIntent);
                } else if (contactType.equals(LSContact.CONTACT_TYPE_IGNORED)) {
                    Intent myIntent = new Intent(mContext, AddEditLeadActivity.class);
                    myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                    myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, contact.getId() + "");
                    myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_COLLEAGUE);
                    mContext.startActivity(myIntent);
                }
            } else if (contactHeaderBottomsheetItem.place.equals("inquiry")) {
                if (contact == null) {
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
        this.bIgnore.setOnClickListener(view -> {
//            String oldType = contact.getContactType();
            contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            contact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
            contact.save();
//            String newType = LSContact.CONTACT_TYPE_IGNORED;
//            TypeManager.ConvertTo(mContext, contact, oldType, newType);
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
            dataSenderAsync.run();

            Toast.makeText(mContext, "Added to Ignored Contact!", Toast.LENGTH_SHORT).show();

            CloseContactBottomSheetEvent closeContactBottomSheetEvent = new NavigationBottomMainActivity();
            closeContactBottomSheetEvent.closeContactBottomSheetCallback();

            CloseInquiryBottomSheetEvent closeInquiryBottomSheetEvent = new NavigationBottomMainActivity();
            closeInquiryBottomSheetEvent.closeInquiryBottomSheetCallback();

            TinyBus.from(mContext.getApplicationContext()).post(new LeadContactAddedEventModel());
        });
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