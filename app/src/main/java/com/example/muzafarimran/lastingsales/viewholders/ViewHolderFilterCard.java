package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;
import com.example.muzafarimran.lastingsales.utils.DynamicColumnBuilderVersion1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderFilterCard extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderFilterCard";
    private final LinearLayout llFilterDynamicArea;

    private Button bFilter;
    private Spinner spinnerNames;
    private ArrayList<String> list;
    private RelativeLayout relativeLayout;
    private LinearLayout l;
    private LinearLayout l2;
    private int width;
    private LinearLayout l3;
    private ArrayList<String> listValues = new ArrayList<String>();

    public ViewHolderFilterCard(View v) {
        super(v);
        bFilter = v.findViewById(R.id.bFilter);
        llFilterDynamicArea = (LinearLayout) v.findViewById(R.id.llFilterDynamicArea);
    }

    public void bind(Object item, int position, Context mContext) {
        Display display = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth() / 2;


        bFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
                if (allColumns != null) {
                    Log.d(TAG, "onClick: allColumns Size: " + allColumns.size());
                    Log.d(TAG, "onClick: allColumns : " + allColumns.toString());

                    l = new LinearLayout(mContext);
                    l.setFocusable(true);
                    l.setFocusableInTouchMode(true);
                    l.setOrientation(LinearLayout.HORIZONTAL);
                    l.setWeightSum(10);

                    LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsRow.setMargins(0, 5, 0, 5);
                    l.setLayoutParams(layoutParamsRow);

                    list = new ArrayList<String>();
                    for (LSDynamicColumns oneColumn : allColumns) {
                        Log.d(TAG, "onClick: oneColumn: " + oneColumn.toString());
                        list.add(oneColumn.getName());
                    }

                    spinnerNames = new Spinner(mContext);
                    Log.d(TAG, "onClick: listHash: " + list.size());
                    spinnerNames.setId(list.size());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
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

                    relativeLayout = new RelativeLayout(mContext);
                    relativeLayout.setBackgroundResource(R.drawable.dynamic_border);
                    relativeLayout.addView(spinnerNames, lpSpinner);

                    l2 = new LinearLayout(mContext);
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
    }

    private class DynamicSpinnerNamesOnItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            listValues.clear();
            Log.d(TAG, "onItemSelected: listHash: +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + list.size());


            Toast.makeText(view.getContext(), "onItemSelected: " + spinnerNames.getSelectedItem(), Toast.LENGTH_SHORT).show();

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
                                if (oneDynamicColumns.value != null && !oneDynamicColumns.value.equalsIgnoreCase("")){
                                    String selectedSpinnerName = spinnerNames.getSelectedItem().toString();
                                    if(selectedSpinnerName.equals(oneDynamicColumns.name)){
                                        if (!listValues.contains(oneDynamicColumns.value)){
                                            listValues.add(oneDynamicColumns.value);
                                        }
                                    }
                                }
                            } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_NUMBER)) {
                                Log.d(TAG, "onItemSelected: COLUMN_TYPE_NUMBER");
                                if (oneDynamicColumns.value != null && !oneDynamicColumns.value.equalsIgnoreCase("")){
                                    String selectedSpinnerName = spinnerNames.getSelectedItem().toString();
                                    if(selectedSpinnerName.equals(oneDynamicColumns.name)){
                                        if (!listValues.contains(oneDynamicColumns.value)){
                                            listValues.add(oneDynamicColumns.value);
                                        }
                                    }
                                }
                            } else if (oneDynamicColumns.column_type.equals(LSDynamicColumns.COLUMN_TYPE_SINGLE)) {
                                Log.d(TAG, "onItemSelected: COLUMN_TYPE_SINGLE");
//                            final Spinner s = (Spinner) llFilterDynamicArea.findViewById(Integer.parseInt(oneDynamicColumns.id));
//                            if (s != null) {
//                                List<String> list = (List<String>) s.getTag();
//                                int index = -1;
//                                for (int i = 0; i < list.size(); i++) {
//                                    if (oneDynamicColumns.value.equals(list.get(i))) {
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


//            ArrayList<LSContact> lsContacts = LSContact.getContactsByDynamicName("honda City");
//            if (lsContacts.size() > 0) {
//
//            }


            Spinner spinnerValues = new Spinner(view.getContext());
            spinnerValues.setId(listValues.size() + 1);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_item, listValues);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerValues.setAdapter(dataAdapter);

//            spinnerValues.setSelection(position);


            spinnerValues.post(new Runnable() {
                @Override
                public void run() {
//                    spinnerValues.setOnItemSelectedListener(new DynamicSpinnerValuesOnItemSelectedListener());
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
}
