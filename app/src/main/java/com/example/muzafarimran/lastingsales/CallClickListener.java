package com.example.muzafarimran.lastingsales;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Class for click events on call icon.
 */
public class CallClickListener implements View.OnClickListener {

    //private ArrayList<Contact> contacts = null;
    private Context context;

    public CallClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        // TODO handle the click
        if (ContextCompat.checkSelfPermission(this.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        }
        String number = v.getTag().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        this.context.startActivity(intent);
    }
}