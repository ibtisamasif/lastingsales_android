package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.LoadingItem;
import com.example.muzafarimran.lastingsales.listloaders.LeadsLoader;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlankFragment3 extends Fragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    public static final String TAG = "BlankFragment3";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int LEAD_LOADER_ID = 3;

    private String mParam1;
    private String mParam2;

    private List<Object> listLoader = new ArrayList<Object>();
    private MyRecyclerViewAdapter adapter;


    private LinearLayout llFilterDynamicArea;
    private Button bFilter;
    private RelativeLayout relativeLayout;
    private LinearLayout l;
    private LinearLayout l2;
    private int width;
    private LinearLayout l3;
    private Spinner spinnerNames;
    private Spinner spinnerValues;
    private ArrayList<String> listFilter;
    private ArrayList<String> listValues = new ArrayList<String>();
    private ArrayList<LSContact> lsContacts = new ArrayList<LSContact>();

    private Button bAll;
    private Button bInProgress;
    private Button bWon;
    private Button bLost;
    private Button bInActive;


    public BlankFragment3() {
    }

    public static BlankFragment3 newInstance(String param1, String param2) {
        Log.d(TAG, "newInstance: ");
        BlankFragment3 fragment = new BlankFragment3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_blank3, container, false);
        adapter = new MyRecyclerViewAdapter(getActivity(), listLoader); //TODO potential bug getActivity can be null.
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);

        bAll = view.findViewById(R.id.bAll);
        bInProgress = view.findViewById(R.id.bInProgress);
        bWon = view.findViewById(R.id.bWon);
        bLost = view.findViewById(R.id.bLost);
        bInActive = view.findViewById(R.id.bInActive);

        bFilter = (Button) view.findViewById(R.id.bFilter);
        llFilterDynamicArea = (LinearLayout) view.findViewById(R.id.llFilterDynamicArea);

        Display display = ((WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth() / 2;
        bFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
                if (allColumns != null) {
                    Log.d(TAG, "onClick: allColumns Size: " + allColumns.size());
                    Log.d(TAG, "onClick: allColumns : " + allColumns.toString());

                    l = new LinearLayout(getActivity());
                    l.setFocusable(true);
                    l.setFocusableInTouchMode(true);
                    l.setOrientation(LinearLayout.HORIZONTAL);
                    l.setWeightSum(10);

                    LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsRow.setMargins(0, 5, 0, 5);
                    l.setLayoutParams(layoutParamsRow);

                    listFilter = new ArrayList<String>();
                    for (LSDynamicColumns oneColumn : allColumns) {
                        Log.d(TAG, "onClick: oneColumn: " + oneColumn.toString());
                        listFilter.add(oneColumn.getName());
                    }

                    spinnerNames = new Spinner(getActivity());
                    Log.d(TAG, "onClick: listHash: " + listFilter.size());
                    spinnerNames.setId(listFilter.size());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, listFilter);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerNames.setAdapter(dataAdapter);

                    spinnerNames.post(new Runnable() {
                        @Override
                        public void run() {
                            spinnerNames.setOnItemSelectedListener(new DynamicSpinnerNamesOnItemSelectedListener());
                        }
                    });

                    LinearLayout.LayoutParams lpSpinner = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpSpinner.setMargins(0, 20, 0, 0);

                    relativeLayout = new RelativeLayout(getActivity());
                    relativeLayout.setBackgroundResource(R.drawable.dynamic_border);
                    relativeLayout.addView(spinnerNames, lpSpinner);

                    l2 = new LinearLayout(getActivity());
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp2.weight = 5;
                    l2.setLayoutParams(lp2);
                    l2.addView(relativeLayout);

//                        l.addView(l1);
                    l.addView(l2);

                    llFilterDynamicArea.addView(l);

                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        getLoaderManager().initLoader(LEAD_LOADER_ID, null, BlankFragment3.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        chipFunction();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    private void chipFunction() {
        Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
        if (contacts != null) {
            bAll.setText("ALL" + "(" + contacts.size() + ")");
        }
        bAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 1;
                bAll.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_selected));
                bInProgress.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                onChipClick("All");
//                Toast.makeText(getActivity(), "bAll Leads", Toast.LENGTH_SHORT).show();
            }
        });

        List<LSContact> contactsInprogress = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_INPROGRESS);
        if (contactsInprogress != null) {
            bInProgress.setText("INPROGRESS" + "(" + contactsInprogress.size() + ")");
        }
        bInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 2;
                bAll.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_selected));
                bWon.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                onChipClick("InProgress");
//                Toast.makeText(getActivity(), "bInProgress Leads", Toast.LENGTH_SHORT).show();
            }
        });

        List<LSContact> contactsWon = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_WON);
        if (contactsWon != null) {
            bWon.setText("WON" + "(" + contactsWon.size() + ")");
        }
        bWon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 3;
                bAll.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_selected));
                bLost.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                onChipClick("Won");
//                Toast.makeText(getActivity(), "bWon Leads", Toast.LENGTH_SHORT).show();
            }
        });

        List<LSContact> contactsLost = LSContact.getDateArrangedSalesContactsByLeadSalesStatus(LSContact.SALES_STATUS_CLOSED_LOST);
        if (contactsLost != null) {
            bLost.setText("LOST" + "(" + contactsLost.size() + ")");
        }
        bLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 4;
                bAll.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_selected));
                bInActive.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                onChipClick("Lost");
//                Toast.makeText(getActivity(), "bLost Leads", Toast.LENGTH_SHORT).show();
            }
        });

        Collection<LSContact> contactsInactive = LSContact.getAllInactiveLeadContacts();
        if (contactsInactive != null) {
            bInActive.setText("INACTIVE" + "(" + contactsInactive.size() + ")");
        }
        bInActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                chipItem.selected = 5;
                bAll.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInProgress.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bWon.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bLost.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_normal));
                bInActive.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_chip_selected));
                onChipClick("InActive");
//                Toast.makeText(getActivity(), "bInActive Leads", Toast.LENGTH_SHORT).show();
            }
        });
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
            case LEAD_LOADER_ID:
                return new LeadsLoader(getActivity(), args);
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

    public void onChipClick(String chip) {
        Bundle bundle = new Bundle();
        switch (chip) {
            case "All":
                bundle.putString("whichLeads", "All");
                getLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, this).forceLoad();
//                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "InProgress":
                bundle.putString("whichLeads", "InProgress");
                getLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, this).forceLoad();
//                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "Won":
                bundle.putString("whichLeads", "Won");
                getLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, this).forceLoad();
//                Toast.makeText(this, "WonListened", Toast.LENGTH_SHORT).show();
                break;

            case "Lost":
                bundle.putString("whichLeads", "Lost");
                getLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, this).forceLoad();
//                Toast.makeText(this, "InProgressListened", Toast.LENGTH_SHORT).show();
                break;

            case "InActive":
                bundle.putString("whichLeads", "InActive");
                getLoaderManager().restartLoader(LEAD_LOADER_ID, bundle, this).forceLoad();
//                Toast.makeText(this, "InActiveListened", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    private class DynamicSpinnerNamesOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            listValues.clear();
            Log.d(TAG, "onItemSelected: listHash: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + listFilter.size());


            Toast.makeText(view.getContext(), "onItemSelectedspinnerNames: " + spinnerNames.getSelectedItem(), Toast.LENGTH_SHORT).show();

            //TODO search for the value of spinnerName.getSelectedItem() in the dynamic column of each contact and store result in an array to populate in SpinnerValues.


            Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
            if (contacts != null && contacts.size() > 0) {

                for (LSContact oneContact : contacts) {

                    if (oneContact.getDynamic() != null) {

                        DynamicColumnBuilderVersion1 dynamicColumnBuilderVersion1 = new DynamicColumnBuilderVersion1();

                        Log.d(TAG, "dynamicColumns: getVersion = 0");
                        dynamicColumnBuilderVersion1.parseJson(oneContact.getDynamic());
                        Log.d(TAG, "dynamicColumnsJSONN: " + oneContact.getDynamic());
                        ArrayList<DynamicColumnBuilderVersion1.Column> dynColumns = dynamicColumnBuilderVersion1.getColumns();
                        for (DynamicColumnBuilderVersion1.Column oneDynamicColumns : dynColumns) {
                            if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_TEXT)) {
                                Log.d(TAG, "onItemSelected: COLUMN_TYPE_TEXT");
                                if (oneDynamicColumns.value != null && !oneDynamicColumns.value.equalsIgnoreCase("")) {
                                    String selectedSpinnerName = spinnerNames.getSelectedItem().toString();
                                    if (selectedSpinnerName.equals(oneDynamicColumns.name)) {
                                        if (!listValues.contains(oneDynamicColumns.value)) {
                                            listValues.add(oneDynamicColumns.value);
                                        }
                                    }
                                }
                            } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                                Log.d(TAG, "onItemSelected: COLUMN_TYPE_NUMBER");
                                if (oneDynamicColumns.value != null && !oneDynamicColumns.value.equalsIgnoreCase("")) {
                                    String selectedSpinnerName = spinnerNames.getSelectedItem().toString();
                                    if (selectedSpinnerName.equals(oneDynamicColumns.name)) {
                                        if (!listValues.contains(oneDynamicColumns.value)) {
                                            listValues.add(oneDynamicColumns.value);
                                        }
                                    }
                                }
                            } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                                Log.d(TAG, "onItemSelected: COLUMN_TYPE_SINGLE");
//                            final Spinner s = (Spinner) llFilterDynamicArea.findViewById(Integer.parseInt(oneDynamicColumns.id));
//                            if (s != null) {
//                                List<String> listFilter = (List<String>) s.getTag();
//                                int index = -1;
//                                for (int i = 0; i < listFilter.size(); i++) {
//                                    if (oneDynamicColumns.value.equals(listFilter.get(i))) {
//                                        index = i;
//                                    }
//                                }
//                                if (oneContact.getDynamic() != null && !oneContact.getDynamic().equals("")) {
//
//                                    Log.d(TAG, "dynamicColumns: " + index);
//                                    s.setSelection(index, false);
//                                }
//                                s.post(new Runnable() {
//                                    @Override
//                                    public void run() {
////                                        s.setOnItemSelectedListener(new IndividualContactDetailsFragment.DynamicSpinnerOnItemSelectedListener());
//                                    }
//                                });
//                            } else {
//                                Log.d(TAG, "this spinner dynamic column was set filled for lead but column is no more");
//                            }
                            }
                        }
                    }

                }

            }

            spinnerValues = new Spinner(view.getContext());
            spinnerValues.setId(listValues.size() + 1);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_item, listValues);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerValues.setAdapter(dataAdapter);

//            spinnerValues.setSelection(position);


            spinnerValues.post(new Runnable() {
                @Override
                public void run() {
                    spinnerValues.setOnItemSelectedListener(new DynamicSpinnerValuesOnItemSelectedListener());
                }
            });
            LinearLayout.LayoutParams lpSpinner = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpSpinner.setMargins(0, 20, 0, 0);
            lpSpinner.weight = 5;

            relativeLayout = new RelativeLayout(view.getContext());
            relativeLayout.setBackgroundResource(R.drawable.dynamic_border);
            relativeLayout.removeAllViews();
            relativeLayout.addView(spinnerValues, lpSpinner);

            l3 = new LinearLayout(view.getContext());
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp2.weight = 5;
            l3.setLayoutParams(lp2);
            l3.addView(relativeLayout);

            l.removeAllViews();
            l.addView(l2);
            l.addView(l3);
            llFilterDynamicArea.removeView(l);
            llFilterDynamicArea.addView(l);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(parent.getContext(), "onNothingSelected", Toast.LENGTH_SHORT).show();
        }
    }

    private class DynamicSpinnerValuesOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(view.getContext(), spinnerNames.getSelectedItem() + " " + spinnerValues.getSelectedItem(), Toast.LENGTH_SHORT).show();
            //TODO apply query here to search for all LEADS date arranged leads having  Name = spinnerName.getSelectedItem() && value = spinnerValues.getSelectedItem().
            lsContacts.clear();

            Collection<LSContact> contacts = LSContact.getDateArrangedSalesContacts();
            if (contacts != null && contacts.size() > 0) {

                for (LSContact oneContact : contacts) {

                    if (oneContact.getDynamic() != null) {

                        DynamicColumnBuilderVersion1 dynamicColumnBuilderVersion1 = new DynamicColumnBuilderVersion1();

                        Log.d(TAG, "dynamicColumns: getVersion = 0");
                        dynamicColumnBuilderVersion1.parseJson(oneContact.getDynamic());
                        Log.d(TAG, "dynamicColumnsJSONN: " + oneContact.getDynamic());
                        ArrayList<DynamicColumnBuilderVersion1.Column> dynColumns = dynamicColumnBuilderVersion1.getColumns();
                        for (DynamicColumnBuilderVersion1.Column oneDynamicColumns : dynColumns) {
                            if (oneDynamicColumns.name != null && !oneDynamicColumns.name.equalsIgnoreCase("") && oneDynamicColumns.value != null && !oneDynamicColumns.value.equalsIgnoreCase("")) {
                                String selectedSpinnerName = spinnerNames.getSelectedItem().toString();
                                String selectedSpinnerValue = spinnerValues.getSelectedItem().toString();
                                if (selectedSpinnerName.equals(oneDynamicColumns.name) && selectedSpinnerValue.equals(oneDynamicColumns.value)) {
                                    lsContacts.add(oneContact);
                                }
                            }
                        }
                    }
                }
            }


//            ArrayList<LSContact> lsContacts = LSContact.getContactsByDynamicNameAndValue("myCar","honda City");
//            if (lsContacts.size() > 0) {
//                Log.e(TAG, "onItemSelected: lsContacts: " + lsContacts.toString());
//            }

            if (lsContacts != null) {
                Log.d(TAG, "onItemSelected: lsContacts: MATCHED : " + lsContacts);
                for (LSContact oneMatchedContact :
                        lsContacts) {
                    Log.d(TAG, "oneMatchedContact: " + oneMatchedContact.getPhoneOne());
                }
//                Toast.makeText(view.getContext(), "" + lsContacts.toString(), Toast.LENGTH_SHORT).show();

//                NavigationBottomMainActivity navigationBottomMainActivity = new NavigationBottomMainActivity();
//                navigationBottomMainActivity.onLoadFilteredContacts(lsContacts);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

}