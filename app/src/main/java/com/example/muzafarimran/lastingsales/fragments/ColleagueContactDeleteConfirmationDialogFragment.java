package com.example.muzafarimran.lastingsales.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.example.muzafarimran.lastingsales.adapters.ColleagueContactsAdapter;
import com.example.muzafarimran.lastingsales.adapters.SalesAdapter;

/**
 * Created by ahmad on 19-Dec-16.
 */

public class ColleagueContactDeleteConfirmationDialogFragment extends DialogFragment {
    ColleagueContactsAdapter colleagueContactsAdapter;
    int position;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.lasting_sales_logo)
                .setTitle("Confirm Delete")
                .setMessage("Do you want to delete this contact ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getColleagueContactsAdapter().deleteAtPosition(position);
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create()
                ;
    }

    public ColleagueContactsAdapter getColleagueContactsAdapter() {
        return colleagueContactsAdapter;
    }

    public void setColleagueContactsAdapter(ColleagueContactsAdapter colleagueContactsAdapter) {
        this.colleagueContactsAdapter = colleagueContactsAdapter;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
