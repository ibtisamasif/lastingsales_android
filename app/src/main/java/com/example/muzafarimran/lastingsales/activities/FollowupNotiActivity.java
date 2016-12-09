package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class FollowupNotiActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.add_followup_activity);
        Toast.makeText(this, "Follow Up Activity", Toast.LENGTH_SHORT).show();
    }
}