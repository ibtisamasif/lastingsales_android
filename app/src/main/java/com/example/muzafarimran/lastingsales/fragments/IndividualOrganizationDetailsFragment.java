package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.adapters.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.app.MyURLs;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.listloaders.DealsOfAOrganizationLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSOrganization;
import com.example.muzafarimran.lastingsales.providers.models.LSProperty;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion1;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion2;
import com.example.muzafarimran.lastingsales.utils.DynamicColums;
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

public class IndividualOrganizationDetailsFragment extends TabFragment implements LoaderManager.LoaderCallbacks<List<Object>>, View.OnClickListener {

    public static final String TAG = "IndividualOrgDetailFra";
    public static final String DEALS_ORGANIZATION_ID = "deals_organization_id";
    public static final int DEALS_OF_A_ORGANIZATION = 32;
    static DynamicColumnBuilderVersion1 dynamicColumnBuilderVersion1;
    static DynamicColumnBuilderVersion2 dynamicColumnBuilderVersion2;
    private static Bundle args;
    TextView tvName;
    TextView tvEmail;
    TextView tvNumber;
    TextView tvAddress;
    TextView tvDefaultText;
    RecyclerView mRecyclerView = null;
    Button save;
    GridLayout gridLayout;
    DynamicColums dynamicColums;
    private List<Object> listLoader = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;
    private Long organizationIDLong;
    private Spinner leadStatusSpinner;
    private Button bSave;
    private LSOrganization mOrganization;
    private LinearLayout ll;
    private TextView tvError;
    private SessionManager sessionManager;
    private RequestQueue queue;
    private Context mContext;

    public static IndividualOrganizationDetailsFragment newInstance(int page, String title, Long id) {
        IndividualOrganizationDetailsFragment fragmentFirst = new IndividualOrganizationDetailsFragment();
        dynamicColumnBuilderVersion1 = new DynamicColumnBuilderVersion1();
        dynamicColumnBuilderVersion2 = new DynamicColumnBuilderVersion2();
        args = new Bundle();
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
        organizationIDLong = args.getLong("someId");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organization_profile_details_fragment, container, false);
        tvName = view.findViewById(R.id.tvName);
        tvNumber = view.findViewById(R.id.tvNumber);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvAddress = view.findViewById(R.id.tvAddress);

        adapter = new MyRecyclerViewAdapter(getActivity(), listLoader); //TODO potential bug getActivity can be null.
        RecyclerView mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        save = view.findViewById(R.id.orgsave);

        save.setOnClickListener(this);

        tvDefaultText = view.findViewById(R.id.tvDefaultText);


        tvDefaultText.setVisibility(View.GONE);
//        bSave.setVisibility(View.GONE);


        gridLayout = view.findViewById(R.id.gridorg);


        dynamicColumnByAmir();


        addItemsOnSpinnerLeadStatus(view);
        dynamicColumns(view);
        getLoaderManager().initLoader(DEALS_OF_A_ORGANIZATION, args, IndividualOrganizationDetailsFragment.this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated: ");
//        loadSocialProfileData();
        loadConnectionsData();
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
        organizationIDLong = args.getLong("someId");
        mOrganization = LSOrganization.findById(LSOrganization.class, organizationIDLong);
        if (mOrganization != null && mOrganization.getName() != null) {
            tvName.setText(mOrganization.getName());
        }
        if (mOrganization != null && mOrganization.getPhone() != null) {
            tvNumber.setText(mOrganization.getPhone());
        }
        if (mOrganization != null && mOrganization.getEmail() != null) {
            tvEmail.setText(mOrganization.getEmail());
        }
        if (mOrganization != null && mOrganization.getAddress() != null) {
            tvAddress.setText(mOrganization.getAddress());
        }
//        if (mOrganization != null && mOrganization.getContactType() != null) {
//            if (mOrganization.getContactType().equals(LSContact.CONTACT_TYPE_SALES)) {
//                if (mOrganization.getContactSalesStatus() != null && !mOrganization.getContactSalesStatus().equals("")) {
//                    switch (mOrganization.getContactSalesStatus()) {
//                        case LSContact.SALES_STATUS_INPROGRESS:
//                            leadStatusSpinner.setSelection(0, false);
//                            break;
//                        case LSContact.SALES_STATUS_CLOSED_WON:
//                            leadStatusSpinner.setSelection(1, false);
//                            break;
//                        case LSContact.SALES_STATUS_CLOSED_LOST:
//                            leadStatusSpinner.setSelection(2, false);
//                            break;
//                    }
//                }
//            }
//        }
//        leadStatusSpinner.post(new Runnable() {
//            public void run() {
//                leadStatusSpinner.setOnItemSelectedListener(new CustomSpinnerLeadStatusOnItemSelectedListener());
//            }
//        });
        getLoaderManager().restartLoader(DEALS_OF_A_ORGANIZATION, args, IndividualOrganizationDetailsFragment.this);
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
     /*   ll = (LinearLayout) view.findViewById(R.id.contactDetailsDropDownLayoutinner);
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
        }*/
        Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//////////////////////////////////////////////////////////////
// Populating LEAD data
//////////////////////////////////////////////////////////////

        Log.d(TAG, "Populating Lead Data");

        organizationIDLong = args.getLong("someId");
        mOrganization = LSOrganization.findById(LSOrganization.class, organizationIDLong);
        Log.d(TAG, "contactID: " + mOrganization.getId());
        Log.d(TAG, "contactName: " + mOrganization.getName());
        Log.d(TAG, "contactServerID: " + mOrganization.getServerId());
        Log.d(TAG, "dynamicColumnOfLead: " + mOrganization.getDynamicValues());
        Log.d(TAG, "contactVersion: " + mOrganization.getVersion());

//        try {
//            if (mOrganization.getDynamicValues() != null) {
//                if (mOrganization.getVersion() != 0 && mOrganization.getVersion() == 2) {
//                    Log.d(TAG, "dynamicColumns: getVersion = 2");
//                    dynamicColumnBuilderVersion2.parseJson(mOrganization.getDynamicValues());
//                    ArrayList<DynamicColumnBuilderVersion2.Column> dynColumns = dynamicColumnBuilderVersion2.getColumns();
//                    for (DynamicColumnBuilderVersion2.Column oneDynamicColumns : dynColumns) {
//                        if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
//
//                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
//                            if (et != null) {
//                                et.setText(oneDynamicColumns.value);
//                            } else {
//                                Log.d(TAG, "this text dynamic column was set filled for lead but column is no more");
//                            }
//
//                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
//
//                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
//                            if (et != null) {
//                                et.setText(oneDynamicColumns.value);
//                            } else {
//                                Log.d(TAG, "this number dynamic column was filled for lead but column is no more");
//                            }
//
//                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
//
//                            final Spinner s = (Spinner) ll.findViewById(Integer.parseInt(oneDynamicColumns.id));
//                            if (s != null) {
//                                List<String> list = (List<String>) s.getTag();
//                                Log.d(TAG, "dynamicColumns: List: " + list);
//                                int index = -1;
//                                for (int i = 0; i < list.size(); i++) {
//                                    if (oneDynamicColumns.value.equals(list.get(i))) {
//                                        index = i;
//                                    }
//                                }
//                                if (mOrganization.getDynamicValues() != null && !mOrganization.getDynamicValues().equals("")) {
//
//                                    Log.d(TAG, "dynamicColumns: " + index);
//                                    s.setSelection(index, false);
//                                }
//                                s.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        s.setOnItemSelectedListener(new DynamicSpinnerOnItemSelectedListener());
//                                    }
//                                });
//                            } else {
//                                Log.d(TAG, "this spinner dynamic column was filled for lead but column is no more");
//                            }
//
//                        }
//                    }
//
//
//                } else {
//                    Log.d(TAG, "dynamicColumns: getVersion = 0");
//                    dynamicColumnBuilderVersion1.parseJson(mOrganization.getDynamicValues());
////                    Log.d(TAG, "dynamicColumnsJSONN: " + mOrganization.getDynamicValues());
//                    ArrayList<DynamicColumnBuilderVersion1.Column> dynColumns = dynamicColumnBuilderVersion1.getColumns();
//                    for (DynamicColumnBuilderVersion1.Column oneDynamicColumns : dynColumns) {
//                        //find column_type from LSDynamic column using the name of column from leads dynamic column.
//                        if (oneDynamicColumns.column_type != null && oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
//
//                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
//                            if (et != null) {
//                                et.setText(oneDynamicColumns.value);
//                            } else {
//                                Log.d(TAG, "this text dynamic column was set filled for lead but column is no more");
//                            }
//
//                        } else if (oneDynamicColumns.column_type != null && oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
//
//                            EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
//                            if (et != null) {
//                                et.setText(oneDynamicColumns.value);
//                            } else {
//                                Log.d(TAG, "this number dynamic column was set filled for lead but column is no more");
//                            }
//
//                        } else if (oneDynamicColumns.column_type != null && oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
//
//                            final Spinner s = (Spinner) ll.findViewById(Integer.parseInt(oneDynamicColumns.id));
//                            if (s != null) {
//                                List<String> list = (List<String>) s.getTag();
//                                int index = -1;
//                                for (int i = 0; i < list.size(); i++) {
//                                    if (oneDynamicColumns.value.equals(list.get(i))) {
//                                        index = i;
//                                    }
//                                }
//                                if (mOrganization.getDynamicValues() != null && !mOrganization.getDynamicValues().equals("")) {
//
//                                    Log.d(TAG, "dynamicColumns: " + index);
//                                    s.setSelection(index, false);
//                                }
//                                s.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        s.setOnItemSelectedListener(new DynamicSpinnerOnItemSelectedListener());
//                                    }
//                                });
//                            } else {
//                                Log.d(TAG, "this spinner dynamic column was set filled for lead but column is no more");
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////
    }

    @Override
    public void onClick(View v) {


        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class, "related_to=?", LSProperty.STORABLE_TYPE_APP_ORGANIZATION);

        if (list.size() > 0) {

            for (int i = 0; i < list.size(); i++) {
                String type = list.get(i).getColumnType();


                if (type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {


                    EditText editText = gridLayout.findViewWithTag("org" + list.get(i).getServerId());

                    String val = editText.getText().toString();


                    Log.d("textfield", val);
                    //Toast.makeText(mContext, "TextField"+val, Toast.LENGTH_SHORT).show();


                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));


                        LSOrganization lsOrganization = LSOrganization.findById(LSOrganization.class, args.getLong("someId"));
                        lsProperty1.setOrganizationOfProperty(lsOrganization);
                        lsProperty1.save();

                        Log.d("created", "created property");
                    }


                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {


                    EditText editText = gridLayout.findViewWithTag("org" + list.get(i).getServerId());

                    String val = editText.getText().toString();

                    Log.d("numberfield", val);
                    // Toast.makeText(mContext,"Number field"+ val, Toast.LENGTH_SHORT).show();

                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        LSOrganization lsOrganization = LSOrganization.findById(LSOrganization.class, args.getLong("someId"));
                        lsProperty1.setOrganizationOfProperty(lsOrganization);
                        lsProperty1.save();

                        Log.d("created", "created property");
                    }


                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_DATE)) {


                    EditText editText = gridLayout.findViewWithTag("org" + list.get(i).getServerId());

                    String val = editText.getText().toString();

                    Log.d("datefield", val);
                    // Toast.makeText(mContext,"Number field"+ val, Toast.LENGTH_SHORT).show();

                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        LSOrganization lsOrganization = LSOrganization.findById(LSOrganization.class, args.getLong("someId"));
                        lsProperty1.setOrganizationOfProperty(lsOrganization);
                        lsProperty1.save();

                        Log.d("created", "created property");
                    }


                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {


                    Spinner spinner = gridLayout.findViewWithTag("org" + list.get(i).getServerId());

                    String val = spinner.getSelectedItem().toString();

                    // Toast.makeText(mContext, "Spinner " + val, Toast.LENGTH_SHORT).show();

                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        LSOrganization lsOrganization = LSOrganization.findById(LSOrganization.class, args.getLong("someId"));
                        lsProperty1.setOrganizationOfProperty(lsOrganization);

                        lsProperty1.save();

                        Log.d("created", "created property");
                    }


                }


            }


        }

        Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();

    }

    public void dynamicColumnByAmir() {

        dynamicColums = new DynamicColums(getContext());

        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class, "related_to=?", LSProperty.STORABLE_TYPE_APP_ORGANIZATION);


        if (list.size() > 0) {


            for (int i = 0; i < list.size(); i++) {

                String type = list.get(i).getColumnType();

                if (type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {

                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperties.size() > 0) {

                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "org" + list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText("", "org" + list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));

                        //Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                    }

                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {

                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperties.size() > 0) {


                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));

                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "org" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));

                        gridLayout.addView(dynamicColums.editText("", "org" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));

                        // Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                    }

                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_DATE)) {

                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperties.size() > 0) {


                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));

                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "org" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));

                        gridLayout.addView(dynamicColums.editText("", "org" + list.get(i).getServerId(), InputType.TYPE_DATETIME_VARIATION_DATE));

                        // Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                    }

                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {

                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and organization_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperties.size() > 0) {

                        int position = 0;

                        List<String> option = new ArrayList<>();


                        String spinnerDefaultVal = list.get(i).getDefaultValueOption();

                        try {
                            JSONArray jsonarray = new JSONArray(spinnerDefaultVal);
                            option.add("Select");

                            for (int j = 0; j < jsonarray.length(); j++) {
                                if (lsProperties.get(0).getValue().equals(jsonarray.getString(j))) {
                                    position = j;
                                    position++;
                                }
                                String jsonobject = jsonarray.getString(j);
                                option.add(jsonobject);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, option);

                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.spinner(dataAdapter, "org" + list.get(i).getServerId(), position));


                    } else {
                        List<String> option = new ArrayList<>();

                        int position = 0;


                        String spinnerDefaultVal = list.get(i).getDefaultValueOption();

                        try {
                            JSONArray jsonarray = new JSONArray(spinnerDefaultVal);
                            option.add("Select");

                            for (int j = 0; j < jsonarray.length(); j++) {


                                String jsonobject = jsonarray.getString(j);
                                option.add(jsonobject);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, option);

                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.spinner(dataAdapter, "org" + list.get(i).getServerId(), position));


                        //Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                    }

                }
            }


        }


    }


    public void addItemsOnSpinnerLeadStatus(View view) {
        leadStatusSpinner = view.findViewById(R.id.lead_status_spinner);
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


//    private class CustomSpinnerLeadStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
//
//        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
//            switch (pos) {
//                case 0:
//                    mOrganization.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
//                    mOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
//                    mOrganization.save();
//                    TinyBus.from(mContext.getApplicationContext()).post(new LeadContactAddedEventModel());
//                    Toast.makeText(parent.getContext(), "Status Changed to InProgress", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
//                    break;
//                case 1:
//                    mOrganization.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
//                    mOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
//                    mOrganization.save();
//                    TinyBus.from(mContext.getApplicationContext()).post(new LeadContactAddedEventModel());
//                    Toast.makeText(parent.getContext(), "Status Changed to Won", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
//                    break;
//                case 2:
//                    mOrganization.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
//                    mOrganization.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
//                    mOrganization.save();
//                    TinyBus.from(mContext.getApplicationContext()).post(new LeadContactAddedEventModel());
//                    Toast.makeText(parent.getContext(), "Status Changed to Lost", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
//                    break;
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//        }
//    }

    private void loadConnectionsData() {

        tvError = new TextView(mContext);
        tvError.setText("Loading...");
        tvError.setGravity(Gravity.CENTER);
        tvError.setVisibility(View.VISIBLE);

        sessionManager = new SessionManager(mContext);
        queue = Volley.newRequestQueue(mContext);
        fetchCustomerHistory(mOrganization.getPhone());

    }

//    private void loadSocialProfileData() {
//        LSContactProfile lsContactProfile = mOrganization.getContactProfile();
//
//        tvTweeterFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
//        tvLinkdnFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
//        tvFbFromProfile.setMovementMethod(LinkMovementMethod.getInstance());
//
//        if (lsContactProfile != null) {
////            if (lsContactProfile.getSocial_image() != null) {
////                MyDateTimeStamp.setFrescoImage(user_avatar_ind, lsContactProfile.getSocial_image());
////            }
//            if (lsContactProfile.getFirstName() != null && !lsContactProfile.getFirstName().equals("")) {
//                tvNameFromProfile.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
//            } else {
//                tvNameFromProfile.setVisibility(View.GONE);
//                tvNameFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getCity() != null && !lsContactProfile.getCity().equals("")) {
//                tvCityFromProfile.setText(lsContactProfile.getCity());
//            } else {
//                tvCityFromProfile.setVisibility(View.GONE);
//                tvCityFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getCountry() != null && !lsContactProfile.getCountry().equals("")) {
//                tvCountryFromProfile.setText(lsContactProfile.getCountry());
//            } else {
//                tvCountryFromProfile.setVisibility(View.GONE);
//                tvCountryFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getWork() != null && !lsContactProfile.getWork().equals("")) {
//                tvWorkFromProfile.setText(lsContactProfile.getWork());
//            } else {
//                tvWorkFromProfile.setVisibility(View.GONE);
//                tvWorkFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getCompany() != null && !lsContactProfile.getCompany().equals("")) {
//                tvCompanyFromProfile.setText(lsContactProfile.getCompany());
//            } else {
//                tvCompanyFromProfile.setVisibility(View.GONE);
//                tvCompanyFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getWhatsapp() != null && !lsContactProfile.getWhatsapp().equals("")) {
//                tvWhatsappFromProfile.setText(lsContactProfile.getWhatsapp());
//            } else {
//                tvWhatsappFromProfile.setVisibility(View.GONE);
//                tvWhatsappFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getTweet() != null && !lsContactProfile.getTweet().equals("")) {
//                tvTweeterFromProfile.setText(lsContactProfile.getTweet());
//            } else {
//                tvTweeterFromProfile.setVisibility(View.GONE);
//                tvTweeterFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getLinkd() != null && !lsContactProfile.getLinkd().equals("")) {
//                tvLinkdnFromProfile.setText(lsContactProfile.getLinkd());
//            } else {
//                tvLinkdnFromProfile.setVisibility(View.GONE);
//                tvLinkdnFromProfileTitle.setVisibility(View.GONE);
//            }
//            if (lsContactProfile.getFb() != null && !lsContactProfile.getFb().equals("")) {
//                tvFbFromProfile.setText(lsContactProfile.getFb());
//            } else {
//                tvFbFromProfile.setVisibility(View.GONE);
//                tvFbFromProfileTitle.setVisibility(View.GONE);
//            }
//        } else {
//            cv_social_item.setVisibility(View.GONE);
//            Log.d(TAG, "loadSocialProfileData: lsContactProfile == NULL " + mOrganization.getPhone());
//            ContactProfileProvider contactProfileProvider = new ContactProfileProvider(getActivity());
//            contactProfileProvider.getContactProfile(mOrganization.getPhone(), new LSContactProfileCallback() {
//                @Override
//                public void onSuccess(LSContactProfile result) {
//                    Log.d(TAG, "onSuccess: lsContactProfile: " + result);
//                    if (result != null) {
//                        cv_social_item.setVisibility(View.VISIBLE);
//                        Log.d(TAG, "onSuccess: Updating Data");
//                        //            if (lsContactProfile.getSocial_image() != null) {
////                MyDateTimeStamp.setFrescoImage(user_avatar_ind, lsContactProfile.getSocial_image());
////            }
//                        if (result.getFirstName() != null && !result.getFirstName().equals("")) {
//                            tvNameFromProfile.setText(result.getFirstName() + " " + result.getLastName());
//                        } else {
//                            tvNameFromProfile.setVisibility(View.GONE);
//                            tvNameFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getCity() != null && !result.getCity().equals("")) {
//                            tvCityFromProfile.setText(result.getCity());
//                        } else {
//                            tvCityFromProfile.setVisibility(View.GONE);
//                            tvCityFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getCountry() != null && !result.getCountry().equals("")) {
//                            tvCountryFromProfile.setText(result.getCountry());
//                        } else {
//                            tvCountryFromProfile.setVisibility(View.GONE);
//                            tvCountryFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getWork() != null && !result.getWork().equals("")) {
//                            tvWorkFromProfile.setText(result.getWork());
//                        } else {
//                            tvWorkFromProfile.setVisibility(View.GONE);
//                            tvWorkFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getCompany() != null && !result.getCompany().equals("")) {
//                            tvCompanyFromProfile.setText(result.getCompany());
//                        } else {
//                            tvCompanyFromProfile.setVisibility(View.GONE);
//                            tvCompanyFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getWhatsapp() != null && !result.getWhatsapp().equals("")) {
//                            tvWhatsappFromProfile.setText(result.getWhatsapp());
//                        } else {
//                            tvWhatsappFromProfile.setVisibility(View.GONE);
//                            tvWhatsappFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getTweet() != null && !result.getTweet().equals("")) {
//                            tvTweeterFromProfile.setText(result.getTweet());
//                        } else {
//                            tvTweeterFromProfile.setVisibility(View.GONE);
//                            tvTweeterFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getLinkd() != null && !result.getLinkd().equals("")) {
//                            tvLinkdnFromProfile.setText(result.getLinkd());
//                        } else {
//                            tvLinkdnFromProfile.setVisibility(View.GONE);
//                            tvLinkdnFromProfileTitle.setVisibility(View.GONE);
//                        }
//                        if (result.getFb() != null && !result.getFb().equals("")) {
//                            tvFbFromProfile.setText(result.getFb());
//                        } else {
//                            tvFbFromProfile.setVisibility(View.GONE);
//                            tvFbFromProfileTitle.setVisibility(View.GONE);
//                        }
//                    }
//                }
//            });
//        }
//    }

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
        Log.d(TAG, "fetchCustomerHistory: myUrl: " + myUrl);
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

                            if (!firstname.equalsIgnoreCase("null") && !lastname.equalsIgnoreCase("null")) {
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

                            }
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
            public Map<String, String> getHeaders() {
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
    public Loader<List<Object>> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: ");
        listLoader.clear();
        LoadingItem loadingItem = new LoadingItem();
        loadingItem.text = "Loading items...";
        listLoader.add(loadingItem);
        adapter.notifyDataSetChanged();


        switch (id) {
            case DEALS_OF_A_ORGANIZATION:
                return new DealsOfAOrganizationLoader(getActivity(), args);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Object>> loader, List<Object> data) {
        Log.d(TAG, "onLoadFinished: ");
        if (data != null) {
            if (!data.isEmpty()) {
                listLoader.clear();
                listLoader.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Object>> loader) {
        Log.d(TAG, "onLoaderReset: ");
        listLoader.clear();
        listLoader.addAll(new ArrayList<Object>());
        adapter.notifyDataSetChanged();
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

}
