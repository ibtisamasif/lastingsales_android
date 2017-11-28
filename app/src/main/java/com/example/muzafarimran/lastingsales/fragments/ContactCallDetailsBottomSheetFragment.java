package com.example.muzafarimran.lastingsales.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.activities.AddEditLeadActivity;
import com.example.muzafarimran.lastingsales.adapters.IndividualContactCallAdapter;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.example.muzafarimran.lastingsales.utils.TypeManager;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

public class ContactCallDetailsBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String TAG = "ContactCallDetailsBotto";
    private static final String CONTACT_ID = "contact_id";
    private static SessionManager sessionManager;
    private static RequestQueue queue;

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
    private TextView tvCallerHistoryName0;
    private TextView tvCallerHistoryLastCallDateTime0;
    private TextView tvCallerHistoryLastCallTimeAgo0;
    private TextView tvCallerHistoryName1;
    private TextView tvCallerHistoryLastCallDateTime1;
    private TextView tvCallerHistoryLastCallTimeAgo1;

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

    public static ContactCallDetailsBottomSheetFragment newInstance(Long contact_id, int page) {
        ContactCallDetailsBottomSheetFragment fragmentFirst = new ContactCallDetailsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putLong(CONTACT_ID, contact_id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.fragment_contact_call_details_bottom_sheet, null);
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
//                LeadContactAddedEventModel mCallEvent = new LeadContactAddedEventModel();
//                TinyBus bus = TinyBus.from(getActivity().getApplicationContext());
//                bus.post(mCallEvent);
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

        ListView listview = view.findViewById(R.id.calls_list);
        indadapter = new IndividualContactCallAdapter(getActivity(), allCallsOfThisContact);
        Log.d(TAG, "setUpList: Size " + allCallsOfThisContact.size());
        for (LSCall oneCall : allCallsOfThisContact) {
            Log.d(TAG, "setUpList: " + oneCall.toString());
        }
        listview.setAdapter(indadapter);

        View includeSocialConnection = view.findViewById(R.id.includeSocialConnection);
        TextView tvSocialConnection = (TextView) includeSocialConnection.findViewById(R.id.tvSeparator);
        tvSocialConnection.setText("Connections");

        tvCallerHistoryName0 = (TextView) view.findViewById(R.id.tvCallerHistoryName0);
        tvCallerHistoryLastCallDateTime0 = (TextView) view.findViewById(R.id.tvCallerHistoryLastCallDateTime0);
        tvCallerHistoryLastCallTimeAgo0 = (TextView) view.findViewById(R.id.tvCallerHistoryLastCallTimeAgo0);
        tvCallerHistoryName1 = (TextView) view.findViewById(R.id.tvCallerHistoryName1);
        tvCallerHistoryLastCallDateTime1 = (TextView) view.findViewById(R.id.tvCallerHistoryLastCallDateTime1);
        tvCallerHistoryLastCallTimeAgo1 = (TextView) view.findViewById(R.id.tvCallerHistoryLastCallTimeAgo1);


        tvCallerHistoryName0.setText("");
        tvCallerHistoryLastCallDateTime0.setText("");
        tvCallerHistoryLastCallTimeAgo0.setText("");
        tvCallerHistoryName1.setText("");
        tvCallerHistoryLastCallDateTime1.setText("");
        tvCallerHistoryLastCallTimeAgo1.setText("");

        tvCallerHistoryName0.setVisibility(View.GONE);
        tvCallerHistoryLastCallDateTime0.setVisibility(View.GONE);
        tvCallerHistoryLastCallTimeAgo0.setVisibility(View.GONE);
        tvCallerHistoryName1.setVisibility(View.GONE);
        tvCallerHistoryLastCallDateTime1.setVisibility(View.GONE);
        tvCallerHistoryLastCallTimeAgo1.setVisibility(View.GONE);

        sessionManager = new SessionManager(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        fetchCustomerHistory(number);

        NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.bottom_sheet);
        nestedScrollView.setScrollY(0);
    }

    private void fetchCustomerHistory(final String number) {
        Log.d(TAG, "fetchCustomersHistoryFunc: Fetching Data...");
        Log.d(TAG, "fetchCustomerHistory: Number: " + number);
        Log.d(TAG, "fetchCustomerHistory: Token: " + sessionManager.getLoginToken());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CUSTOMER_HISTORY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("phone", "" + number)
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        Log.d(TAG, "fetchCustomerHistory: myUrl: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String respon) {
//                tvError.setVisibility(View.GONE);
                Log.d(TAG, "onResponse() response = [" + respon + "]");
                try {
                    JSONObject jObj = new JSONObject(respon);
//                    int responseCode = jObj.getInt("responseCode");
                    JSONObject response = jObj.getJSONObject("response");
                    JSONArray dataArray = response.getJSONArray("data");
                    Log.d(TAG, "onResponse: dataArray Lenght: " + dataArray.length());
//                    for (int i = 0; i < dataArray.length(); i++) {
                    if (dataArray.length() > 0) {
                        JSONObject jsonobject0 = dataArray.getJSONObject(0);
                        String last_call0 = jsonobject0.getString("last_call");
                        String user_id0 = jsonobject0.getString("user_id");
                        String duration0 = jsonobject0.getString("duration");
                        String firstname0 = jsonobject0.getString("firstname");
                        String lastname0 = jsonobject0.getString("lastname");
                        String role_id0 = jsonobject0.getString("role_id");
                        String name0 = jsonobject0.getString("name");

                        Log.d(TAG, "onResponse0: last_call: " + last_call0);
                        Log.d(TAG, "onResponse0: user_id: " + user_id0);
                        Log.d(TAG, "onResponse0: duration: " + duration0);
                        Log.d(TAG, "onResponse0: firstname: " + firstname0);
                        Log.d(TAG, "onResponse0: lastname: " + lastname0);
                        Log.d(TAG, "onResponse0: role_id: " + role_id0);
                        Log.d(TAG, "onResponse0: name: " + name0);

//                        if (name0 != null && !name0.equals("null")) {
//                            tvName.setText(name0);
//                        }

                        if (firstname0 != null && lastname0 != null && last_call0 != null) {
                            tvCallerHistoryName0.setVisibility(View.VISIBLE);
                            if (!user_id0.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName0.setText("Last contacted " + firstname0 + " " + lastname0);
                            } else {
                                tvCallerHistoryName0.setText("Last contacted with me");
                            }
                            tvCallerHistoryLastCallDateTime0.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallTimeAgo0.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallDateTime0.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call0), "dd-MMM-yyyy"));
                            tvCallerHistoryLastCallTimeAgo0.setText("(" + PhoneNumberAndCallUtils.getDaysAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call0), getActivity()) + ")");
                        }
                    }
                    if (dataArray.length() > 1) {
                        JSONObject jsonobject1 = dataArray.getJSONObject(1);
                        String last_call1 = jsonobject1.getString("last_call");
                        String user_id1 = jsonobject1.getString("user_id");
                        String duration1 = jsonobject1.getString("duration");
                        String firstname1 = jsonobject1.getString("firstname");
                        String lastname1 = jsonobject1.getString("lastname");
                        String role_id1 = jsonobject1.getString("role_id");
                        String name1 = jsonobject1.getString("name");

                        Log.d(TAG, "onResponse1: last_call: " + last_call1);
                        Log.d(TAG, "onResponse1: user_id: " + user_id1);
                        Log.d(TAG, "onResponse1: duration: " + duration1);
                        Log.d(TAG, "onResponse1: firstname: " + firstname1);
                        Log.d(TAG, "onResponse1: lastname: " + lastname1);
                        Log.d(TAG, "onResponse1: role_id: " + role_id1);
                        Log.d(TAG, "onResponse1: name: " + name1);

                        if (firstname1 != null && lastname1 != null && last_call1 != null) {
                            tvCallerHistoryName1.setVisibility(View.VISIBLE);
                            if (!user_id1.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName1.setText("Last contacted " + firstname1 + " " + lastname1);
                            } else {
                                tvCallerHistoryName1.setText("Last contacted with me");
                            }

                            tvCallerHistoryLastCallDateTime1.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallTimeAgo1.setVisibility(View.VISIBLE);
                            tvCallerHistoryLastCallDateTime1.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call1), "dd-MMM-yyyy"));
                            tvCallerHistoryLastCallTimeAgo1.setText("(" + PhoneNumberAndCallUtils.getDaysAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call1), getActivity()) + ")");
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotGetCustomerHistory");
                if (!NetworkAccess.isNetworkAvailable(getActivity())) {
//                    tvError.setText("Internet is required to view connections");
                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 404) {
//                                tvError.setText("Connections not found");
                            } else {
//                                tvError.setText("Error loading");
                            }
                        } else {
//                            tvError.setText("Poor Internet Connectivity");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        int MY_MAX_RETRIES = 3;
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                MY_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
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