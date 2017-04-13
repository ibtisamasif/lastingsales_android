package com.example.muzafarimran.lastingsales.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.events.LeadContactAddedEventModel;
import com.example.muzafarimran.lastingsales.providers.DynamicColumnBuilder;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.SyncStatus;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.tinybus.Subscribe;
import de.halfbit.tinybus.TinyBus;

/**
 * Created by ibtisam on 1/9/2017.
 */

public class IndividualContactDetailsFragment extends TabFragment {

    public static final String TAG = "IndividualConDetailFrag";
    TextView tvName;
    TextView tvNumber;
//    TextView tvEmail;
//    TextView tvAddress;
    ListView listView = null;
    MaterialSearchView searchView;
    private Long contactIDLong;
    private Spinner leadStatusSpinner;
    private Button bSave;
    private LSContact mContact;
    private TinyBus bus;
    private LinearLayout ll;
    static DynamicColumnBuilder dynamicColumnBuilder;

    public static IndividualContactDetailsFragment newInstance(int page, String title, Long id) {
        IndividualContactDetailsFragment fragmentFirst = new IndividualContactDetailsFragment();
        dynamicColumnBuilder = new DynamicColumnBuilder();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        args.putLong("someId", id);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus = TinyBus.from(getActivity().getApplicationContext());
        bus.register(this);
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
        if (mContact != null && mContact.getContactName() != null) {
            tvName.setText(mContact.getContactName());
        }
        if (mContact != null && mContact.getPhoneOne() != null) {
            tvNumber.setText(mContact.getPhoneOne());
        }
//        if (mContact != null && mContact.getContactEmail() != null) {
//            tvEmail.setText(mContact.getContactEmail());
//        }
//        if (mContact != null && mContact.getContactAddress() != null) {
//            tvAddress.setText(mContact.getContactAddress());
//        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_profile_details_fragment, container, false);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvNumber = (TextView) view.findViewById(R.id.tvNumber);
//        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
//        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        bSave = (Button) view.findViewById(R.id.contactDetailsSaveButton);
//        bSave.setVisibility(View.GONE);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
                for (LSDynamicColumns dynamicColumns : allColumns) {
                    if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                        DynamicColumnBuilder.Column column = dynamicColumnBuilder.getById(Integer.parseInt(dynamicColumns.getServerId()));
                        EditText et = (EditText) ll.findViewWithTag(dynamicColumns.getServerId());
                        String currentValue = et.getText().toString();
                        // if not exists already
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilder.Column column1 = new DynamicColumnBuilder.Column();
                            column1.id = dynamicColumns.getServerId();
                            column1.name = dynamicColumns.getName();
                            column1.column_type = dynamicColumns.getColumnType();
                            column1.value = currentValue;
                            dynamicColumnBuilder.addColumn(column1);

                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilder.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        }
                    } else if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                        DynamicColumnBuilder.Column column = dynamicColumnBuilder.getById(Integer.parseInt(dynamicColumns.getServerId()));
                        EditText et = (EditText) ll.findViewWithTag(dynamicColumns.getServerId());
                        String currentValue = et.getText().toString();
                        //if current value is default no need to add a columns
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilder.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilder.Column column1 = new DynamicColumnBuilder.Column();
                            column1.id = dynamicColumns.getServerId();
                            column1.name = dynamicColumns.getName();
                            column1.column_type = dynamicColumns.getColumnType();
                            column1.value = currentValue;
                            dynamicColumnBuilder.addColumn(column1);

                        }
                    } else if (dynamicColumns.getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                        DynamicColumnBuilder.Column column = dynamicColumnBuilder.getById(Integer.parseInt(dynamicColumns.getServerId()));
                        Spinner s = (Spinner) ll.findViewById(Integer.parseInt((dynamicColumns.getServerId())));

                        String currentValue = s.getSelectedItem().toString();
                        //if current value is default no need to add a columns
                        if (dynamicColumns.getDefaultValueOption().equals(currentValue)) {
                            //pass
                        } else if (column != null) {
                            //update column
                            dynamicColumnBuilder.getById(Integer.parseInt(dynamicColumns.getServerId())).value = currentValue;

                        } else if (column == null) {
                            //add new column
                            DynamicColumnBuilder.Column column1 = new DynamicColumnBuilder.Column();
                            column1.id = dynamicColumns.getServerId();
                            column1.name = dynamicColumns.getName();
                            column1.column_type = dynamicColumns.getColumnType();
                            column1.value = currentValue;
                            dynamicColumnBuilder.addColumn(column1);

                        }
                    }
                }

                mContact.setDynamic(dynamicColumnBuilder.buildJSON());
                if (mContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_ADD_SYNCED) || mContact.getSyncStatus().equals(SyncStatus.SYNC_STATUS_LEAD_UPDATE_SYNCED)) {
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                }
                mContact.save();
                Log.d(TAG, "onClick: "+mContact.getDynamic());
                DataSenderAsync dataSenderAsync = new DataSenderAsync(getActivity());
                dataSenderAsync.execute();
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        addItemsOnSpinnerLeadStatus(view);

        dynamicColumns(view);
        setHasOptionsMenu(true);
        return view;
    }

    private void dynamicColumns(View view) {
        ll = (LinearLayout) view.findViewById(R.id.contactDetailsDropDownLayoutinner);
        Display display = ((WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth() / 2;
        Log.d(TAG, "Display SIZE: "+display);
        List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();// TODO if column is null dont render view
        Log.d(TAG, "onCreateView: Size: " + allColumns.size());
        for (int i = 0; i < allColumns.size(); i++) {
            LinearLayout l = new LinearLayout(getContext());
            l.setFocusable(true);
            l.setFocusableInTouchMode(true);
            l.setOrientation(LinearLayout.HORIZONTAL);
            if (allColumns.get(i).getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {

                Log.d(TAG, "dynamicColumns: matched text");
                TextView tv = new TextView(getContext());
                tv.setText(allColumns.get(i).getName());
                final EditText et = new EditText(getContext());
                et.setTextColor(getResources().getColor(R.color.black));
                et.setText(allColumns.get(i).getDefaultValueOption());
                et.setMinimumWidth(400);
                String id = allColumns.get(i).getServerId();
                et.setTag(id);
//                et.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Text field clicked: " + et.getId() + " " + et.getTag() + " " + et.getText(), Toast.LENGTH_SHORT).show();
//                        et.getId();
//                    }
//                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                l.addView(tv, lp);
                l.addView(et, lp);
                ll.addView(l);

            } else if (allColumns.get(i).getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                Log.d(TAG, "dynamicColumns: matched number");
                TextView tv = new TextView(getContext());
                tv.setText(allColumns.get(i).getName());
                final EditText et = new EditText(getContext());
                et.setTextColor(getResources().getColor(R.color.black));
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.setText(allColumns.get(i).getDefaultValueOption());
                et.setMinimumWidth(400);
                et.setTag(allColumns.get(i).getServerId());
//                et.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Number field clicked: " + et.getId() + " " + et.getTag() + " " + et.getText(), Toast.LENGTH_SHORT).show();
//                    }
//                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                l.addView(tv, lp);
                l.addView(et, lp);
                ll.addView(l);
            } else if (allColumns.get(i).getColumnType().equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                Log.d(TAG, "dynamicColumns: matched single");
                TextView tv = new TextView(getContext());
                tv.setText(allColumns.get(i).getName());
                Spinner s = new Spinner(getContext());
                s.setId(Integer.parseInt(allColumns.get(i).getServerId()));
                List<String> list = new ArrayList<String>();
                String spinnerDefaultVal = allColumns.get(i).getDefaultValueOption();
                Log.d(TAG, "dynamicColumns: SpinnerDefaultValues: " + spinnerDefaultVal);

                try {
                    JSONArray jsonarray = new JSONArray(spinnerDefaultVal);
                    list.add("");
                    for (int j = 0; j < jsonarray.length(); j++) {
                        String jsonobject = jsonarray.getString(j);
                        list.add(jsonobject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                s.setTag(list);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(dataAdapter);
                LinearLayout.LayoutParams lpTotalLayout = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
                double d = display.getWidth()/2.4;
                LinearLayout.LayoutParams lpSpinner = new LinearLayout.LayoutParams((int)d, LinearLayout.LayoutParams.MATCH_PARENT);
                l.addView(tv, lpTotalLayout);
                l.addView(s, lpSpinner);
                ll.addView(l);
            }
        }
        Log.d(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//////////////////////////////////////////////////////////////
// Populating LEAD data
//////////////////////////////////////////////////////////////

        Log.d(TAG, "Populating Lead Data");
        Log.d(TAG, "Populating Lead Data");
        Log.d(TAG, "Populating Lead Data");

        Bundle bundle = this.getArguments();
        contactIDLong = bundle.getLong("someId");
        mContact = LSContact.findById(LSContact.class, contactIDLong);
        Log.d(TAG, "contactID: " + mContact.getId());
        Log.d(TAG, "dynamicColumns: " + mContact.getDynamic());

        try {
            if (mContact.getDynamic() != null) {
                dynamicColumnBuilder.parseJson(mContact.getDynamic());
                Log.d(TAG, "dynamicColumnsJSONN: " + mContact.getDynamic());
                ArrayList<DynamicColumnBuilder.Column> dynColumns = dynamicColumnBuilder.getColumns();
                for (DynamicColumnBuilder.Column oneDynamicColumns : dynColumns) {
                    if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                        EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
                        et.setText(oneDynamicColumns.value);
                    } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                        EditText et = (EditText) ll.findViewWithTag(oneDynamicColumns.id);
                        et.setText(oneDynamicColumns.value);
                    } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                        Spinner s = (Spinner) ll.findViewById(Integer.parseInt(oneDynamicColumns.id));
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
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////
    }


    public void addItemsOnSpinnerLeadStatus(View view) {
        leadStatusSpinner = (Spinner) view.findViewById(R.id.lead_status_spinner);
        List<String> list = new ArrayList<String>();
        list.add("InProgress");
        list.add("Close Won");
        list.add("Closed Lost");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leadStatusSpinner.setAdapter(dataAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView = null;
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
            DataSenderAsync dataSenderNew = new DataSenderAsync(getActivity().getApplicationContext());
            switch (pos) {
                case 0:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to InProgress", Toast.LENGTH_SHORT).show();
                    dataSenderNew.execute();
                    break;
                case 1:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Won", Toast.LENGTH_SHORT).show();
                    dataSenderNew.execute();
                    break;
                case 2:
                    mContact.setContactSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
                    mContact.setSyncStatus(SyncStatus.SYNC_STATUS_LEAD_UPDATE_NOT_SYNCED);
                    mContact.save();
                    Toast.makeText(parent.getContext(), "Status Changed to Lost", Toast.LENGTH_SHORT).show();
                    dataSenderNew.execute();
                    break;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Subscribe
    public void onSalesContactAddedEventModel(LeadContactAddedEventModel event) {
        Log.d(TAG, "onSalesContactAddedEventModel: CalledInFrag");
    }
}
