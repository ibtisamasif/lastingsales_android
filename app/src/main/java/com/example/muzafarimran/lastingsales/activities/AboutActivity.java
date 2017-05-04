package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;

/**
 * Created by ibtisam on 4/20/2017.
 */

public class AboutActivity extends Activity {
    private TextView textViewNumber;
    private TextView textViewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        textViewNumber = (TextView) findViewById(R.id.textViewNumber);
        textViewNumber.setText("03111308308");
        textViewVersion = (TextView) findViewById(R.id.textViewVersion);
        try {
            String currentVersionCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            textViewVersion.setText(currentVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
