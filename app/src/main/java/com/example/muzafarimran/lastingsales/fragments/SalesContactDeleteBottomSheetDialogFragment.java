package com.example.muzafarimran.lastingsales.fragments;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.SalesAdapter;

/**
 * Created by ahmad on 15-Dec-16.
 */

public class SalesContactDeleteBottomSheetDialogFragment extends BottomSheetDialogFragment {

    int position;
    SalesAdapter salesAdapter;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }
        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet, null);

        LinearLayout llDelete = (LinearLayout) contentView.findViewById(R.id.deleteLayoutBottomSheet);
        llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesContactDeleteConfirmationDialogFragment salesContactDeleteConfirmationDialogFragment;
                salesContactDeleteConfirmationDialogFragment = new SalesContactDeleteConfirmationDialogFragment();
                salesContactDeleteConfirmationDialogFragment.setPosition(position);
                salesContactDeleteConfirmationDialogFragment.setSalesAdapter(salesAdapter);
                salesContactDeleteConfirmationDialogFragment.show(getFragmentManager(),"Delete Confirm");
                dismiss();

            }
        });
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    public SalesAdapter getSalesAdapter() {
        return salesAdapter;
    }

    public void setSalesAdapter(SalesAdapter salesAdapter) {
        this.salesAdapter = salesAdapter;
    }

    public BottomSheetBehavior.BottomSheetCallback getmBottomSheetBehaviorCallback() {
        return mBottomSheetBehaviorCallback;
    }

    public void setmBottomSheetBehaviorCallback(BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback) {
        this.mBottomSheetBehaviorCallback = mBottomSheetBehaviorCallback;
    }
}