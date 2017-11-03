package com.example.muzafarimran.lastingsales.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.IndividualContactCallAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactCallDetails extends AppCompatActivity {
    private static final String TAG = "ContactCallDetails";
    private String number = "";
    private String name = "";
    private IndividualContactCallAdapter indadapter;
    private TextView contact_name_ind;
    private CircleImageView user_avatar_ind;
    private TextView tvNameFromProfile;
    private TextView tvCityFromProfile;
    private TextView tvCountryFromProfile;
    private TextView tvWorkFromProfile;
    private TextView tvCompanyFromProfile;
    private TextView tvWhatsappFromProfile;
    private TextView tvTweeterFromProfile;
    private TextView tvLinkdnFromProfile;
    private TextView tvFbFromProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_call_details_ap_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationIcon(upArrow);

        Intent intent = getIntent();
        this.number = intent.getStringExtra("number");
        LSContact contact = LSContact.getContactFromNumber(this.number);
        if (contact != null) {
            this.name = contact.getContactName();
//            bTagButton.setVisibility(GONE);
        } else {
            this.name = "UNKNOWN";
        }

        ArrayList<LSCall> allCallsOfThisContact = (ArrayList<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(this.number)).orderBy("begin_time DESC").list();
        CallClickListener callClickListener = new CallClickListener(ContactCallDetails.this);
        ((TextView) (this.findViewById(R.id.call_numbe_ind))).setText(this.number);
        String contactName = this.name;
        this.findViewById(R.id.call_icon_ind).setTag(this.number);
        this.findViewById(R.id.call_icon_ind).setOnClickListener(callClickListener);
        //hide tag button if name is not stored
        if (this.name == null || (this.name).isEmpty()) {
//            ((Button) (this.findViewById(R.id.b_tag_individual_contact_call_screen))).setVisibility(GONE);
            contactName = this.name;
        }
        contact_name_ind = findViewById(R.id.contact_name_ind);
        contact_name_ind.setText(contactName);
        user_avatar_ind = findViewById(R.id.user_avatar_ind);
        tvNameFromProfile = findViewById(R.id.tvNameFromProfile);
        tvCityFromProfile = findViewById(R.id.tvCityFromProfile);
        tvCountryFromProfile = findViewById(R.id.tvCountryFromProfile);
        tvWorkFromProfile = findViewById(R.id.tvWorkFromProfile);
        tvCompanyFromProfile = findViewById(R.id.tvCompanyFromProfile);
        tvWhatsappFromProfile = findViewById(R.id.tvWhatsappFromProfile);
        tvTweeterFromProfile = findViewById(R.id.tvTweeterFromProfile);
        tvLinkdnFromProfile = findViewById(R.id.tvLinkdnFromProfile);
        tvFbFromProfile = findViewById(R.id.tvFbFromProfile);

        tvTweeterFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvLinkdnFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvFbFromProfile.setMovementMethod(LinkMovementMethod.getInstance());

        LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(number);
        if (lsContactProfile != null) {
            if (lsContactProfile.getSocial_image() != null) {
                imageFunc(user_avatar_ind, lsContactProfile.getSocial_image());
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

        ListView listview = this.findViewById(R.id.calls_list);
        indadapter = new IndividualContactCallAdapter(ContactCallDetails.this, allCallsOfThisContact);
        Log.d(TAG, "setUpList: Size " + allCallsOfThisContact.size());
        for (LSCall oneCall : allCallsOfThisContact) {
            Log.d(TAG, "setUpList: " + oneCall.toString());
        }
        listview.setAdapter(indadapter);

        getSupportActionBar().setTitle("Details");
    }

    private void imageFunc(CircleImageView imageView, String url) {
        //Downloading using Glide Library
        Glide.with(this)
                .load(url)
//                .override(48, 48)
//                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_account_circle_white)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_action_edit:
//                Intent addContactScreenIntent = new Intent(getApplicationContext(), TagNumberAndAddFollowupActivity.class);
//                addContactScreenIntent.putExtra(TagNumberAndAddFollowupActivity.ACTIVITY_LAUNCH_MODE, TagNumberAndAddFollowupActivity.LAUNCH_MODE_EDIT_EXISTING_CONTACT);
//                addContactScreenIntent.putExtra(TagNumberAndAddFollowupActivity.TAG_LAUNCH_MODE_CONTACT_ID, contactIdString);
//                startActivity(addContactScreenIntent);
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}