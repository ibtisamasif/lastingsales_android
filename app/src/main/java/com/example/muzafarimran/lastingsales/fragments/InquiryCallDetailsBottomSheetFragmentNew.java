package com.example.muzafarimran.lastingsales.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.carditems.ConnectionItem;
import com.example.muzafarimran.lastingsales.carditems.ContactHeaderBottomsheetItem;
import com.example.muzafarimran.lastingsales.carditems.SeparatorItem;
import com.example.muzafarimran.lastingsales.providers.models.LSCall;
import com.example.muzafarimran.lastingsales.providers.models.LSContact;
import com.example.muzafarimran.lastingsales.providers.models.LSContactProfile;
import com.example.muzafarimran.lastingsales.recycleradapter.MyRecyclerViewAdapter;
import com.google.firebase.crash.FirebaseCrash;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InquiryCallDetailsBottomSheetFragmentNew extends BottomSheetDialogFragment {

    private static final String TAG = "InquiryCallDetailsBottomSheetFragmentNew";
    public static final String CONTACT_NUM = "contact_num";
    private List<Object> list = new ArrayList<Object>();
    private RecyclerView mRecyclerView;


    private MyRecyclerViewAdapter adapter;
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

    public static InquiryCallDetailsBottomSheetFragmentNew newInstance(String contact_num, int page) {
        InquiryCallDetailsBottomSheetFragmentNew fragmentFirst = new InquiryCallDetailsBottomSheetFragmentNew();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString(CONTACT_NUM, contact_num);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.fragment_contact_call_details_bottom_sheet_new, null);
        dialog.setContentView(view);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            ((BottomSheetBehavior) behavior).setPeekHeight(600);
        }
        String contactNum = getArguments().getString(CONTACT_NUM);
        LSContact selectedContact = LSContact.getContactFromNumber(contactNum);
        if (selectedContact != null) {

            ContactHeaderBottomsheetItem contactHeaderBottomsheetItem = new ContactHeaderBottomsheetItem();
            contactHeaderBottomsheetItem.lsContact = selectedContact;
            contactHeaderBottomsheetItem.place = "inquiry";
            list.add(contactHeaderBottomsheetItem);

            LSContactProfile lsContactProfile = LSContactProfile.getProfileFromNumber(selectedContact.getPhoneOne());
            if (lsContactProfile != null) {
                SeparatorItem separatorItemlsContactProfile = new SeparatorItem();
                separatorItemlsContactProfile.text = "Profile";
                list.add(separatorItemlsContactProfile);
                list.add(lsContactProfile);
            }

            SeparatorItem separatorItemConnections = new SeparatorItem();
            separatorItemConnections.text = "Connections";
            list.add(separatorItemConnections);

            ConnectionItem connectionItem = new ConnectionItem();
            connectionItem.id = 1;
            connectionItem.lsContact = selectedContact;
            list.add(connectionItem);

            Collection<LSCall> allCallsOfThisContact = (Collection<LSCall>) Select.from(LSCall.class).where(Condition.prop("contact_number").eq(selectedContact.getPhoneOne())).orderBy("begin_time DESC").list();
            if (allCallsOfThisContact != null) {
                SeparatorItem separatorItemallCallsOfThisContact = new SeparatorItem();
                separatorItemallCallsOfThisContact.text = "Call History";
                list.add(separatorItemallCallsOfThisContact);
                list.addAll(allCallsOfThisContact);
            }

            adapter = new MyRecyclerViewAdapter(getActivity(), list);
            mRecyclerView = view.findViewById(R.id.mRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setNestedScrollingEnabled(false);
            NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.bottom_sheet);
            nestedScrollView.setScrollY(0);
        } else {
            FirebaseCrash.logcat(Log.ERROR, TAG, "Exception caught");
            FirebaseCrash.report(new Exception("Contact of inquiry is null "));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_action_edit:

                break;
            case android.R.id.home:
//                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}