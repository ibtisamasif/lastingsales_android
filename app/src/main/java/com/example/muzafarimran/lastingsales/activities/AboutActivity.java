package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;

/**
 * Created by ibtisam on 4/20/2017.
 */

public class AboutActivity extends AppCompatActivity {
    private TextView textViewNumber;
    private TextView textViewVersion;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.ic_notification_small);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("About");
        textViewNumber = (TextView) findViewById(R.id.textViewNumber);
        textViewVersion = (TextView) findViewById(R.id.textViewVersion);
        textViewNumber.setText("03111308308");
        try {
            String currentVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            textViewVersion.setText(currentVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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
