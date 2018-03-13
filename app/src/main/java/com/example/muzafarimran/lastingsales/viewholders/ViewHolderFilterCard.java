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
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;

import java.util.ArrayList;
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


//                        ArrayList<LSContact> lsContacts = LSContact.getContactsByDynamicName("honda city");
//                        if(lsContacts.size() > 0){
//
//                        }

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


//                        LinearLayout l1 = new LinearLayout(mContext);
//                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        lp1.weight = 5;
//                        l1.setLayoutParams(lp1);
//                        l1.addView(tv);

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

            Log.d(TAG, "onItemSelected: listHash: " + list.size());

            Toast.makeText(view.getContext(), "onItemSelected: " + spinnerNames.getSelectedItem(), Toast.LENGTH_SHORT).show();


            Spinner spinnerValues = new Spinner(view.getContext());
            spinnerValues.setId(list.size()+1);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(), R.layout.spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerValues.setAdapter(dataAdapter);

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
            relativeLayout.addView(spinnerValues, lpSpinner);


//            TextView tv = new TextView(view.getContext());
//            tv.setText(((String) spinnerNames.getSelectedItem()));
//
//            LinearLayout l1 = new LinearLayout(view.getContext());
//            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp1.weight = 5;
//            l1.setLayoutParams(lp1);
//            l1.addView(tv);

            l.removeAllViews();
            l.addView(l2);
            l.addView(relativeLayout);
            llFilterDynamicArea.removeView(l);
            llFilterDynamicArea.addView(l);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(parent.getContext(), "onNothingSelected", Toast.LENGTH_SHORT).show();
        }
    }
}
