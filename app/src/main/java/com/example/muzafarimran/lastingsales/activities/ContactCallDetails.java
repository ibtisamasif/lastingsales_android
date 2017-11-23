package com.example.muzafarimran.lastingsales.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;

import android.support.v4.widget.NestedScrollView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.IndividualContactCallAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.example.muzafarimran.lastingsales.utils.TypeManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orm.query.Condition;
import com.orm.query.Select;
import java.util.ArrayList;

public class ContactCallDetails extends BottomSheetDialogFragment {
    private static final String TAG = "ContactCallDetails";
    private static final String CONTACT_ID = "contact_id";

    private String number = "";
    private String name = "";
    private IndividualContactCallAdapter indadapter;
    private TextView contact_name_ind;
    private SimpleDraweeView user_avatar_ind;
    private TextView tvNameFromProfile;
    private TextView tvCityFromProfile;
    private TextView tvCountryFromProfile;
    private TextView tvWorkFromProfile;
    private TextView tvCompanyFromProfile;
    private TextView tvWhatsappFromProfile;
    private TextView tvTweeterFromProfile;
    private TextView tvLinkdnFromProfile;
    private TextView tvFbFromProfile;
    private Button bTrack;
    private Button bIgnore;

    public static ContactCallDetails newInstance(Long contact_id, int page) {
        ContactCallDetails fragmentFirst = new ContactCallDetails();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putLong(CONTACT_ID, contact_id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {

        View view = View.inflate(getContext(), R.layout.activity_contact_call_details, null);
        dialog.setContentView(view);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            ((BottomSheetBehavior) behavior).setPeekHeight(300);
        }

        Long contactId = getArguments().getLong(CONTACT_ID);
        LSContact selectedContact = LSContact.findById(LSContact.class, contactId);
        this.number = selectedContact.getPhoneOne();
        LSContact contact = LSContact.getContactFromNumber(number);
        if (contact != null) {
            this.name = contact.getContactName();
//            bTagButton.setVisibility(GONE);
        } else {
            this.name = "UNKNOWN";
        }

        ArrayList<LSCall> allCallsOfThisContact = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();
        CallClickListener callClickListener = new CallClickListener(getActivity());
        ((TextView) (view.findViewById(R.id.call_numbe_ind))).setText(this.number);
        String contactName = this.name;
        view.findViewById(R.id.call_icon_ind).setTag(this.number);
        view.findViewById(R.id.call_icon_ind).setOnClickListener(callClickListener);
        //hide tag button if name is not stored
        if (this.name == null || (this.name).isEmpty()) {
            contactName = this.name;
        }
        contact_name_ind = view.findViewById(R.id.contact_name_ind);
        contact_name_ind.setText(contactName);
        user_avatar_ind = view.findViewById(R.id.user_avatar_ind);
        bTrack = view.findViewById(R.id.bTrack);
        bTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), AddEditLeadActivity.class);
                myIntent.putExtra(AddEditLeadActivity.ACTIVITY_LAUNCH_MODE, AddEditLeadActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
                myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_PHONE_NUMBER, contact.getPhoneOne() + "");
                myIntent.putExtra(AddEditLeadActivity.TAG_LAUNCH_MODE_CONTACT_ID, "");
                myIntent.putExtra(AddEditLeadActivity.MIXPANEL_SOURCE, AddEditLeadActivity.MIXPANEL_SOURCE_UNLABELED);
                getActivity().startActivity(myIntent);
//                Snackbar.make(view, "Added to Contact!", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Added to Contact!", Toast.LENGTH_SHORT).show();
            }
        });
        bIgnore = view.findViewById(R.id.bIgnore);
        bIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldType = contact.getContactType();
                contact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                contact.setContactType(LSContact.CONTACT_TYPE_IGNORED);
                contact.save();
                String newType = LSContact.CONTACT_TYPE_IGNORED;
                TypeManager.ConvertTo(getActivity(), contact, oldType, newType);
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getActivity().getApplicationContext());
                dataSenderAsync.run();
//                Collection<LSContact> allUntaggedContacts = LSContact.getContactsByTypeInDescOrder(LSContact.CONTACT_TYPE_UNLABELED);
//                setList(allUntaggedContacts);
//                Snackbar.make(view, "Added to Ignored Contact!", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Added to Ignored Contact!", Toast.LENGTH_SHORT).show();
            }
        });

        View includeContactProfile = view.findViewById(R.id.includeContactProfile);
        TextView tvContactProfileSeparator = (TextView) includeContactProfile.findViewById(R.id.tvSeparator);
        tvContactProfileSeparator.setText("Social Profile");

        tvNameFromProfile = view.findViewById(R.id.tvNameFromProfile);
        tvCityFromProfile = view.findViewById(R.id.tvCityFromProfile);
        tvCountryFromProfile = view.findViewById(R.id.tvCountryFromProfile);
        tvWorkFromProfile = view.findViewById(R.id.tvWorkFromProfile);
        tvCompanyFromProfile = view.findViewById(R.id.tvCompanyFromProfile);
        tvWhatsappFromProfile = view.findViewById(R.id.tvWhatsappFromProfile);
        tvTweeterFromProfile = view.findViewById(R.id.tvTweeterFromProfile);
        tvLinkdnFromProfile = view.findViewById(R.id.tvLinkdnFromProfile);
        tvFbFromProfile = view.findViewById(R.id.tvFbFromProfile);

        View includeCallHistory = view.findViewById(R.id.includeCallHistory);
        TextView tvContactCallHistorySeparator = (TextView) includeCallHistory.findViewById(R.id.tvSeparator);
        tvContactCallHistorySeparator.setText("Call History");

        tvTweeterFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvLinkdnFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvFbFromProfile.setMovementMethod(LinkMovementMethod.getInstance());

        LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(number);
        if (lsContactProfile != null) {
            if (lsContactProfile.getSocial_image() != null) {
                MyDateTimeStamp.setFrescoImage(user_avatar_ind, lsContactProfile.getSocial_image());
            }
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

        com.example.muzafarimran.lastingsales.customview.NonScrollListView listview = view.findViewById(R.id.calls_list);
        indadapter = new IndividualContactCallAdapter(getActivity(), allCallsOfThisContact);
        Log.d(TAG, "setUpList: Size " + allCallsOfThisContact.size());
        for (LSCall oneCall : allCallsOfThisContact) {
            Log.d(TAG, "setUpList: " + oneCall.toString());
        }
        listview.setAdapter(indadapter);

//        getSupportActionBar().setTitle("Details");

        NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.bottom_sheet);
        nestedScrollView.setScrollY(0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_action_edit:

                break;
            case android.R.id.home:
//                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}