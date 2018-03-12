package com.example.muzafarimran.lastingsales.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ibtisam on 4/20/2017.
 */

public class DealsDetailActivity extends AppCompatActivity {
    private static final String TAG = "DealsDetailActivity";
    private final Context mContext = this;
    private LinearLayout ll;
    private Spinner deal_status_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.ic_notification_small);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("Deals");

        TextView tvDealHeading = (TextView) findViewById(R.id.tvDealHeading);
        tvDealHeading.setText("Deal Number 1");
        TextView contactName = (TextView) findViewById(R.id.contactName);
        contactName.setText("Bilal");

        Button bSave = (Button) findViewById(R.id.dealsDetailsSaveButton);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DealsDetailActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });
        addItemsOnSpinnerLeadStatus();
        dynamicColumns();
    }

    public void addItemsOnSpinnerLeadStatus() {
        deal_status_spinner = (Spinner) findViewById(R.id.deal_status_spinner);
        List<String> list = new ArrayList<String>();
        list.add("Deal InProgress");
        list.add("Deal Won");
        list.add("Deal Lost");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deal_status_spinner.setAdapter(dataAdapter);
    }

    private void dynamicColumns() {
        ll = (LinearLayout) findViewById(R.id.llDealsHistoryContainer);

        Display display = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth() / 2;
        Log.d(TAG, "Display SIZE: " + display);

        for (int i = 0; i < 3; i++) {


            LinearLayout l = new LinearLayout(mContext);
            l.setFocusable(true);
            l.setFocusableInTouchMode(true);
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setWeightSum(10);

            LinearLayout.LayoutParams layoutParamsRow = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsRow.setMargins(0, 5, 0, 5);
            l.setLayoutParams(layoutParamsRow);

            TextView tvKey = new TextView(mContext);
            tvKey.setText("ABC");
            tvKey.setPadding(15, 15, 15, 15);

            TextView tvValue = new TextView(mContext);
//                et.setTextColor(getResources().getColor(R.color.black));
            tvValue.setBackgroundResource(R.drawable.dynamic_border);
            tvValue.setPadding(15, 15, 15, 15);
            tvValue.setText("DEF");
            tvValue.setMinimumWidth(400);

            LinearLayout l1 = new LinearLayout(mContext);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp1.weight = 5;
            l1.setLayoutParams(lp1);
            l1.addView(tvKey);

            LinearLayout l2 = new LinearLayout(mContext);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp2.weight = 5;
            l2.setLayoutParams(lp2);
            l2.addView(tvValue);

            l.addView(l1);
            l.addView(l2);
            ll.addView(l);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        deal_status_spinner.setSelection(1, false);
        deal_status_spinner.post(new Runnable() {
            public void run() {
                deal_status_spinner.setOnItemSelectedListener(new CustomSpinnerStatusOnItemSelectedListener());
            }
        });
    }

    private class CustomSpinnerStatusOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch (pos) {
                case 0:
                    Toast.makeText(parent.getContext(), "Status Changed to InProgress", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(parent.getContext(), "Status Changed to Won", Toast.LENGTH_SHORT).show();
                    ;
                    break;
                case 2:
                    Toast.makeText(parent.getContext(), "Status Changed to Lost", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
