package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.activities.ContactCallDetails;
import com.example.muzafarimran.lastingsales.activities.ContactDetailsTabActivity;
import com.example.muzafarimran.lastingsales.providers.models.LSInquiry;
import com.example.muzafarimran.lastingsales.providers.models.LSNote;
import com.example.muzafarimran.lastingsales.providers.models.TempFollowUp;
import com.example.muzafarimran.lastingsales.utils.TypeManager;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ibtisam on 11/1/2017.
 */

public class ViewHolderUnlabeledCard extends RecyclerView.ViewHolder {
    public static final String TAG = "ViewHolderUnlabeledCard";
    private final CardView cv_item;

    CircleImageView user_avatar;
    TextView name;
    TextView time;
    ImageView call_icon;
    ImageView missed_call_icon;
    RelativeLayout rl_container_buttons;
    TextView numberDetailTextView;
    Button bIgnore;
    Button bSales;

    private TextView tvNameFromProfile;
    private TextView tvCityFromProfile;
    private TextView tvCountryFromProfile;
    private TextView tvWorkFromProfile;
    private TextView tvCompanyFromProfile;
    private TextView tvWhatsappFromProfile;
    private TextView tvTweeterFromProfile;
    private TextView tvLinkdnFromProfile;
    private TextView tvFbFromProfile;
    private RelativeLayout user_profile_group_wrapper;
    private View.OnClickListener callClickListener = null;

    public ViewHolderUnlabeledCard(View v) {
        super(v);
        this.cv_item = v.findViewById(R.id.cv_item);
        this.user_avatar = v.findViewById(R.id.user_avatar);
        this.name = v.findViewById(R.id.call_name);
        this.time = v.findViewById(R.id.call_time);
        this.call_icon = v.findViewById(R.id.call_icon);
        this.numberDetailTextView = v.findViewById(R.id.call_number);
        this.rl_container_buttons = v.findViewById(R.id.rl_container_buttons);
        this.bSales = v.findViewById(R.id.bSalesUtaggedItem);
        this.bIgnore = v.findViewById(R.id.bIgnore);

        this.tvNameFromProfile = v.findViewById(R.id.tvNameFromProfile);
        this.tvCityFromProfile = v.findViewById(R.id.tvCityFromProfile);
        this.tvCountryFromProfile = v.findViewById(R.id.tvCountryFromProfile);
        this.tvWorkFromProfile = v.findViewById(R.id.tvWorkFromProfile);
        this.tvCompanyFromProfile = v.findViewById(R.id.tvCompanyFromProfile);
        this.tvWhatsappFromProfile = v.findViewById(R.id.tvWhatsappFromProfile);
        this.tvTweeterFromProfile = v.findViewById(R.id.tvTweeterFromProfile);
        this.tvLinkdnFromProfile = v.findViewById(R.id.tvLinkdnFromProfile);
        this.tvFbFromProfile = v.findViewById(R.id.tvFbFromProfile);
        this.user_profile_group_wrapper = v.findViewById(R.id.user_profile_group_wrapper);
        this.user_profile_group_wrapper.setVisibility(View.GONE);
    }

    public void bind(Object item, int position, Context mContext) {
        final LSContact contact = (LSContact) item;
        final String number = contact.getPhoneOne();

        final String contactType = contact.getContactType();

        LSContactProfile lsContactProfile = contact.getContactProfile();
        if (lsContactProfile == null) {
            Log.d(TAG, "CreateOrUpdate: Not Found in contact Table now getting from ContactProfileProvider: " + contact.toString());
            lsContactProfile = LSContactProfile.getProfileFromNumber(number);
        } else {
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
            this.user_profile_group_wrapper.setVisibility(View.VISIBLE);

            if (lsContactProfile.getSocial_image() != null && !lsContactProfile.getSocial_image().equals("")) {
                imageFunc(this.user_avatar, lsContactProfile.getSocial_image(), mContext);
            } else {
                this.user_avatar.setImageResource(R.drawable.ic_account_circle);
            }


            if (lsContactProfile.getFirstName() != null) {
                this.tvNameFromProfile.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
            }
            if (lsContactProfile.getCity() != null) {
                this.tvCityFromProfile.setText(lsContactProfile.getCity());
            }
            if (lsContactProfile.getCountry() != null) {
                this.tvCountryFromProfile.setText(lsContactProfile.getCountry());
            }
            if (lsContactProfile.getWork() != null) {
                this.tvWorkFromProfile.setText(lsContactProfile.getWork());
            }
            if (lsContactProfile.getCompany() != null) {
                this.tvCompanyFromProfile.setText(lsContactProfile.getCompany());
            }
            if (lsContactProfile.getWhatsapp() != null) {
                this.tvWhatsappFromProfile.setText(lsContactProfile.getWhatsapp());
            }
            if (lsContactProfile.getTweet() != null) {
                this.tvTweeterFromProfile.setText(lsContactProfile.getTweet());
            }
            if (lsContactProfile.getLinkd() != null) {
                this.tvLinkdnFromProfile.setText(lsContactProfile.getLinkd());
            }
            if (lsContactProfile.getFb() != null) {
                this.tvFbFromProfile.setText(lsContactProfile.getFb());
            }
        } else {
            this.user_profile_group_wrapper.setVisibility(View.GONE);
            this.user_avatar.setImageResource(R.drawable.ic_account_circle);
        }

        this.bIgnore.setTag(number);
        this.cv_item.setTag(number);
        this.numberDetailTextView.setText(number);
//        this.time.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(contact.getBeginTime()));
        this.callClickListener = new CallClickListener(mContext);
        this.call_icon.setOnClickListener(this.callClickListener);
        this.call_icon.setTag(contact.getPhoneOne());
        this.bSales.setOnClickListener(view -> {
            Intent myIntent = new Intent(mContext, AddEditLeadActivity.class);
            myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
            myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, contact.getPhoneOne() + "");
            myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, "");
            myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_UNLABELED);
            mContext.startActivity(myIntent);
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
            Collection<LSContact> allUntaggedContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
//                setList(allUntaggedContacts);
            Toast.makeText(mContext, "Added to Ignored Contact!", Toast.LENGTH_SHORT).show();
        });

        switch (contactType) {
            case LSContact.CONTACT_TYPE_SALES:
                rl_container_buttons.setVisibility(View.GONE);
                // navigate to lead
                // dont show smart info
                // add delete followup button

                this.cv_item.setOnClickListener(view -> {
                    Intent detailsActivityIntent = new Intent(mContext, ContactDetailsTabActivity.class);
                    long contactId = contact.getId();
                    detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                    mContext.startActivity(detailsActivityIntent);
                });

                //  Deletes the contact, queries db and updates local list plus notifies adapter
                this.cv_item.setOnLongClickListener(view -> {

                    LSInquiry checkInquiry = LSInquiry.getInquiryByNumberIfExists(contact.getPhoneOne());
//                    if (checkInquiry == null) {
                    checkInquiry.delete();
                    //Flushing Notes Of lead
                    List<LSNote> allNotesOfThisContact = LSNote.getNotesByContactId(contact.getId());
                    if (allNotesOfThisContact != null && allNotesOfThisContact.size() > 0) {
                        for (LSNote oneNote : allNotesOfThisContact) {
                            oneNote.delete();
                        }
                    }
                    //Flushing Followup Of lead
                    List<TempFollowUp> allFollowupsOfThisContact = TempFollowUp.getFollowupsByContactId(contact.getId());
                    if (allFollowupsOfThisContact != null && allFollowupsOfThisContact.size() > 0) {
                        for (TempFollowUp oneFollowup : allFollowupsOfThisContact) {
                            oneFollowup.delete();
                        }
                    }
                    //contact is deleted and will be hard deleted on syncing.
                    contact.setLeadDeleted(true);
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
                    contact.save();
//                    contact.delete();
                    DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
                    dataSenderAsync.run();
                    // FIRE EVENT TO REFRESH LIST
                    Snackbar.make(view, "Lead deleted!", Snackbar.LENGTH_SHORT).show();
//                    }else {
//                        Toast.makeText(mContext, "Please Handle Inquiry First", Toast.LENGTH_SHORT).show();
//                    }

                    return true;
                });
                break;

            case LSContact.CONTACT_TYPE_BUSINESS:
                rl_container_buttons.setVisibility(View.VISIBLE);
                // navigate to details
                // show smart info

                this.cv_item.setOnClickListener(view -> {
                    Intent myIntent = new Intent(mContext, ContactCallDetails.class);
                    myIntent.putExtra("number", (String) view.getTag());
                    mContext.startActivity(myIntent);
                });

                this.cv_item.setOnLongClickListener(view -> {

                    LSInquiry checkInquiry = LSInquiry.getInquiryByNumberIfExists(contact.getPhoneOne());
//                if (checkInquiry == null) {
                    contact.setLeadDeleted(true);
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
                    contact.delete();
                    Snackbar.make(view, "Personal contact deleted!", Snackbar.LENGTH_SHORT).show();
                    // FIRE EVENT TO REFRESH LIST
                    DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
                    dataSenderAsync.run();
//                } else {
//                    Toast.makeText(mContext, "Please Handle Inquiry First", Toast.LENGTH_SHORT).show();
//                }
                    return true;
                });
                break;

            case LSContact.CONTACT_TYPE_UNLABELED:
                rl_container_buttons.setVisibility(View.VISIBLE);

                this.cv_item.setOnClickListener(view -> {
                    Intent myIntent = new Intent(mContext, ContactCallDetails.class);
                    myIntent.putExtra("number", (String) view.getTag());
                    mContext.startActivity(myIntent);
                });

                this.cv_item.setOnLongClickListener(view -> {
                    Snackbar.make(view, "Can not delete unlabeled contact", Snackbar.LENGTH_SHORT).show();
                    return true;
                });
                break;

            case LSContact.CONTACT_TYPE_IGNORED:
                //No profile layout shown
                rl_container_buttons.setVisibility(View.GONE);

                //Remove ignore button

                //No navigation on click

                this.cv_item.setOnLongClickListener(view -> {

                    LSInquiry checkInquiry = LSInquiry.getInquiryByNumberIfExists(contact.getPhoneOne());
//                if (checkInquiry == null) {
                    contact.setLeadDeleted(true);
                    contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_DELETE_NOT_SYNCED);
                    contact.save();
                    DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
                    dataSenderAsync.run();
                    // FIRE EVENT TO REFRESH LIST
                    Snackbar.make(view, "Ignored Contact Deleted!", Snackbar.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(mContext, "Please Handle Inquiry First", Toast.LENGTH_SHORT).show();
//                }
                    return true;
                });
                break;
            default:
        }
    }

    private void imageFunc(CircleImageView imageView, String url, Context context) {
        //Downloading using Glide Library
        Glide.with(context)
                .load(url)
//                .override(48, 48)
//                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_account_circle)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}