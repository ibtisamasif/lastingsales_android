package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.listeners.LSContactProfileCallback;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.sync.ContactProfileProvider;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/9/2017.
 */

public class IndividualDealDetailsFragment extends TabFragment {

    public static final String TAG = "IndividualConDetailFrag";
    //    TextView tvName;
    TextView tvNumber;
    TextView tvDefaultText;
    //    TextView tvEmail;
    TextView tvAddress;

    private CardView cv_social_item;
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
    private LinearLayout llDynamicConnectionsContainer;
    private TextView tvError;
    private SessionManager sessionManager;
    private RequestQueue queue;

    ListView listView = null;
    private Spinner dealStatusSpinner;
    private Button bSave;
    private Long dealIDLong;
    private Context mContext;
    private LSDeal mDeal;
    private LSContact mContact;


    public static IndividualDealDetailsFragment newInstance(int page, String title, Long id) {
        IndividualDealDetailsFragment fragmentFirst = new IndividualDealDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach: ");
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        dealIDLong = bundle.getLong("someId");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.deal_profile_details_fragment, container, false);
//        tvName = (TextView) view.findViewById(R.id.tvName);
        tvNumber = (TextView) view.findViewById(R.id.tvContactNumber);
//        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);

        //      social Profile views
        cv_social_item = view.findViewById(R.id.cv_social_item);
        tvNameFromProfile = view.findViewById(R.id.tvNameFromProfile);
        tvNameFromProfileTitle = view.findViewById(R.id.tvNameFromProfileTitle);
        tvCityFromProfile = view.findViewById(R.id.tvCityFromProfile);
        tvCityFromProfileTitle = view.findViewById(R.id.tvCityFromProfileTitle);
        tvCountryFromProfile = view.findViewById(R.id.tvCountryFromProfile);
        tvCountryFromProfileTitle = view.findViewById(R.id.tvCountryFromProfileTitle);
        tvWorkFromProfile = view.findViewById(R.id.tvWorkFromProfile);
        tvWorkFromProfileTitle = view.findViewById(R.id.tvWorkFromProfileTitle);
        tvCompanyFromProfile = view.findViewById(R.id.tvCompanyFromProfile);
        tvCompanyFromProfileTitle = view.findViewById(R.id.tvCompanyFromProfileTitle);
        tvWhatsappFromProfile = view.findViewById(R.id.tvWhatsappFromProfile);
        tvWhatsappFromProfileTitle = view.findViewById(R.id.tvWhatsappFromProfileTitle);
        tvTweeterFromProfile = view.findViewById(R.id.tvTweeterFromProfile);
        tvTweeterFromProfileTitle = view.findViewById(R.id.tvTweeterFromProfileTitle);
        tvLinkdnFromProfile = view.findViewById(R.id.tvLinkdnFromProfile);
        tvLinkdnFromProfileTitle = view.findViewById(R.id.tvLinkdnFromProfileTitle);
        tvFbFromProfile = view.findViewById(R.id.tvFbFromProfile);
        tvFbFromProfileTitle = view.findViewById(R.id.tvFbFromProfileTitle);

        llDynamicConnectionsContainer = (LinearLayout) view.findViewById(R.id.llDynamicConnectionsContainer);

        tvDefaultText = (TextView) view.findViewById(R.id.tvDefaultText);
        bSave = (Button) view.findViewById(R.id.contactDetailsSaveButton);
        tvDefaultText.setVisibility(View.GONE);
//        bSave.setVisibility(View.GONE);
        bSave.setOnClickListener(v -> {
            Toast.makeText(mContext, "Saving..", Toast.LENGTH_SHORT).show();
        });
        addItemsOnSpinnerLeadStatus(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        Bundle bundle = this.getArguments();
        dealIDLong = bundle.getLong("someId");
        mDeal = LSDeal.findById(LSDeal.class, dealIDLong);
        if (mDeal != null) {
            mContact = mDeal.getContact();
        }
//        if (mDeal != null && mDeal.getContactName() != null) {
//            tvName.setText(mDeal.getContactName());
//        }
        if (mContact != null && mContact.getPhoneOne() != null) {
            tvNumber.setText(mContact.getPhoneOne());
        }
//        if (mDeal != null && mDeal.getContactEmail() != null) {
//            tvEmail.setText(mDeal.getContactEmail());
//        }
        if (mContact != null && mContact.getContactAddress() != null) {
            tvAddress.setText(mContact.getContactAddress());
        }
//        if (mDeal != null && mDeal.getContactType() != null) {
//            if (mDeal.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
//                if (mDeal.getContactSalesStatus() != null && !mDeal.getContactSalesStatus().equals("")) {
//                    switch (mDeal.getContactSalesStatus()) {
//                        case LSContact.SALES_STATUS_INPROGRESS:
//                            dealStatusSpinner.setSelection(0, false);
//                            break;
//                        case LSContact.SALES_STATUS_CLOSED_WON:
//                            dealStatusSpinner.setSelection(1, false);
//                            break;
//                        case LSContact.SALES_STATUS_CLOSED_LOST:
//                            dealStatusSpinner.setSelection(2, false);
//                            break;
//                    }
//                }
//            }
//        }
        dealStatusSpinner.post(new Runnable() {
            public void run() {
                dealStatusSpinner.setOnItemSelectedListener(new CustomSpinnerDealStatusOnItemSelectedListener());
            }
        });

        if (mContact != null) {
            loadSocialProfileData();
            loadConnectionsData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        TinyBus.from(mContext.getApplicationContext()).post(new LeadContactAddedEventModel());
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ");
        listView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach: ");
    }

    public void addItemsOnSpinnerLeadStatus(View view) {
        dealStatusSpinner = (Spinner) view.findViewById(R.id.lead_status_spinner);
        List<String> list = new ArrayList<String>();
        list.add("InProgress");
        list.add("Close Won");
        list.add("Closed Lost");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dealStatusSpinner.setAdapter(dataAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CustomSpinnerDealStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
            switch (pos) {
                case 0:
//                    mDeal.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
//                    mDeal.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Pending", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
                    break;
                case 1:
//                    mDeal.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
//                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mDeal.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Won", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
                    ;
                    break;
                case 2:
//                    mDeal.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
//                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
//                    mDeal.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Lost", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void loadSocialProfileData() {
        LSContactProfile lsContactProfile = mContact.getContactProfile();

        tvTweeterFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvLinkdnFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
        tvFbFromProfile.setMovementMethod(LinkMovementMethod.getInstance());

        if (lsContactProfile != null) {
//            if (lsContactProfile.getSocial_image() != null) {
//                MyDateTimeStamp.setFrescoImage(user_avatar_ind, lsContactProfile.getSocial_image());
//            }
            if (lsContactProfile.getFirstName() != null && !lsContactProfile.getFirstName().equals("")) {
                tvNameFromProfile.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
            } else {
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
        } else {
            cv_social_item.setVisibility(View.GONE);
            Log.d(TAG, "loadSocialProfileData: lsContactProfile == NULL " + mContact.getPhoneOne());
            ContactProfileProvider contactProfileProvider = new ContactProfileProvider(getActivity());
            contactProfileProvider.getContactProfile(mContact.getPhoneOne(), new LSContactProfileCallback() {
                @Override
                public void onSuccess(LSContactProfile result) {
                    Log.d(TAG, "onSuccess: lsContactProfile: " + result);
                    if (result != null) {
                        cv_social_item.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onSuccess: Updating Data");
                        //            if (lsContactProfile.getSocial_image() != null) {
//                MyDateTimeStamp.setFrescoImage(user_avatar_ind, lsContactProfile.getSocial_image());
//            }
                        if (result.getFirstName() != null && !result.getFirstName().equals("")) {
                            tvNameFromProfile.setText(result.getFirstName() + " " + result.getLastName());
                        } else {
                            tvNameFromProfile.setVisibility(View.GONE);
                            tvNameFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getCity() != null && !result.getCity().equals("")) {
                            tvCityFromProfile.setText(result.getCity());
                        } else {
                            tvCityFromProfile.setVisibility(View.GONE);
                            tvCityFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getCountry() != null && !result.getCountry().equals("")) {
                            tvCountryFromProfile.setText(result.getCountry());
                        } else {
                            tvCountryFromProfile.setVisibility(View.GONE);
                            tvCountryFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getWork() != null && !result.getWork().equals("")) {
                            tvWorkFromProfile.setText(result.getWork());
                        } else {
                            tvWorkFromProfile.setVisibility(View.GONE);
                            tvWorkFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getCompany() != null && !result.getCompany().equals("")) {
                            tvCompanyFromProfile.setText(result.getCompany());
                        } else {
                            tvCompanyFromProfile.setVisibility(View.GONE);
                            tvCompanyFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getWhatsapp() != null && !result.getWhatsapp().equals("")) {
                            tvWhatsappFromProfile.setText(result.getWhatsapp());
                        } else {
                            tvWhatsappFromProfile.setVisibility(View.GONE);
                            tvWhatsappFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getTweet() != null && !result.getTweet().equals("")) {
                            tvTweeterFromProfile.setText(result.getTweet());
                        } else {
                            tvTweeterFromProfile.setVisibility(View.GONE);
                            tvTweeterFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getLinkd() != null && !result.getLinkd().equals("")) {
                            tvLinkdnFromProfile.setText(result.getLinkd());
                        } else {
                            tvLinkdnFromProfile.setVisibility(View.GONE);
                            tvLinkdnFromProfileTitle.setVisibility(View.GONE);
                        }
                        if (result.getFb() != null && !result.getFb().equals("")) {
                            tvFbFromProfile.setText(result.getFb());
                        } else {
                            tvFbFromProfile.setVisibility(View.GONE);
                            tvFbFromProfileTitle.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void loadConnectionsData() {

        tvError = new TextView(mContext);
        tvError.setText("Loading...");
        tvError.setGravity(Gravity.CENTER);
        llDynamicConnectionsContainer.addView(tvError);
        tvError.setVisibility(View.VISIBLE);

        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
        fetchCustomerHistory(mContact.getPhoneOne());

    }

    private void fetchCustomerHistory(final String number) {
//        Log.d(TAG, "fetchCustomersHistoryFunc: Fetching Data...");
//        Log.d(TAG, "fetchCustomerHistory: Number: " + number);
//        Log.d(TAG, "fetchCustomerHistory: Token: " + sessionManager.getLoginToken());
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.GET_CUSTOMER_HISTORY;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
//                .appendQueryParameter("phone", "+92 301 4775234")
                .appendQueryParameter("phone", "" + number)
//                .appendQueryParameter("api_token", "NVAPN67dqZU4bBW18ykrtylvfyXFogt1dh3Dgw2XsOFuQKaWLySRv058v1If")
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
//        Log.d(TAG, "fetchCustomerHistory: myUrl: " + myUrl);
        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String respon) {
                tvError.setVisibility(View.GONE);
//                Log.d(TAG, "onResponse() response = [" + respon + "]");
                try {
                    JSONObject jObj = new JSONObject(respon);
//                    int responseCode = jObj.getInt("responseCode");
                    JSONObject response = jObj.getJSONObject("response");
                    JSONArray dataArray = response.getJSONArray("data");
//                    Log.d(TAG, "onResponse: dataArray Length: " + dataArray.length());
                    if (dataArray.length() > 0) {
                        for (int i = 0; i < dataArray.length(); i++) {
//                            Log.d(TAG, "onResponse: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                            JSONObject jsonobject = dataArray.getJSONObject(i);
                            String last_call = jsonobject.getString("last_call");
                            String user_id = jsonobject.getString("user_id");
                            String duration = jsonobject.getString("duration");
                            String firstname = jsonobject.getString("firstname");
                            String lastname = jsonobject.getString("lastname");
                            String role_id = jsonobject.getString("role_id");
                            String name = jsonobject.getString("name");

//                            Log.d(TAG, "onResponse: last_call: " + last_call);
//                            Log.d(TAG, "onResponse: user_id: " + user_id);
//                            Log.d(TAG, "onResponse: duration: " + duration);
//                            Log.d(TAG, "onResponse: firstname: " + firstname);
//                            Log.d(TAG, "onResponse: lastname: " + lastname);
//                            Log.d(TAG, "onResponse: role_id: " + role_id);
//                            Log.d(TAG, "onResponse: name: " + name);

//                            Display display = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//                            int width = display.getWidth();

                            LinearLayout llParentHorizontal = new LinearLayout(mContext.getApplicationContext()); // Huawei mate
                            llParentHorizontal.setFocusable(true);
                            llParentHorizontal.setFocusableInTouchMode(true);
                            llParentHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                            llParentHorizontal.setWeightSum(10);

                            LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParamsRow.setMargins(8, 8, 8, 8);
                            llParentHorizontal.setLayoutParams(layoutParamsRow);

                            TextView tvCallerHistoryName = new TextView(mContext);
                            tvCallerHistoryName.setPadding(0, 0, 0, 0);
                            tvCallerHistoryName.setMaxLines(3);
                            tvCallerHistoryName.setGravity(Gravity.LEFT);
                            tvCallerHistoryName.setTextSize(14);
                            tvCallerHistoryName.setTypeface(tvCallerHistoryName.getTypeface(), Typeface.BOLD);
                            if (!user_id.equals(sessionManager.getKeyLoginId())) {
                                tvCallerHistoryName.setText("Last contacted " + firstname + " " + lastname);
                            } else {
                                tvCallerHistoryName.setText("Last contacted with me");
                            }

                            TextView tvCallerHistoryLastCallTimeAgo = new TextView(mContext);
                            tvCallerHistoryLastCallTimeAgo.setText("(" + PhoneNumberAndCallUtils.getDaysAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call), mContext) + ")");
                            tvCallerHistoryLastCallTimeAgo.setPadding(0, 0, 0, 0);
                            tvCallerHistoryLastCallTimeAgo.setGravity(Gravity.LEFT);
                            tvCallerHistoryLastCallTimeAgo.setTextSize(10);

                            TextView tvCallerHistoryLastCallDateTime = new TextView(mContext);
                            tvCallerHistoryLastCallDateTime.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call), "dd-MMM-yyyy"));
                            tvCallerHistoryLastCallDateTime.setPadding(0, 0, 0, 0);
                            tvCallerHistoryLastCallDateTime.setGravity(Gravity.RIGHT);
                            tvCallerHistoryLastCallDateTime.setTextSize(14);

                            LinearLayout l1ChildLeft = new LinearLayout(mContext);
                            l1ChildLeft.setOrientation(LinearLayout.VERTICAL);
                            LinearLayout.LayoutParams lpChildLeft = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lpChildLeft.weight = 7;
                            l1ChildLeft.setLayoutParams(lpChildLeft);
                            l1ChildLeft.addView(tvCallerHistoryName);
                            l1ChildLeft.addView(tvCallerHistoryLastCallTimeAgo);

                            LinearLayout llChildRight = new LinearLayout(mContext);
                            llChildRight.setOrientation(LinearLayout.HORIZONTAL);
                            llChildRight.setGravity(Gravity.RIGHT);
                            LinearLayout.LayoutParams lpChildRight = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lpChildRight.weight = 3;
                            lpChildRight.gravity = Gravity.RIGHT;
                            llChildRight.setLayoutParams(lpChildRight);
                            llChildRight.addView(tvCallerHistoryLastCallDateTime);

                            llParentHorizontal.addView(l1ChildLeft);
                            llParentHorizontal.addView(llChildRight);

                            llDynamicConnectionsContainer.addView(llParentHorizontal);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: CouldNotGetCustomerHistory");
                if (mContext != null) {
                    if (!NetworkAccess.isNetworkAvailable(mContext)) {
                        tvError.setText("Internet is required to view connections");
                    } else {
                        try {
                            if (error.networkResponse != null) {
                                if (error.networkResponse.statusCode == 404) {
                                    tvError.setText("Connections not found");
                                } else {
                                    tvError.setText("Error Loading");
                                }
                            } else {
                                tvError.setText("Poor Internet Connectivity");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    tvError.setText("Error Loading");
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

}
