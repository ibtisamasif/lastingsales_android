package com.example.muzafarimran.lastingsales.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.SalesAdapter;

/**
 * Created by ahmad on 19-Dec-16.
 */

public class SalesContactDeleteConfirmationDialogFragment extends DialogFragment {
    SalesAdapter salesAdapter;
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
                        getSalesAdapter().deleteAtPosition(position);
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

    public SalesAdapter getSalesAdapter() {
        return salesAdapter;
    }

    public void setSalesAdapter(SalesAdapter salesAdapter) {
        this.salesAdapter = salesAdapter;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
