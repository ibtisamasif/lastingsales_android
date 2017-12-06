package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.carditems.ContactHeaderBottomsheetItem;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utils.TypeManager;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 11/1/2017.
 */

public class ViewHolderContactHeaderBottomsheetCard extends RecyclerView.ViewHolder {
    public static final String TAG = "ViewHolderUnlabeledCard";
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

    public ViewHolderContactHeaderBottomsheetCard(View v) {
        super(v);
        this.cl = v.findViewById(R.id.cl);
        this.imSmartBadge = v.findViewById(R.id.imSmartBadge);
        this.llTypeRibbon = v.findViewById(R.id.llTypeRibbon);
        this.user_avatar = v.findViewById(R.id.user_avatar);
        this.name = v.findViewById(R.id.call_name);
        this.call_icon = v.findViewById(R.id.call_icon);
        this.numberDetailTextView = v.findViewById(R.id.call_number);
        this.bSales = v.findViewById(R.id.bSalesUtaggedItem);
        this.bIgnore = v.findViewById(R.id.bIgnore);
    }

    public void bind(Object item, int position, Context mContext) {
        final ContactHeaderBottomsheetItem contactHeaderBottomsheetItem = (ContactHeaderBottomsheetItem) item;
        final LSContact contact = contactHeaderBottomsheetItem.lsContact;
        final String number = contact.getPhoneOne();

        final String contactType = contact.getContactType();

        LSContactProfile lsContactProfile = contact.getContactProfile();
        if (lsContactProfile == null) {
            imSmartBadge.setVisibility(View.GONE);
            Log.d(TAG, "CreateOrUpdate: Not Found in contact Table now getting from ContactProfileProvider: " + contact.toString());
            lsContactProfile = LSContactProfile.getProfileFromNumber(number);
        } else {
            imSmartBadge.setVisibility(View.VISIBLE);
            Log.d(TAG, "CreateOrUpdate: Found in contact Table: " + contact);
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
            if (lsContactProfile != null) {
                this.name.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
            } else {
                this.name.setText(contact.getPhoneOne());
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
            String oldType = contact.getContactType();
            contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
            contact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
            contact.save();
            String newType = LSContact.CONTACT_TYPE_IGNORED;
            TypeManager.ConvertTo(mContext, contact, oldType, newType);
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
            dataSenderAsync.run();
//            Collection<LSContact> allUntaggedContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
//                setList(allUntaggedContacts);
            Toast.makeText(mContext, "Added to Ignored Contact!", Toast.LENGTH_SHORT).show();
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