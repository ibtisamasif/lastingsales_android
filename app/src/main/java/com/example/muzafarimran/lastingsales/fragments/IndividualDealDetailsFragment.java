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
import com.example.muzafarimran.lastingsales.app.SyncStatus;
import com.example.muzafarimran.lastingsales.events.DealEventModel;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.providers.models.LSDeal;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.providers.models.LSProperty;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.utils.DynamicColums;
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
    TextView tvDefaultText;
    List<LSStage> stageList = new ArrayList<LSStage>();
    Bundle args;
    GridLayout gridLayout;
    DynamicColums dynamicColums;
    Button save;
    private Spinner isPrivateSpinner;
    private Spinner stageSpinner;
    private Long dealIDLong;
    private Context mContext;
    private LSDeal mDeal;
    private LSContact mContact;
    private View.OnClickListener callClickListener = null;
    private EditText valueEditText;
    private TextView currencyTextView;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        args = this.getArguments();
        dealIDLong = bundle.getLong("someId");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.deal_profile_details_fragment, container, false);
        save = view.findViewById(R.id.dealsave);
        tvDefaultText = view.findViewById(R.id.tvDefaultText);
        tvDefaultText.setVisibility(View.GONE);
        gridLayout = view.findViewById(R.id.griddeal);
        dynamicColumnByAmir();
        save.setOnClickListener(this);
        addItemsOnSpinnerDealStage(view);
        addViews(view);
        addItemsOnSpinnerDealIsPrivate(view);
        setHasOptionsMenu(true);
        return view;
    }

    public void dynamicColumnByAmir() {
        dynamicColums = new DynamicColums(getContext());
        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class, "related_to=?", LSProperty.STORABLE_TYPE_APP_DEAL);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String type = list.get(i).getColumnType();
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperties.size() > 0) {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "deal" + list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText("", "deal" + list.get(i).getServerId(), InputType.TYPE_CLASS_TEXT));
                    }
                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperties.size() > 0) {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "deal" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText("", "deal" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    }
                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_DATE)) {
                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
                            list.get(i).getServerId(), String.valueOf(args.getLong("someId")));
                    if (lsProperties.size() > 0) {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText(lsProperties.get(0).getValue(), "deal" + list.get(i).getServerId(), InputType.TYPE_CLASS_NUMBER));
                    } else {
                        gridLayout.addView(dynamicColums.textView(list.get(i).getName(), "tag"));
                        gridLayout.addView(dynamicColums.editText("", "deal" + list.get(i).getServerId(), InputType.TYPE_DATETIME_VARIATION_DATE));
                    }
                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                    List<LSProperty> lsProperties = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
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
                        gridLayout.addView(dynamicColums.spinner(dataAdapter, "deal" + list.get(i).getServerId(), position));
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
                        gridLayout.addView(dynamicColums.spinner(dataAdapter, "deal" + list.get(i).getServerId(), position));
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        List<LSDynamicColumns> list = LSDynamicColumns.find(LSDynamicColumns.class, "related_to=?", LSProperty.STORABLE_TYPE_APP_DEAL);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String type = list.get(i).getColumnType();
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                    EditText editText = gridLayout.findViewWithTag("deal" + list.get(i).getServerId());
                    String val = editText.getText().toString();
                    Log.d("textfield", val);
                    //Toast.makeText(mContext, "TextField"+val, Toast.LENGTH_SHORT).show();
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
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
                        LSDeal lsDeal = LSDeal.findById(LSDeal.class, args.getLong("someId"));
                        lsProperty1.setDealOfProperty(lsDeal);
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty1.save();
                        Log.d("created", "created property");
                    }

                }
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                    EditText editText = gridLayout.findViewWithTag("deal" + list.get(i).getServerId());
                    String val = editText.getText().toString();
                    Log.d("numberfield", val);
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
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
                        LSDeal lsDeal = LSDeal.findById(LSDeal.class, args.getLong("someId"));
                        lsProperty1.setDealOfProperty(lsDeal);
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty1.save();
                        Log.d("created", "created property");
                    }
                }
                if (type.equals(LSDynamicColumns.COLUMN_TYPE_DATE)) {
                    EditText editText = gridLayout.findViewWithTag("deal" + list.get(i).getServerId());
                    String val = editText.getText().toString();
                    Log.d("numberfield", val);
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
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
                        LSDeal lsDeal = LSDeal.findById(LSDeal.class, args.getLong("someId"));
                        lsProperty1.setDealOfProperty(lsDeal);
                        lsProperty1.setSyncStatus(SyncStatus.SYNC_STATUS_PROPERTY_ADD_OR_UPDATE_NOT_SYNCED);
                        lsProperty1.save();
                        Log.d("created", "created property");
                    }
                } else if (type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                    Spinner spinner = gridLayout.findViewWithTag("deal" + list.get(i).getServerId());
                    String val = spinner.getSelectedItem().toString();
                    List<LSProperty> lsProperty = LSProperty.find(LSProperty.class, "column_id=? and deal_of_property=?",
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
                        LSDeal lsDeal = LSDeal.findById(LSDeal.class, args.getLong("someId"));
                        lsProperty1.setDealOfProperty(lsDeal);
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
                CardView cv_item = getView().findViewById(R.id.cv_item);
                cv_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailsActivityIntent = new Intent(mContext, ContactDetailsTabActivity.class);
                        long contactId = mContact.getId();
                        detailsActivityIntent.putExtra(ContactDetailsTabActivity.KEY_CONTACT_ID, contactId + "");
                        mContext.startActivity(detailsActivityIntent);
                    }
                });
            } else {
                View myLayout = getView().findViewById(R.id.include);
                myLayout.setVisibility(View.GONE);
            }
            if (mDeal.getValue() != null) {
                valueEditText.setText(mDeal.getValue());
            }
            if (mDeal.getCurrency() != null) {
                currencyTextView.setText(mDeal.getCurrency());
            }
            if (mDeal.getCreatedAt() != null) {
                created_agoTextView.setText(mDeal.getCreatedAt());
            }
            switch (mDeal.getIsPrivate()) {
                case LSDeal.DEAL_VISIBILITY_STATUS_COMPANY:
                    isPrivateSpinner.setSelection(0, false);
                    break;
                case LSDeal.DEAL_VISIBILITY_STATUS_PRIVATE:
                    isPrivateSpinner.setSelection(1, false);
                    break;
            }
            LSStage lsStage = LSStage.getStageFromServerId(mDeal.getWorkflowStageId());
            int index = 0;
            for (int i = 0; i < stageList.size(); i++) {
                if (lsStage != null && stageList.get(i).getName().equalsIgnoreCase(lsStage.getName())) {
                    index = i;
                }
            }
            stageSpinner.setSelection(index, false);
        }
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

    private void addViews(View view) {
        valueEditText = view.findViewById(R.id.valueEditText);
        currencyTextView = view.findViewById(R.id.currencyTextView);
        created_agoTextView = view.findViewById(R.id.created_agoTextView);
    }

    public void addItemsOnSpinnerDealIsPrivate(View view) {
        isPrivateSpinner = view.findViewById(R.id.isPrivateSpinner);
        List<String> list = new ArrayList<String>();
        list.add("Company");
        list.add("Private");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isPrivateSpinner.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinnerDealStage(View view) {
        stageSpinner = view.findViewById(R.id.stage_spinner);
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

    public class CustomSpinnerDealIsPrivateOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
            switch (pos) {
                case 0:
                    mDeal.setIsPrivate(LSDeal.DEAL_VISIBILITY_STATUS_COMPANY);
                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                    mDeal.save();
                    TinyBus.from(mContext.getApplicationContext()).post(new DealEventModel());
                    Toast.makeText(parent.getContext(), "Status Changed to Company", Toast.LENGTH_SHORT).show();
                    dataSenderAsync.run();
                    break;
                case 1:
                    mDeal.setIsPrivate(LSDeal.DEAL_VISIBILITY_STATUS_PRIVATE);
                    mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                    mDeal.save();
                    TinyBus.from(mContext.getApplicationContext()).post(new DealEventModel());
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
            if (selectedStage != null) {
                mDeal.setWorkflowStageId(selectedStage.getServerId());
                mDeal.setSyncStatus(SyncStatus.SYNC_STATUS_DEAL_UPDATE_NOT_SYNCED);
                mDeal.save();
                Toast.makeText(mContext, "Stage Changed to " + selectedStage.getName(), Toast.LENGTH_SHORT).show();
                TinyBus.from(mContext.getApplicationContext()).post(new DealEventModel());
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(mContext.getApplicationContext());
                dataSenderAsync.run();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}
