package com.example.muzafarimran.lastingsales.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import android.widget.DatePicker;
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
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.listeners.LSContactProfileCallback;
import com.example.muzafarimran.lastingsales.providers.ContactProfileProvider;
import com.example.muzafarimran.lastingsales.providers.listloaders.DealsOfALeadLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSProperty;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion1;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion2;
import com.example.muzafarimran.lastingsales.utils.DynamicColums;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/9/2017.
 */

public class IndividualContactDetailsFragment extends TabFragment implements LoaderManager.LoaderCallbacks<List<Object>>, View.OnClickListener {

    public static final String TAG = "IndividualContDetailFra";
    public static final java.lang.String DEALS_LEAD_ID = "deals_lead_id";
    public static final int DEALS_OF_A_LEAD = 31;
    static DynamicColumnBuilderVersion1 dynamicColumnBuilderVersion1;
    static DynamicColumnBuilderVersion2 dynamicColumnBuilderVersion2;
    private static Bundle args;
    //    TextView tvName;
    TextView tvNumber;
    TextView tvDefaultText;
    //    TextView tvEmail;
    TextView tvAddress;
    RecyclerView mRecyclerView = null;
    GridLayout gridLayout;
    //    private Spinner leadStatusSpinner;
    Button save;
    DynamicColums dynamicColums;
    private List<Object> listLoader = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;
    private Long contactIDLong;
    private LSContact mContact;
    private LinearLayout ll;
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
        contactIDLong = args.getLong("someId");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_profile_details_fragment, container, false);
        tvNumber = view.findViewById(R.id.tvNumber);
        tvAddress = view.findViewById(R.id.tvAddress);

        gridLayout = view.findViewById(R.id.grid);

        adapter = new MyRecyclerViewAdapter(getActivity(), listLoader); //TODO potential bug getActivity can be null.
        RecyclerView mRecyclerView = view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setNestedScrollingEnabled(false);

        save = view.findViewById(R.id.csave);

        save.setOnClickListener(this);

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

        llDynamicConnectionsContainer = view.findViewById(R.id.llDynamicConnectionsContainer);
        tvDefaultText = view.findViewById(R.id.tvDefaultText);
        tvDefaultText.setVisibility(View.GONE);
        dynamicColumnByAmir();
        dynamicColumns(view);
        getLoaderManager().initLoader(DEALS_OF_A_LEAD, args, IndividualContactDetailsFragment.this);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        contactIDLong = args.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
        if (mContact != null && mContact.getPhoneOne() != null) {
            tvNumber.setText(mContact.getPhoneOne());
        }
        if (mContact != null && mContact.getContactAddress() != null) {
            tvAddress.setText(mContact.getContactAddress());
        }
        getLoaderManager().restartLoader(DEALS_OF_A_LEAD, args, IndividualContactDetailsFragment.this);
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

    // FIXME: 7/20/2018

    @Override
    public void onClick(View v) {
        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class, "related_to=?", LSProperty.STORABLE_TYPE_APP_LEAD);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String type = list.get(i).getColumnType();
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                    EditText editText = gridLayout.findViewWithTag("lead" + list.get(i).getServerId());
                    String val = editText.getText().toString();
                    Log.d("textfield", val);
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
//                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        LSContact lsContact = LSContact.findById(LSContact.class, args.getLong("someId"));
                        lsProperty1.setContactOfProperty(lsContact);
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty1.save();
                        Log.d("created", "created property");
                    }
                }
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                    EditText editText = gridLayout.findViewWithTag("lead" + list.get(i).getServerId());
                    String val = editText.getText().toString();
                    Log.d("numberfield", val);
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
//                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        LSContact lsContact = LSContact.findById(LSContact.class, args.getLong("someId"));
                        lsProperty1.setContactOfProperty(lsContact);
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty1.save();
                        Log.d("created", "created property");
                    }
                }
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_DATE)) {
                    EditText editText = gridLayout.findViewWithTag("lead" + list.get(i).getServerId());
                    String val = editText.getText().toString();
                    Log.d("numberfield", val);
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
//                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        LSContact lsContact = LSContact.findById(LSContact.class, args.getLong("someId"));
                        lsProperty1.setContactOfProperty(lsContact);
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty1.save();
                        Log.d("created", "created property");
                    }
                }
               else if (type.equals(LSDynamicColumns.COLUMN_TYPE_MULTI)) {


                    EditText editText = gridLayout.findViewWithTag("lead" + list.get(i).getServerId());

                    String val = editText.getText().toString();

                    Log.d("multifield", val);
                    // Toast.makeText(mContext,"Number field"+ val, Toast.LENGTH_SHORT).show();

                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);

                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);

                        LSContact lsContact = LSContact.findById(LSContact.class, args.getLong("someId"));

                        lsProperty1.setContactOfProperty(lsContact);

                        lsProperty1.save();

                        Log.d("created", "created property");
                    }


                }


                else if (type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                    Spinner spinner = gridLayout.findViewWithTag("lead" + list.get(i).getServerId());
                    String val = spinner.getSelectedItem().toString();
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperty.size() > 0) {
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty.get(0).save();
                        Log.d("saved", "value saved");
                    } else {
                        LSProperty lsProperty1 = new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
//                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));
                        LSContact lsContact = LSContact.findById(LSContact.class, args.getLong("someId"));
                        lsProperty1.setContactOfProperty(lsContact);
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty1.save();
                        Log.d("created", "created property");
                    }
                }
            }
        }
        DataSenderAsync.getInstance(mContext).run();
        Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
    }

    public void dynamicColumnByAmir() {
        dynamicColums = new DynamicColums(getContext());
        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class, "related_to=?", LSProperty.STORABLE_TYPE_APP_LEAD);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String type = list.get(i).getColumnType();
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperties.size() > 0) {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "lead" + list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText("", "lead" + list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));
                    }
                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperties.size() > 0) {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "lead" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText("", "lead" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    }
                }  else if (type.equals(LSDynamicColumns.COLUMN_TYPE_DATE)) {

                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperties.size() > 0) {


                        EditText dateColumn=dynamicColums.dateEditText(lsProperties.get(0).getValue(), "lead" + list.get(i).getServerId(), InputType.TYPE_DATETIME_VARIATION_DATE);

                        dateColumn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogFragment dialogFragment=new SelectDateFragment(dateColumn);
                                dialogFragment.show(getFragmentManager(),"Date Picker");
                                Log.d("click","ondate column");

                            }
                        });

                        //generate dynamically
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));

                        gridLayout.addView(dateColumn);
                    } else {
                        EditText dateColumn=dynamicColums.dateEditText("", "lead" + list.get(i).getServerId(), InputType.TYPE_DATETIME_VARIATION_DATE);

                        dateColumn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogFragment dialogFragment=new SelectDateFragment(dateColumn);
                                dialogFragment.show(getFragmentManager(),"Date Picker");
                                Log.d("click","ondate column");
                                Log.d("click","ondate column");
                            }
                        });
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));

                        gridLayout.addView(dateColumn);


                        // Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                    }

                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_MULTI)) {

                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));

                    if (lsProperties.size() > 0) {



                        EditText multi=dynamicColums.dateEditText(lsProperties.get(0).getValue(), "lead" + list.get(i).getServerId(),InputType.TYPE_CLASS_TEXT);

                        String spinnerDefaultVal = list.get(i).getDefaultValueOption();

                        List<String> option=new ArrayList<>();

                        try {
                            JSONArray jsonarray = new JSONArray(spinnerDefaultVal);


                            for (int j = 0; j < jsonarray.length(); j++) {


                                String jsonobject = jsonarray.getString(j);
                                option.add(jsonobject);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        multi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showMultiDialog(option,multi);
                            }
                        });



                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(multi);
                    } else {
                        EditText multi=dynamicColums.dateEditText("", "lead" + list.get(i).getServerId(),InputType.TYPE_CLASS_TEXT);

                        String spinnerDefaultVal = list.get(i).getDefaultValueOption();

                        List<String> option=new ArrayList<>();

                        try {
                            JSONArray jsonarray = new JSONArray(spinnerDefaultVal);


                            for (int j = 0; j < jsonarray.length(); j++) {


                                String jsonobject = jsonarray.getString(j);
                                option.add(jsonobject);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        multi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showMultiDialog(option,multi);
                            }
                        });



                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(multi); }

                }




                else if (type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and contact_of_property=? ",
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
                        gridLayout.addView(dynamicColums.spinner(dataAdapter, "lead" + list.get(i).getServerId(), position));
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
                        gridLayout.addView(dynamicColums.spinner(dataAdapter, "lead" + list.get(i).getServerId(), ++position));
                    }
                }
            }
        }
    }


    private void showMultiDialog(List colors,EditText multi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Convert the color array to list
        final List<String> colorsList = colors;

        boolean checkedColors[]=new boolean[colors.size()];

        String[] strings =(String[]) colors.toArray(new String[0]);
        builder.setMultiChoiceItems(strings, checkedColors, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                // Update the current focused item's checked status
                checkedColors[which] = isChecked;

                // Get the current focused item
                String currentItem = colorsList.get(which);

                // Notify the current action
         /*       Toast.makeText(getContext(),
                        currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
          */  }
        });

        // Specify the dialog is not cancelable
        builder.setCancelable(false);

        // Set a title for alert dialog


        // Set the positive/yes button click listener
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something whebuilder.setTitle("Multi Select");n click positive button
                multi.setText("");
                for (int i = 0; i<checkedColors.length; i++){
                    boolean checked = checkedColors[i];
                    if (checked) {
                        multi.setText(multi.getText() + colorsList.get(i) + ",");
                    }
                }
            }
        });

        // Set the negative/no button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the negative button
            }
        });

        // Set the neutral/cancel button click listener
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click the neutral button
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();

    }
    private static int year_x;
    private static int month_x;
    private static int day_x;
    public static   class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        EditText dateColumn;

        public SelectDateFragment() {
        }

        public SelectDateFragment(EditText dateColumn) {
            this.dateColumn = dateColumn;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm+1, dd);

            year_x=yy;
            month_x=mm;
            day_x=dd;
        }
        public void populateSetDate(int year, int month, int day) {
            dateColumn.setText(year+"-"+month+"-"+day);

        }

    }



    private void dynamicColumns(View view) {
        Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//////////////////////////////////////////////////////////////
// Populating LEAD data
//////////////////////////////////////////////////////////////

        List<LSProperty> lsProperties = LSProperty.listAll(LSProperty.class);

        List<LSDynamicColumns> lsDynamicColumns = LSDynamicColumns.listAll(LSDynamicColumns.class);

        Log.d(TAG, "Populating Lead Data");

        contactIDLong = args.getLong("someId");
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

                            EditText et = ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            } else {
                                Log.d(TAG, "this text dynamic column was set filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {

                            EditText et = ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            } else {
                                Log.d(TAG, "this number dynamic column was filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {

                            final Spinner s = ll.findViewById(Integer.parseInt(oneDynamicColumns.id));
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
                            } else {
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
                        //find column_type from LSDynamic column using the name of column from leads dynamic column.
                        if (oneDynamicColumns.column_type != null && oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {

                            EditText et = ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            } else {
                                Log.d(TAG, "this text dynamic column was set filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type != null && oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {

                            EditText et = ll.findViewWithTag(oneDynamicColumns.id);
                            if (et != null) {
                                et.setText(oneDynamicColumns.value);
                            } else {
                                Log.d(TAG, "this number dynamic column was set filled for lead but column is no more");
                            }

                        } else if (oneDynamicColumns.column_type != null && oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {

                            final Spinner s = ll.findViewById(Integer.parseInt(oneDynamicColumns.id));
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
                            } else {
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

                                llDynamicConnectionsContainer.addView(llParentHorizontal);
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
            case DEALS_OF_A_LEAD:
                return new DealsOfALeadLoader(getActivity(), args);
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

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

}
