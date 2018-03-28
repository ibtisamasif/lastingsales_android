package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.fragments.ContactCallDetailsBottomSheetFragment;
import com.example.muzafarimran.lastingsales.fragments.InquiryCallDetailsBottomSheetFragment;
import com.example.muzafarimran.lastingsales.listeners.CloseContactBottomSheetEvent;
import com.example.muzafarimran.lastingsales.listeners.CloseInquiryBottomSheetEvent;
import com.google.firebase.crash.FirebaseCrash;

public class BlankFragment2 extends Fragment implements CloseContactBottomSheetEvent, CloseInquiryBottomSheetEvent{
    public static final String TAG = "BlankFragment2";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    private static InquiryCallDetailsBottomSheetFragment inquiryCallDetailsBottomSheetFragment;
    private static ContactCallDetailsBottomSheetFragment contactCallDetailsBottomSheetFragment;
    public static Activity activity;
    private static boolean sheetShowing = false;

    public BlankFragment2() {
    }

    public static BlankFragment2 newInstance(String param1, String param2) {
        BlankFragment2 fragment = new BlankFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank2, container, false);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyUnlabeledPagerAdapter(getChildFragmentManager()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
//                        tab.setIcon(R.drawable.call_icon);
                        break;
                    case 1:
//                        tab.setIcon(R.drawable.call_icon_incoming_ind);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
//                        tab.setIcon(R.drawable.call_icon);
                        break;
                    case 1:
//                        tab.setIcon(R.drawable.call_icon_out_going_ind);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    public void openContactBottomSheetCallback(Long contact_id) {
        if (!isAdded()) return;
        contactCallDetailsBottomSheetFragment = ContactCallDetailsBottomSheetFragment.newInstance(contact_id, 0);
        FragmentManager fragmentManager = getChildFragmentManager();
        contactCallDetailsBottomSheetFragment.show(fragmentManager, "tag");
        sheetShowing = true;
    }

    @Override
    public void closeContactBottomSheetCallback() {
        if (sheetShowing) {
            if (contactCallDetailsBottomSheetFragment != null) {
                Log.d(TAG, "closeContactBottomSheetCallback: is NOT NULL");
                try {
                    if (Build.VERSION.SDK_INT > 21) {
                        contactCallDetailsBottomSheetFragment.dismiss(); //UncaughtException: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                    } else {
                        contactCallDetailsBottomSheetFragment.dismissAllowingStateLoss();
                    }
                } catch (IllegalStateException ignored) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "IllegalStateException caught");
                    FirebaseCrash.report(new Exception("closeContactBottomSheetCallback dismiss() called after onSaveInstanceState"));
                }
            } else {
                Log.d(TAG, "closeContactBottomSheetCallback: is NULL");
            }
        }
    }

    public void openInquiryBottomSheetCallback(String number) {
        if (!isAdded()) return;
        inquiryCallDetailsBottomSheetFragment = InquiryCallDetailsBottomSheetFragment.newInstance(number, 0);
        FragmentManager fragmentManager = getChildFragmentManager();
        inquiryCallDetailsBottomSheetFragment.show(fragmentManager, "tag");
        sheetShowing = true;
    }

    @Override
    public void closeInquiryBottomSheetCallback() {
        if (sheetShowing) {
            if (inquiryCallDetailsBottomSheetFragment != null) {
                Log.d(TAG, "inquiryCallDetailsBottomSheetFragment: is NOT NULL");
                try {
                    if (Build.VERSION.SDK_INT > 21) {
                        inquiryCallDetailsBottomSheetFragment.dismiss(); //UncaughtException: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                    } else {
                        inquiryCallDetailsBottomSheetFragment.dismissAllowingStateLoss();
                    }
                } catch (IllegalStateException ignored) {
                    FirebaseCrash.logcat(Log.ERROR, TAG, "IllegalStateException caught");
                    FirebaseCrash.report(new Exception("closeInquiryBottomSheetCallback dismiss() called after onSaveInstanceState"));
                }
            } else {
                Log.d(TAG, "inquiryCallDetailsBottomSheetFragment: is NULL");
            }
        }
    }

}