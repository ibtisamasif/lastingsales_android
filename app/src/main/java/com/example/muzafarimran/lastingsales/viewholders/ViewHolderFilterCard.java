package com.example.muzafarimran.lastingsales.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.providers.models.LSDynamicColumns;

import java.util.List;

/**
 * Created by ibtisam on 11/2/2017.
 */

public class ViewHolderFilterCard extends RecyclerView.ViewHolder {
    private static final String TAG = "ViewHolderFilterCard";
    private final LinearLayout ll;

    private Button bFilter;

    public ViewHolderFilterCard(View v) {
        super(v);
        bFilter = v.findViewById(R.id.bFilter);
        ll = (LinearLayout) v.findViewById(R.id.contactDetailsDropDownLayoutinner);
    }

    public void bind(Object item, int position, Context mContext) {

        bFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<LSDynamicColumns> allColumns = LSDynamicColumns.getAllColumns();
                if (allColumns != null) {
                    Log.d(TAG, "onClick: allColumns Size: " + allColumns.size());
                    Log.d(TAG, "onClick: allColumns : " + allColumns.toString());
                    for (LSDynamicColumns oneColumn : allColumns) {
                        Log.d(TAG, "onClick: oneColumn: " + oneColumn.toString());

//                        LSContact lsContact = LSContact.getContactByDynamicValue("honda");
//                        if(lsContact)



//                        LinearLayout l = new LinearLayout(mContext);
//                        l.setFocusable(true);
//                        l.setFocusableInTouchMode(true);
//                        l.setOrientation(LinearLayout.HORIZONTAL);
//                        l.setWeightSum(10);
//
//                        LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        layoutParamsRow.setMargins(0, 5, 0, 5);
//                        l.setLayoutParams(layoutParamsRow);
//
//
//                        Log.d(TAG, "dynamicColumns: matched text");
//                        TextView tv = new TextView(mContext);
////                        tv.setText(allColumns.get(i).getName());
//                        tv.setPadding(15, 15, 15, 15);
//                        final EditText et = new EditText(mContext);
////                et.setTextColor(getResources().getColor(R.color.black));
//                        et.setBackgroundResource(R.drawable.dynamic_border);
//                        et.setPadding(15, 15, 15, 15);
////                        et.setText(allColumns.get(i).getDefaultValueOption());
//                        et.setMinimumWidth(400);
////                        String id = allColumns.get(i).getServerId();
////                        et.setTag(id);
//                        et.addTextChangedListener(new TextWatcher() {
//                            @Override
//                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                            }
//
//                            @Override
//                            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                            }
//
//                            @Override
//                            public void afterTextChanged(Editable s) {
//                            }
//                        });
//                        LinearLayout l1 = new LinearLayout(mContext);
//                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        lp1.weight = 5;
//                        l1.setLayoutParams(lp1);
//                        l1.addView(tv);
//
//                        LinearLayout l2 = new LinearLayout(mContext);
//                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        lp2.weight = 5;
//                        l2.setLayoutParams(lp2);
//                        l2.addView(et);
//
//                        l.addView(l1);
//                        l.addView(l2);
//                        ll.addView(l);


                    }
                }
            }
        });
    }
}
