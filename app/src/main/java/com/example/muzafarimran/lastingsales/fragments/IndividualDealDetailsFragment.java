package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.CallClickListener;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.ContactDetailsTabActivity;
import com.example.muzafarimran.lastingsales.adapters.LSStageSpinAdapter;
import com.example.muzafarimran.lastingsales.events.DealAddedEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.helper.DynamicColums;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSProperty;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/9/2017.
 */

public class IndividualDealDetailsFragment extends TabFragment implements View.OnClickListener {

    public static final String TAG = "IndividualDealDetailFra";
    //    TextView tvName;
//    TextView tvNumber;
    TextView tvDefaultText;
    //    TextView tvEmail;
//    TextView tvAddress;

//    private CardView cv_social_item;
//    private TextView tvNameFromProfile;
//    private TextView tvCityFromProfile;
//    private TextView tvCountryFromProfile;
//    private TextView tvWorkFromProfile;
//    private TextView tvCompanyFromProfile;
//    private TextView tvWhatsappFromProfile;
//    private TextView tvTweeterFromProfile;
//    private TextView tvLinkdnFromProfile;
//    private TextView tvFbFromProfile;
//    private TextView tvNameFromProfileTitle;
//    private TextView tvCityFromProfileTitle;
//    private TextView tvCountryFromProfileTitle;
//    private TextView tvWorkFromProfileTitle;
//    private TextView tvCompanyFromProfileTitle;
//    private TextView tvWhatsappFromProfileTitle;
//    private TextView tvTweeterFromProfileTitle;
//    private TextView tvLinkdnFromProfileTitle;
//    private TextView tvFbFromProfileTitle;
//    private LinearLayout llDynamicConnectionsContainer;
//    private TextView tvError;
//    private SessionManager sessionManager;
//    private RequestQueue queue;

    //    private Spinner statusSpinner;
    private Spinner isPrivateSpinner;
    private Spinner stageSpinner;
    List<LSStage> stageList = new ArrayList<LSStage>();
    //    private Button bSave;
    private Long dealIDLong;
    private Context mContext;
    private LSDeal mDeal;
    private LSContact mContact;
    private View.OnClickListener callClickListener = null;
    private EditText valueEditText;
    private TextView currencyTextView;
//    private EditText success_rateEditText;
//    private EditText success_ETAEditText;
    private TextView created_agoTextView;

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

    Bundle args;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        args=this.getArguments();
        dealIDLong = bundle.getLong("someId");
        setHasOptionsMenu(true);
    }

    GridLayout gridLayout;
    DynamicColums dynamicColums;
    Button save;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.deal_profile_details_fragment, container, false);
//        tvName = (TextView) view.findViewfById(R.id.tvName);
//        tvNumber = (TextView) view.findViewById(R.id.tvNumber);
//        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
//        tvAddress = (TextView) view.findViewById(R.id.tvAddress);

//        //      social Profile views
//        cv_social_item = view.findViewById(R.id.cv_social_item);
//        tvNameFromProfile = view.findViewById(R.id.tvNameFromProfile);
//        tvNameFromProfileTitle = view.findViewById(R.id.tvNameFromProfileTitle);
//        tvCityFromProfile = view.findViewById(R.id.tvCityFromProfile);
//        tvCityFromProfileTitle = view.findViewById(R.id.tvCityFromProfileTitle);
//        tvCountryFromProfile = view.findViewById(R.id.tvCountryFromProfile);
//        tvCountryFromProfileTitle = view.findViewById(R.id.tvCountryFromProfileTitle);
//        tvWorkFromProfile = view.findViewById(R.id.tvWorkFromProfile);
//        tvWorkFromProfileTitle = view.findViewById(R.id.tvWorkFromProfileTitle);
//        tvCompanyFromProfile = view.findViewById(R.id.tvCompanyFromProfile);
//        tvCompanyFromProfileTitle = view.findViewById(R.id.tvCompanyFromProfileTitle);
//        tvWhatsappFromProfile = view.findViewById(R.id.tvWhatsappFromProfile);
//        tvWhatsappFromProfileTitle = view.findViewById(R.id.tvWhatsappFromProfileTitle);
//        tvTweeterFromProfile = view.findViewById(R.id.tvTweeterFromProfile);
//        tvTweeterFromProfileTitle = view.findViewById(R.id.tvTweeterFromProfileTitle);
//        tvLinkdnFromProfile = view.findViewById(R.id.tvLinkdnFromProfile);
//        tvLinkdnFromProfileTitle = view.findViewById(R.id.tvLinkdnFromProfileTitle);
//        tvFbFromProfile = view.findViewById(R.id.tvFbFromProfile);
//        tvFbFromProfileTitle = view.findViewById(R.id.tvFbFromProfileTitle);
//
//        llDynamicConnectionsContainer = (LinearLayout) view.findViewById(R.id.llDynamicConnectionsContainer);

        save=view.findViewById(R.id.dealsave);

        tvDefaultText = (TextView) view.findViewById(R.id.tvDefaultText);
//        bSave = (Button) view.findViewById(R.id.bSave);
        tvDefaultText.setVisibility(View.GONE);
//        bSave.setVisibility(View.GONE);
//        bSave.setOnClickListener(v -> {
//            Toast.makeText(mContext, "Saving..", Toast.LENGTH_SHORT).show();
//        });
//        addItemsOnSpinnerDealStatus(view);


        gridLayout=view.findViewById(R.id.griddeal);


        dynamicColumnByAmir();


        save.setOnClickListener(this);

        addItemsOnSpinnerDealStage(view);
        addViews(view);
        addItemsOnSpinnerDealIsPrivate(view);
        setHasOptionsMenu(true);
        return view;
    }
    public void dynamicColumnByAmir() {

        dynamicColums=new DynamicColums(getContext());

        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class,"related_to=?", LSProperty.STORABLE_TYPE_APP_DEAL);


        if (list.size() > 0) {


            for (int i = 0; i < list.size(); i++) {

                String type=list.get(i).getColumnType();

                if(type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)){

                    List<LSProperty> lsProperties=LSProperty.find(LSProperty.class,"column_id=?",list.get(i).getServerId());

                    if(lsProperties.size()>0) {

                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "deal"+list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));
                    }else{
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText("", "deal"+list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));

                        //Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                    }

                }
                else if(type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)){

                    List<LSProperty> lsProperties=LSProperty.find(LSProperty.class,"column_id=?",list.get(i).getServerId());

                    if(lsProperties.size()>0) {


                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(),"tag"));

                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(),"deal"+list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    }else{
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(),"tag"));

                        gridLayout.addView(dynamicColums.editText("","deal"+list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));

                        // Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                    }

                }

                else if(type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)){

                    List<LSProperty> lsProperties=LSProperty.find(LSProperty.class,"column_id=?",list.get(i).getServerId());

                    if(lsProperties.size()>0) {


                        int position=0;
                        List<String> option=new ArrayList<>();



                        String spinnerDefaultVal = list.get(i).getDefaultValueOption();

                        try {
                            JSONArray jsonarray = new JSONArray(spinnerDefaultVal);
                            option.add("Select");

                            for (int j = 0; j < jsonarray.length(); j++) {
                                if(lsProperties.get(0).getValue().equals(jsonarray.getString(j))){
                                    position=j;
                                    position++;
                                }

                                String jsonobject = jsonarray.getString(j);
                                option.add(jsonobject);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, option);

                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(),"tag"));
                        gridLayout.addView(dynamicColums.spinner(dataAdapter,"deal"+list.get(i).getServerId(),position));


                    }


                }else{
                    List<String> option=new ArrayList<>();


                    int position=0;

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

                    gridLayout.addView(dynamicColums.textView(list.get(i).getName(),"tag"));
                    gridLayout.addView(dynamicColums.spinner(dataAdapter,"deal"+list.get(i).getServerId(),position));


                    //Toast.makeText(mContext, "Can't compare colummnId & serverID", Toast.LENGTH_SHORT).show();
                }




            }


        }


    }

    @Override
    public void onClick(View v) {


        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class,"related_to=?", LSProperty.STORABLE_TYPE_APP_DEAL);

        if(list.size()>0){

            for(int i=0;i<list.size();i++){
                String type=list.get(i).getColumnType();


                if(type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)){


                    EditText editText=(EditText)gridLayout.findViewWithTag("deal"+list.get(i).getServerId());

                    String val=editText.getText().toString();




                    Log.d("textfield",val);
                    //Toast.makeText(mContext, "TextField"+val, Toast.LENGTH_SHORT).show();


                    List<LSProperty> lsProperty=LSProperty.find(LSProperty.class,"column_id=?",list.get(i).getServerId());

                    if(lsProperty.size()>0){
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).save();
                        Log.d("saved","value saved");
                    }else{
                        LSProperty lsProperty1=new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));

                        lsProperty1.save();

                        Log.d("created","created property");
                    }



                }
                if(type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)){


                    EditText editText=(EditText)gridLayout.findViewWithTag("deal"+list.get(i).getServerId());

                    String val=editText.getText().toString();

                    Log.d("numberfield",val);
                    // Toast.makeText(mContext,"Number field"+ val, Toast.LENGTH_SHORT).show();

                    List<LSProperty> lsProperty=LSProperty.find(LSProperty.class,"column_id=?",list.get(i).getServerId());

                    if(lsProperty.size()>0){
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).save();
                        Log.d("saved","value saved");
                    }else{
                        LSProperty lsProperty1=new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));

                        lsProperty1.save();

                        Log.d("created","created property");
                    }



                }

                else if(type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)){


                    Spinner spinner=(Spinner) gridLayout.findViewWithTag("deal"+list.get(i).getServerId());

                    String val=spinner.getSelectedItem().toString();

                    //Toast.makeText(mContext, "Spinner "+val, Toast.LENGTH_SHORT).show();

                    List<LSProperty> lsProperty=LSProperty.find(LSProperty.class,"column_id=?",list.get(i).getServerId());

                    if(lsProperty.size()>0){
                        lsProperty.get(0).setValue(val);
                        lsProperty.get(0).save();
                        Log.d("saved","value saved");
                    }else{
                        LSProperty lsProperty1=new LSProperty();
                        lsProperty1.setValue(val);
                        lsProperty1.setStorableType(list.get(i).getRelatedTo());
                        lsProperty1.setColumnId(list.get(i).getServerId());
                        lsProperty1.setStorableId(String.valueOf(args.getLong("someId")));

                        lsProperty1.save();

                        Log.d("created","created property");
                    }



                }





            }



        }
        Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();


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
            if (mContact != null) {
                CardView cv_item = getView().findViewById(R.id.cv_item);
                View myLayout = getView().findViewById(R.id.include);
                RelativeLayout user_profile_group_wrapper = myLayout.findViewById(R.id.user_profile_group_wrapper);
                user_profile_group_wrapper.setVisibility(View.GONE);
                RelativeLayout rl_container_buttons = myLayout.findViewById(R.id.rl_container_buttons);
                rl_container_buttons.setVisibility(View.GONE);
                TextView tvContactName = myLayout.findViewById(R.id.tvContactName);
                TextView tvNumber = myLayout.findViewById(R.id.tvNumber);
                ImageView imSmartBadge = myLayout.findViewById(R.id.imSmartBadge);
                ImageView whatsapp_icon = myLayout.findViewById(R.id.whatsapp_icon);
                ImageView call_icon = myLayout.findViewById(R.id.call_icon);

                LSContactProfile lsContactProfile = mContact.getContactProfile();
                if (lsContactProfile == null) {
                    imSmartBadge.setVisibility(View.GONE);
                    Log.d(TAG, "createOrUpdate: Not Found in mContact Table now getting from ContactProfileProvider: " + mContact.toString());
                    lsContactProfile = LSContactProfile.getProfileFromNumber(mContact.getPhoneOne());
                } else {
                    imSmartBadge.setVisibility(View.VISIBLE);
                    Log.d(TAG, "createOrUpdate: Found in mContact Table: " + mContact);
                }
                if (mContact.getContactName() != null) {
                    if (mContact.getContactName().equals("null")) {
                        tvContactName.setText("");
                    } else if (mContact.getContactName().equals("Unlabeled Contact") || mContact.getContactName().equals("Ignored Contact")) {
                        String name = PhoneNumberAndCallUtils.getContactNameFromLocalPhoneBook(mContext, mContact.getPhoneOne());
                        if (name != null) {
                            tvContactName.setText(name);
                        } else {
                            if (lsContactProfile != null) {
                                tvContactName.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
                            } else {
                                tvContactName.setText(mContact.getPhoneOne());
                            }
                        }
                    } else {
                        tvContactName.setText(mContact.getContactName());
                    }
                } else {
                    if (lsContactProfile != null) {
                        tvContactName.setText(lsContactProfile.getFirstName() + " " + lsContactProfile.getLastName());
                    } else {
                        tvContactName.setText(mContact.getPhoneOne());
                    }
                }
                whatsapp_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PackageManager packageManager = mContext.getPackageManager();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        try {
                            String url = "https://api.whatsapp.com/send?phone=" + mContact.getPhoneOne() + "&text=" + URLEncoder.encode("", "UTF-8");
                            i.setPackage("com.whatsapp");
                            i.setData(Uri.parse(url));
                            if (i.resolveActivity(packageManager) != null) {
                                mContext.startActivity(i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                callClickListener = new CallClickListener(mContext);
                call_icon.setOnClickListener(this.callClickListener);
                call_icon.setTag(mContact.getPhoneOne());
                cv_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailsActivityIntent = new Intent(mContext, ContactDetailsTabActivity.class);
                        long contactId = mContact.getId();
                        detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                        mContext.startActivity(detailsActivityIntent);
                    }
                });
            }
        if (mDeal.getValue() != null) {
            valueEditText.setText(mDeal.getValue());
        }
        if (mDeal.getCurrency() != null) {
            currencyTextView.setText(mDeal.getCurrency());
        }
//        if (mDeal.getSuccessRate() != null) {
//            success_rateEditText.setText(mDeal.getSuccessRate());
//        }
//        if (mDeal.getSuccessEta() != null) {
//            success_ETAEditText.setText(mDeal.getSuccessEta());
//        }
        if (mDeal.getCreatedAt() != null) {
            created_agoTextView.setText(mDeal.getCreatedAt());
        }

//            // populate status spinner
//            switch (mDeal.getStatus()) {
//                case LSDeal.DEAL_STATUS_PENDING:
//                    statusSpinner.setSelection(0, false);
//                    break;
//                case LSDeal.DEAL_STATUS_CLOSED_WON:
//                    statusSpinner.setSelection(1, false);
//                    break;
//                case LSDeal.DEAL_STATUS_CLOSED_LOST:
//                    statusSpinner.setSelection(2, false);
//                    break;
//            }
            switch (mDeal.getIsPrivate()) {
                case LSDeal.DEAL_VISIBILITY_STATUS_COMPANY:
                    isPrivateSpinner.setSelection(0, false);
                    break;
                case LSDeal.DEAL_VISIBILITY_STATUS_PRIVATE:
                    isPrivateSpinner.setSelection(1, false);
                    break;
            }
            //populate stage spinner
            LSStage lsStage = LSStage.getStageFromServerId(mDeal.getWorkflowStageId());
            int index = 0;
            for (int i = 0; i < stageList.size(); i++) {
                if (lsStage != null && stageList.get(i).getName().equalsIgnoreCase(lsStage.getName())) {
                    index = i;
                }
            }
            stageSpinner.setSelection(index, false);
        }
//        statusSpinner.post(new Runnable() {
//            public void run() {
//                statusSpinner.setOnItemSelectedListener(new CustomSpinnerDealStatusOnItemSelectedListener());
//            }
//        });
        isPrivateSpinner.post(new Runnable() {
            public void run() {
                isPrivateSpinner.setOnItemSelectedListener(new CustomSpinnerDealIsPrivateOnItemSelectedListener());
            }
        });
        stageSpinner.post(new Runnable() {
            public void run() {
                stageSpinner.setOnItemSelectedListener(new CustomSpinnerDealStageOnItemSelectedListener());
            }
        });
//        if (mContact != null) {
//            loadSocialProfileData();
//            loadConnectionsData();
//        }
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

//    public void addItemsOnSpinnerDealStatus(View view) {
//        statusSpinner = (Spinner) view.findViewById(R.id.statusSpinner);
//        List<String> list = new ArrayList<String>();
//        list.add("Pending");
//        list.add("Won");
//        list.add("Lost");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        statusSpinner.setAdapter(dataAdapter);
//    }

    private void addViews(View view) {
        valueEditText = (EditText) view.findViewById(R.id.valueEditText);
        currencyTextView = (TextView) view.findViewById(R.id.currencyTextView);
//        success_rateEditText = (EditText) view.findViewById(R.id.success_rateEditText);
//        success_ETAEditText = (EditText) view.findViewById(R.id.success_ETAEditText);
        created_agoTextView = (TextView) view.findViewById(R.id.created_agoTextView);
    }

    public void addItemsOnSpinnerDealIsPrivate(View view) {
        isPrivateSpinner = (Spinner) view.findViewById(R.id.isPrivateSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Company");
        list.add("Private");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isPrivateSpinner.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinnerDealStage(View view) {
        stageSpinner = (Spinner) view.findViewById(R.id.stage_spinner);
        LSWorkflow defaultWorkFlow = LSWorkflow.getDefaultWorkflow();
        Collection<LSStage> lsStages = LSStage.getAllStagesInPositionSequenceByWorkflowServerId(defaultWorkFlow.getServerId());
        if (lsStages != null) {
            stageList.addAll(lsStages);
        }
        LSStageSpinAdapter dataAdapter = new LSStageSpinAdapter(mContext, R.layout.spinner_item, stageList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stageSpinner.setAdapter(dataAdapter);
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

//    public class CustomSpinnerDealStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
//
//        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
//            switch (pos) {
//                case 0:
//                    mDeal.setStatus(LSDeal.DEAL_STATUS_PENDING);
//                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
//                    mDeal.save();
//                    Toast.makeText(parent.getContext(), "Status Changed to Pending", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
//                    break;
//                case 1:
//                    mDeal.setStatus(LSDeal.DEAL_STATUS_CLOSED_WON);
//                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
//                    mDeal.save();
//                    Toast.makeText(parent.getContext(), "Status Changed to Won", Toast.LENGTH_SHORT).show();
//                    dataSenderAsync.run();
//                    break;
//                case 2:
//                    mDeal.setStatus(LSDeal.DEAL_STATUS_CLOSED_LOST);
//                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
//                    mDeal.save();
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

    public class CustomSpinnerDealIsPrivateOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
            switch (pos) {
                case 0:
                    mDeal.setIsPrivate(LSDeal.DEAL_VISIBILITY_STATUS_COMPANY);
                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                    mDeal.save();
                    TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                    Toast.makeText(parent.getContext(), "Status Changed to Company", Toast.LENGTH_SHORT).show();
                    dataSenderAsync.run();
                    break;
                case 1:
                    mDeal.setIsPrivate(LSDeal.DEAL_VISIBILITY_STATUS_PRIVATE);
                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                    mDeal.save();
                    TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                    Toast.makeText(parent.getContext(), "Status Changed to Private", Toast.LENGTH_SHORT).show();
                    dataSenderAsync.run();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private class CustomSpinnerDealStageOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            LSStage selectedStage = (LSStage) parent.getItemAtPosition(pos);
//            LSStage lsStage = LSStage.getStageByName(selectedStepName);
            if (selectedStage != null) {
                mDeal.setWorkflowStageId(selectedStage.getServerId());
                mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                mDeal.save();
                Toast.makeText(mContext, "Stage Changed to " + selectedStage.getName(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(parent.getContext(), "Stage Changed to " + mDeal.getWorkflowStageId(), Toast.LENGTH_SHORT).show();
                TinyBus.from(mContext.getApplicationContext()).post(new DealAddedEventModel());
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
                dataSenderAsync.run();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

//    private void loadSocialProfileData() {
//        LSContactProfile lsContactProfile = mContact.getContactProfile();
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
//            Log.d(TAG, "loadSocialProfileData: lsContactProfile == NULL " + mContact.getPhoneOne());
//            ContactProfileProvider contactProfileProvider = new ContactProfileProvider(getActivity());
//            contactProfileProvider.getContactProfile(mContact.getPhoneOne(), new LSContactProfileCallback() {
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
//
//    private void loadConnectionsData() {
//
//        tvError = new TextView(mContext);
//        tvError.setText("Loading...");
//        tvError.setGravity(Gravity.CENTER);
//        llDynamicConnectionsContainer.addView(tvError);
//        tvError.setVisibility(View.VISIBLE);
//
//        sessionManager = new SessionManager(mContext);
//        queue = Volley.newRequestQueue(mContext);
//        fetchCustomerHistory(mContact.getPhoneOne());
//
//    }
//
//    private void fetchCustomerHistory(final String number) {
////        Log.d(TAG, "fetchCustomersHistoryFunc: Fetching Data...");
////        Log.d(TAG, "fetchCustomerHistory: Number: " + number);
////        Log.d(TAG, "fetchCustomerHistory: Token: " + sessionManager.getLoginToken());
//        final int MY_SOCKET_TIMEOUT_MS = 60000;
//        final String BASE_URL = MyURLs.GET_CUSTOMER_HISTORY;
//        Uri builtUri = Uri.parse(BASE_URL)
//                .buildUpon()
////                .appendQueryParameter("phone", "+92 301 4775234")
//                .appendQueryParameter("phone", "" + number)
////                .appendQueryParameter("api_token", "NVAPN67dqZU4bBW18ykrtylvfyXFogt1dh3Dgw2XsOFuQKaWLySRv058v1If")
//                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
//                .build();
//        final String myUrl = builtUri.toString();
////        Log.d(TAG, "fetchCustomerHistory: myUrl: " + myUrl);
//        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String respon) {
//                tvError.setVisibility(View.GONE);
////                Log.d(TAG, "onResponse() response = [" + respon + "]");
//                try {
//                    JSONObject jObj = new JSONObject(respon);
////                    int responseCode = jObj.getInt("responseCode");
//                    JSONObject response = jObj.getJSONObject("response");
//                    JSONArray dataArray = response.getJSONArray("data");
////                    Log.d(TAG, "onResponse: dataArray Length: " + dataArray.length());
//                    if (dataArray.length() > 0) {
//                        for (int i = 0; i < dataArray.length(); i++) {
////                            Log.d(TAG, "onResponse: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//                            JSONObject jsonobject = dataArray.getJSONObject(i);
//                            String last_call = jsonobject.getString("last_call");
//                            String user_id = jsonobject.getString("user_id");
//                            String duration = jsonobject.getString("duration");
//                            String firstname = jsonobject.getString("firstname");
//                            String lastname = jsonobject.getString("lastname");
//                            String role_id = jsonobject.getString("role_id");
//                            String name = jsonobject.getString("name");
//
////                            Log.d(TAG, "onResponse: last_call: " + last_call);
////                            Log.d(TAG, "onResponse: user_id: " + user_id);
////                            Log.d(TAG, "onResponse: duration: " + duration);
////                            Log.d(TAG, "onResponse: firstname: " + firstname);
////                            Log.d(TAG, "onResponse: lastname: " + lastname);
////                            Log.d(TAG, "onResponse: role_id: " + role_id);
////                            Log.d(TAG, "onResponse: name: " + name);
//
////                            Display display = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
////                            int width = display.getWidth();
//
//                            LinearLayout llParentHorizontal = new LinearLayout(mContext.getApplicationContext()); // Huawei mate
//                            llParentHorizontal.setFocusable(true);
//                            llParentHorizontal.setFocusableInTouchMode(true);
//                            llParentHorizontal.setOrientation(LinearLayout.HORIZONTAL);
//                            llParentHorizontal.setWeightSum(10);
//
//                            LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            layoutParamsRow.setMargins(8, 8, 8, 8);
//                            llParentHorizontal.setLayoutParams(layoutParamsRow);
//
//                            TextView tvCallerHistoryName = new TextView(mContext);
//                            tvCallerHistoryName.setPadding(0, 0, 0, 0);
//                            tvCallerHistoryName.setMaxLines(3);
//                            tvCallerHistoryName.setGravity(Gravity.LEFT);
//                            tvCallerHistoryName.setTextSize(14);
//                            tvCallerHistoryName.setTypeface(tvCallerHistoryName.getTypeface(), Typeface.BOLD);
//                            if (!user_id.equals(sessionManager.getKeyLoginId())) {
//                                tvCallerHistoryName.setText("Last contacted " + firstname + " " + lastname);
//                            } else {
//                                tvCallerHistoryName.setText("Last contacted with me");
//                            }
//
//                            TextView tvCallerHistoryLastCallTimeAgo = new TextView(mContext);
//                            tvCallerHistoryLastCallTimeAgo.setText("(" + PhoneNumberAndCallUtils.getDaysAgo(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call), mContext) + ")");
//                            tvCallerHistoryLastCallTimeAgo.setPadding(0, 0, 0, 0);
//                            tvCallerHistoryLastCallTimeAgo.setGravity(Gravity.LEFT);
//                            tvCallerHistoryLastCallTimeAgo.setTextSize(10);
//
//                            TextView tvCallerHistoryLastCallDateTime = new TextView(mContext);
//                            tvCallerHistoryLastCallDateTime.setText(PhoneNumberAndCallUtils.getDateTimeStringFromMiliseconds(PhoneNumberAndCallUtils.getMillisFromSqlFormattedDate(last_call), "dd-MMM-yyyy"));
//                            tvCallerHistoryLastCallDateTime.setPadding(0, 0, 0, 0);
//                            tvCallerHistoryLastCallDateTime.setGravity(Gravity.RIGHT);
//                            tvCallerHistoryLastCallDateTime.setTextSize(14);
//
//                            LinearLayout l1ChildLeft = new LinearLayout(mContext);
//                            l1ChildLeft.setOrientation(LinearLayout.VERTICAL);
//                            LinearLayout.LayoutParams lpChildLeft = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            lpChildLeft.weight = 7;
//                            l1ChildLeft.setLayoutParams(lpChildLeft);
//                            l1ChildLeft.addView(tvCallerHistoryName);
//                            l1ChildLeft.addView(tvCallerHistoryLastCallTimeAgo);
//
//                            LinearLayout llChildRight = new LinearLayout(mContext);
//                            llChildRight.setOrientation(LinearLayout.HORIZONTAL);
//                            llChildRight.setGravity(Gravity.RIGHT);
//                            LinearLayout.LayoutParams lpChildRight = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//                            lpChildRight.weight = 3;
//                            lpChildRight.gravity = Gravity.RIGHT;
//                            llChildRight.setLayoutParams(lpChildRight);
//                            llChildRight.addView(tvCallerHistoryLastCallDateTime);
//
//                            llParentHorizontal.addView(l1ChildLeft);
//                            llParentHorizontal.addView(llChildRight);
//
//                            llDynamicConnectionsContainer.addView(llParentHorizontal);
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "onErrorResponse: CouldNotGetCustomerHistory");
//                if (mContext != null) {
//                    if (!NetworkAccess.isNetworkAvailable(mContext)) {
//                        tvError.setText("Internet is required to view connections");
//                    } else {
//                        try {
//                            if (error.networkResponse != null) {
//                                if (error.networkResponse.statusCode == 404) {
//                                    tvError.setText("Connections not found");
//                                } else {
//                                    tvError.setText("Error Loading");
//                                }
//                            } else {
//                                tvError.setText("Poor Internet Connectivity");
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    tvError.setText("Error Loading");
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//        };
//        int MY_MAX_RETRIES = 3;
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                MY_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//    }

}
