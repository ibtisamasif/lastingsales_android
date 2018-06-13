//package com.example.muzafarimran.lastingsales.NavigationBottomFragments;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.muzafarimran.lastingsales.R;
//import com.example.muzafarimran.lastingsales.adapters.MyLeadPagerAdapter;
//
//public class BlankFragment3 extends Fragment {
//    public static final String TAG = "BlankFragment3";
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;
//
//    public BlankFragment3() {
//    }
//
//    public static BlankFragment3 newInstance(String param1, String param2) {
//        BlankFragment3 fragment = new BlankFragment3();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_blank3, container, false);
//
//        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
//        mViewPager.setAdapter(new MyLeadPagerAdapter(getChildFragmentManager()));
//
//        // Give the TabLayout the ViewPager
//        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setupWithViewPager(mViewPager);
//        mViewPager.setCurrentItem(1);
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()) {
//                    case 0:
////                        tab.setIcon(R.drawable.call_icon);
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                switch (tab.getPosition()) {
//                    case 0:
////                        tab.setIcon(R.drawable.call_icon);
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
//
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        Log.d(TAG, "onActivityCreated: ");
//    }
//}