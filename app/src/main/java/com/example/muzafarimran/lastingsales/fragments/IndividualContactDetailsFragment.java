package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion1;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion2;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/9/2017.
 */

public class IndividualContactDetailsFragment extends TabFragment {

    public static final String TAG = "IndividualConDetailFrag";
    //    TextView tvName;
    TextView tvNumber;
    TextView tvDefaultText;
    //    TextView tvEmail;
    TextView tvAddress;
    ListView listView = null;
    private Long contactIDLong;
    private Spinner leadStatusSpinner;
    private Button bSave;
    private LSContact mContact;
    private TinyBus bus;
    private LinearLayout ll;
    static DynamicColumnBuilderVersion1 dynamicColumnBuilderVersion1;
    static DynamicColumnBuilderVersion2 dynamicColumnBuilderVersion2;

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
    private Context mContext;


    public static IndividualContactDetailsFragment newInstance(int page, String title, Long id) {
        IndividualContactDetailsFragment fragmentFirst = new IndividualContactDetailsFragment();
        dynamicColumnBuilderVersion1 = new DynamicColumnBuilderVersion1();
        dynamicColumnBuilderVersion2 = new DynamicColumnBuilderVersion2();
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
        contactIDLong = bundle.getLong("someId");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_profile_details_fragment, container, false);
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

            if (mContact.getVersion() != 0 && mContact.getVersion() == 2) {
                // save according to version2 parsing
                List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
                for (LSDynamicColumns dynamicColumns : allColumns) {
                    if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                        DynamicColumnBuilderVersion2.Column column = dynamicColumnBuilderVersion2.getById(Integer.parseInt(dynamicColumns.getServerId()));
                        EditText et = (EditText) ll.findViewWithTag(dynamicColumns.getServerId());
                        String currentValue = et.getText().toString();
                        // if not exists already
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilderVersion2.Column column2 = new DynamicColumnBuilderVersion2.Column();
                            column2.id = dynamicColumns.getServerId();
                            column2.name = dynamicColumns.getName();
                            column2.column_type = dynamicColumns.getColumnType();
                            column2.value = currentValue;
                            dynamicColumnBuilderVersion2.addColumn(column2);

                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilderVersion2.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        }
                    } else if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                        DynamicColumnBuilderVersion2.Column column = dynamicColumnBuilderVersion2.getById(Integer.parseInt(dynamicColumns.getServerId()));
                        EditText et = (EditText) ll.findViewWithTag(dynamicColumns.getServerId());
                        String currentValue = et.getText().toString();
                        //if current value is default no need to add a columns
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilderVersion2.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilderVersion2.Column column2 = new DynamicColumnBuilderVersion2.Column();
//                                column2.id = dynamicColumns.getServerId();
                            column2.name = dynamicColumns.getName();
//                                column2.column_type = dynamicColumns.getColumnType();
                            column2.value = currentValue;
                            dynamicColumnBuilderVersion2.addColumn(column2);

                        }
                    } else if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                        DynamicColumnBuilderVersion2.Column column = dynamicColumnBuilderVersion2.getById(Integer.parseInt(dynamicColumns.getServerId())); //TODO Exception java.lang.NullPointerException: SGH-T889 18 sep
                        Spinner s = (Spinner) ll.findViewById(Integer.parseInt((dynamicColumns.getServerId())));

                        String currentValue = s.getSelectedItem().toString();
                        //if current value is default no need to add a columns
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilderVersion2.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilderVersion2.Column column2 = new DynamicColumnBuilderVersion2.Column();
//                                column2.id = dynamicColumns.getServerId();
                            column2.name = dynamicColumns.getName();
//                                column2.column_type = dynamicColumns.getColumnType();
                            column2.value = currentValue;
                            dynamicColumnBuilderVersion2.addColumn(column2);

                        }
                    }
                }
                mContact.setDynamic(dynamicColumnBuilderVersion2.buildJSONversion2()); // Parsing LIKE IN VERSION 2 now
                if (mContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || mContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                }
                mContact.save();
                Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "JSON Prepared to save for version2 onClick: mContact.getDynamic(): " + mContact.getDynamic());
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
                dataSenderAsync.run();
                Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().finish();
                }
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
                mixpanel.track("Dynamic Column Updated");


            } else {
                // save according to version1 parsing
                List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
                for (LSDynamicColumns dynamicColumns : allColumns) {
                    if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                        DynamicColumnBuilderVersion1.Column column = dynamicColumnBuilderVersion1.getById(Integer.parseInt(dynamicColumns.getServerId()));
                        EditText et = (EditText) ll.findViewWithTag(dynamicColumns.getServerId());
                        String currentValue = et.getText().toString();
                        // if not exists already
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilderVersion1.Column column1 = new DynamicColumnBuilderVersion1.Column();
                            column1.id = dynamicColumns.getServerId();
                            column1.name = dynamicColumns.getName();
                            column1.column_type = dynamicColumns.getColumnType();
                            column1.value = currentValue;
                            dynamicColumnBuilderVersion1.addColumn(column1);

                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilderVersion1.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        }
                    } else if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                        DynamicColumnBuilderVersion1.Column column = dynamicColumnBuilderVersion1.getById(Integer.parseInt(dynamicColumns.getServerId()));
                        EditText et = (EditText) ll.findViewWithTag(dynamicColumns.getServerId());
                        String currentValue = et.getText().toString();
                        //if current value is default no need to add a columns
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilderVersion1.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilderVersion1.Column column1 = new DynamicColumnBuilderVersion1.Column();
                            column1.id = dynamicColumns.getServerId();
                            column1.name = dynamicColumns.getName();
                            column1.column_type = dynamicColumns.getColumnType();
                            column1.value = currentValue;
                            dynamicColumnBuilderVersion1.addColumn(column1);

                        }
                    } else if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                        DynamicColumnBuilderVersion1.Column column = dynamicColumnBuilderVersion1.getById(Integer.parseInt(dynamicColumns.getServerId())); //TODO Exception java.lang.NullPointerException: SGH-T889 18 sep
                        Spinner s = (Spinner) ll.findViewById(Integer.parseInt((dynamicColumns.getServerId())));

                        String currentValue = s.getSelectedItem().toString();
                        //if current value is default no need to add a columns
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilderVersion1.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilderVersion1.Column column1 = new DynamicColumnBuilderVersion1.Column();
                            column1.id = dynamicColumns.getServerId();
                            column1.name = dynamicColumns.getName();
                            column1.column_type = dynamicColumns.getColumnType();
                            column1.value = currentValue;
                            dynamicColumnBuilderVersion1.addColumn(column1);

                        }
                    }
                }
                mContact.setDynamic(dynamicColumnBuilderVersion1.buildJSON());
                if (mContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || mContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                }
                mContact.save();
                Toast.makeText(mContext, "Updated Successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "JSON Prepared to save for version1 onClick: mContact.getDynamic(): " + mContact.getDynamic());
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext);
                dataSenderAsync.run();
                Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().finish();
                }
                String projectToken = MixpanelConfig.projectToken;
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(mContext, projectToken);
                mixpanel.track("Dynamic Column Updated");
            }
        });
        addItemsOnSpinnerLeadStatus(view);
        dynamicColumns(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
        loadSocialProfileData();
        loadConnectionsData();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        bus = TinyBus.from(mContext.getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
//        if (mContact != null && mContact.getContactName() != null) {
//            tvName.setText(mContact.getContactName());
//        }
        if (mContact != null && mContact.getPhoneOne() != null) {
            tvNumber.setText(mContact.getPhoneOne());
        }
//        if (mContact != null && mContact.getContactEmail() != null) {
//            tvEmail.setText(mContact.getContactEmail());
//        }
        if (mContact != null && mContact.getContactAddress() != null) {
            tvAddress.setText(mContact.getContactAddress());
        }
        if (mContact != null && mContact.getContactType() != null) {
            if (mContact.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
                if (mContact.getContactSalesStatus() != null && !mContact.getContactSalesStatus().equals("")) {
                    switch (mContact.getContactSalesStatus()) {
                        case LSContact.SALES_STATUS_INPROGRESS:
                            leadStatusSpinner.setSelection(0, false);
                            break;
                        case LSContact.SALES_STATUS_CLOSED_WON:
                            leadStatusSpinner.setSelection(1, false);
                            break;
                        case LSContact.SALES_STATUS_CLOSED_LOST:
                            leadStatusSpinner.setSelection(2, false);
                            break;
                    }
                }
            }
        }
        leadStatusSpinner.post(new Runnable() {
            public void run() {
                leadStatusSpinner.setOnItemSelectedListener(new CustomSpinnerLeadStatusOnItemSelectedListener());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        bus.unregister(this);
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


    private void dynamicColumns(View view) {
        ll = (LinearLayout) view.findViewById(R.id.contactDetailsDropDownLayoutinner);
        Display display = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth() / 2;
        Log.d(TAG, "Display SIZE: " + display);
        List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
        Log.d(TAG, "onCreateView: Size: " + allColumns.size());
        if (allColumns == null || allColumns.size() == 0) {
            tvDefaultText.setVisibility(View.VISIBLE);
            bSave.setVisibility(View.GONE);
        }
        for (int i = 0; i < allColumns.size(); i++) {
            LinearLayout l = new LinearLayout(getContext());
            l.setFocusable(true);
            l.setFocusableInTouchMode(true);
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setWeightSum(10);

            LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsRow.setMargins(0, 5, 0, 5);
            l.setLayoutParams(layoutParamsRow);

            if (allColumns.get(i).getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {

                Log.d(TAG, "dynamicColumns: matched text");
                TextView tv = new TextView(getContext());
                tv.setText(allColumns.get(i).getName());
                tv.setPadding(15, 15, 15, 15);
                final EditText et = new EditText(getContext());
//                et.setTextColor(getResources().getColor(R.color.black));
                et.setBackgroundResource(R.drawable.dynamic_border);
                et.setPadding(15, 15, 15, 15);
                et.setText(allColumns.get(i).getDefaultValueOption());
                et.setMinimumWidth(400);
                String id = allColumns.get(i).getServerId();
                et.setTag(id);
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        bSave.setVisibility(View.VISIBLE);
                    }
                });
                LinearLayout l1 = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.weight = 5;
                l1.setLayoutParams(lp1);
                l1.addView(tv);

                LinearLayout l2 = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.weight = 5;
                l2.setLayoutParams(lp2);
                l2.addView(et);

                l.addView(l1);
                l.addView(l2);
                ll.addView(l);

            } else if (allColumns.get(i).getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                Log.d(TAG, "dynamicColumns: matched number");
                TextView tv = new TextView(getContext());
                tv.setText(allColumns.get(i).getName());
                tv.setPadding(15, 15, 15, 15);
                final EditText et = new EditText(getContext());
//                et.setTextColor(getResources().getColor(R.color.black));
                et.setBackgroundResource(R.drawable.dynamic_border);
                et.setPadding(15, 15, 15, 15);
//                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.setText(allColumns.get(i).getDefaultValueOption());
                et.setMinimumWidth(400);
                et.setTag(allColumns.get(i).getServerId());
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        bSave.setVisibility(View.VISIBLE);
                    }
                });
                LinearLayout l1 = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.weight = 5;
                l1.setLayoutParams(lp1);
                l1.addView(tv);

                LinearLayout l2 = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.weight = 5;
                l2.setLayoutParams(lp2);
                l2.addView(et);

                l.addView(l1);
                l.addView(l2);
                ll.addView(l);
            } else if (allColumns.get(i).getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                Log.d(TAG, "dynamicColumns: matched single");
                TextView tv = new TextView(getContext());
                tv.setText(allColumns.get(i).getName());
                tv.setPadding(15, 15, 15, 15);
                Spinner s = new Spinner(getContext());
                s.setId(Integer.parseInt(allColumns.get(i).getServerId()));
                List<String> list = new ArrayList<String>();
                String spinnerDefaultVal = allColumns.get(i).getDefaultValueOption();
                Log.d(TAG, "dynamicColumns: SpinnerDefaultValues: " + spinnerDefaultVal);

                try {
                    JSONArray jsonarray = new JSONArray(spinnerDefaultVal);
                    list.add("Select Below");
                    for (int j = 0; j < jsonarray.length(); j++) {
                        String jsonobject = jsonarray.getString(j);
                        list.add(jsonobject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                s.setTag(list);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(dataAdapter);

                LinearLayout.LayoutParams lpSpinner = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpSpinner.setMargins(0, 20, 0, 0);

                RelativeLayout relativeLayout = new RelativeLayout(getContext());
                relativeLayout.setBackgroundResource(R.drawable.dynamic_border);
                relativeLayout.addView(s, lpSpinner);

                LinearLayout l1 = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp1.weight = 5;
                l1.setLayoutParams(lp1);
                l1.addView(tv);

                LinearLayout l2 = new LinearLayout(getContext());
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp2.weight = 5;
                l2.setLayoutParams(lp2);
                l2.addView(relativeLayout);

                l.addView(l1);
                l.addView(l2);

                ll.addView(l);
            }
        }
        Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//////////////////////////////////////////////////////////////
// Populating LEAD data
//////////////////////////////////////////////////////////////

        Log.d(TAG, "Populating Lead Data");

        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
        Log.d(TAG, "contactID: " + mContact.getId());
        Log.d(TAG, "contactName: " + mContact.getContactName());
        Log.d(TAG, "contactServerID: " + mContact.getServerId());
        Log.d(TAG, "dynamicColumnOfLead: " + mContact.getDynamic());
        Log.d(TAG, "contactVersion: " + mContact.getVersion());

        try {
            if (mContact.getDynamic() != null) {
                if (mContact.getVersion() != 0 && mContact.getVersion() == 2) {
                    Log.d(TAG, "dynamicColumns: getVersion = 2");
                    dynamicColumnBuilderVersion2.parseJson(mContact.getDynamic());
                    ArrayList<DynamicColumnBuilderVersion2.Column> dynColumns = dynamicColumnBuilderVersion2.getColumns();
                    for (DynamicColumnBuilderVersion2.Column oneDynamicColumns : dynColumns) {
                        if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {

                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            }else {
                                Log.d(TAG, "this text dynamic column was set filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {

                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            }else {
                                Log.d(TAG, "this number dynamic column was filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {

                            final Spinner s = (Spinner) ll.findViewById(Integer.parseInt(oneDynamicColumns.id));
                            if (s != null) {
                                List<String> list = (List<String>) s.getTag();
                                Log.d(TAG, "dynamicColumns: List: " + list);
                                int index = -1;
                                for (int i = 0; i < list.size(); i++) {
                                    if (oneDynamicColumns.value.equals(list.get(i))) {
                                        index = i;
                                    }
                                }
                                if (mContact.getDynamic() != null && !mContact.getDynamic().equals("")) {

                                    Log.d(TAG, "dynamicColumns: " + index);
                                    s.setSelection(index, false);
                                }
                                s.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        s.setOnItemSelectedListener(new DynamicSpinnerOnItemSelectedListener());
                                    }
                                });
                            }else {
                                Log.d(TAG, "this spinner dynamic column was filled for lead but column is no more");
                            }

                        }
                    }


                } else {
                    Log.d(TAG, "dynamicColumns: getVersion = 0");
                    dynamicColumnBuilderVersion1.parseJson(mContact.getDynamic());
//                    Log.d(TAG, "dynamicColumnsJSONN: " + mContact.getDynamic());
                    ArrayList<DynamicColumnBuilderVersion1.Column> dynColumns = dynamicColumnBuilderVersion1.getColumns();
                    for (DynamicColumnBuilderVersion1.Column oneDynamicColumns : dynColumns) {
                        if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {

                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            }else {
                                Log.d(TAG, "this text dynamic column was set filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {

                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            }else {
                                Log.d(TAG, "this number dynamic column was set filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {

                            final Spinner s = (Spinner) ll.findViewById(Integer.parseInt(oneDynamicColumns.id));
                            if (s != null) {
                                List<String> list = (List<String>) s.getTag();
                                int index = -1;
                                for (int i = 0; i < list.size(); i++) {
                                    if (oneDynamicColumns.value.equals(list.get(i))) {
                                        index = i;
                                    }
                                }
                                if (mContact.getDynamic() != null && !mContact.getDynamic().equals("")) {

                                    Log.d(TAG, "dynamicColumns: " + index);
                                    s.setSelection(index, false);
                                }
                                s.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        s.setOnItemSelectedListener(new DynamicSpinnerOnItemSelectedListener());
                                    }
                                });
                            }else {
                                Log.d(TAG, "this spinner dynamic column was set filled for lead but column is no more");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////
    }

    public void addItemsOnSpinnerLeadStatus(View view) {
        leadStatusSpinner = (Spinner) view.findViewById(R.id.lead_status_spinner);
        List<String> list = new ArrayList<String>();
        list.add("InProgress");
        list.add("Close Won");
        list.add("Closed Lost");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leadStatusSpinner.setAdapter(dataAdapter);
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

    private class CustomSpinnerLeadStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
            switch (pos) {
                case 0:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to InProgress", Toast.LENGTH_SHORT).show();
                    dataSenderAsync.run();
                    break;
                case 1:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Won", Toast.LENGTH_SHORT).show();
                    dataSenderAsync.run();
                    break;
                case 2:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Lost", Toast.LENGTH_SHORT).show();
                    dataSenderAsync.run();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class DynamicSpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            bSave.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Subscribe
    public void onLeadContactAddedEventModel(LeadContactAddedEventModel event) {
        Log.d(TAG, "onLeadContactAddedEventModel: CalledInFrag");
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
