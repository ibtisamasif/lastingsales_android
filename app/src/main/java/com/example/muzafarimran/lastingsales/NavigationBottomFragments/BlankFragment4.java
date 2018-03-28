package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.deals.FragmentAdapter;
import com.example.muzafarimran.lastingsales.deals.LSDynamicDealPipeline;

import java.util.ArrayList;
import java.util.List;

public class BlankFragment4 extends Fragment  implements ViewPager.OnPageChangeListener {
    public static final String TAG = "BlankFragment4";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    private LinearLayout indicator;
    private int mDotCount;
    private LinearLayout[] mDots;
    private ViewPager viewPager;
    private List<LSDynamicDealPipeline> listItem = new ArrayList<>();
    private FragmentAdapter fragmentAdapter;

    public BlankFragment4() {
    }

    public static BlankFragment4 newInstance(String param1, String param2) {
        Log.d(TAG, "newInstance: ");
        BlankFragment4 fragment = new BlankFragment4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_blank4, container, false);
        indicator = (LinearLayout) view.findViewById(R.id.indicators);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager_itemList);


        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
//                        floatingActionButton.hide();
//                        tab.setIcon(R.drawable.menu_icon_details_selected);
                        break;
                    case 1:
//                        floatingActionButton.show();
//                        tab.setIcon(R.drawable.menu_icon_phone_selected);
                        break;
                    case 2:
//                        floatingActionButton.hide();
//                        tab.setIcon(R.drawable.menu_icon_contact_selected);
                        break;
                    case 3:
//                        floatingActionButton.hide();
//                        tab.setIcon(R.drawable.add_contact_notes_field_icon);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
//                        tab.setIcon(R.drawable.menu_icon_details);
                        break;
                    case 1:
//                        tab.setIcon(R.drawable.menu_icon_phone);
                        break;
                    case 2:
//                        tab.setIcon(R.drawable.menu_icon_contact);
                        break;
                    case 3:
//                        tab.setIcon(R.drawable.add_contact_notes_field_icon_unselected);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setData();
        return view;
    }

    private void setData(){
        LSDynamicDealPipeline LSDynamicDealPipeline1 = new LSDynamicDealPipeline();
        LSDynamicDealPipeline1.setName("Prospect");
        LSDynamicDealPipeline1.save();

        LSDynamicDealPipeline LSDynamicDealPipeline2 = new LSDynamicDealPipeline();
        LSDynamicDealPipeline2.setName("Negotiation");
        LSDynamicDealPipeline2.save();

        LSDynamicDealPipeline LSDynamicDealPipeline3 = new LSDynamicDealPipeline();
        LSDynamicDealPipeline3.setName("OnBoarding");
        LSDynamicDealPipeline3.save();

        LSDynamicDealPipeline LSDynamicDealPipeline4 = new LSDynamicDealPipeline();
        LSDynamicDealPipeline4.setName("Payment");
        LSDynamicDealPipeline4.save();

        LSDynamicDealPipeline LSDynamicDealPipeline5 = new LSDynamicDealPipeline();
        LSDynamicDealPipeline5.setName("Returning customer");
        LSDynamicDealPipeline5.save();

        LSDynamicDealPipeline LSDynamicDealPipeline6 = new LSDynamicDealPipeline();
        LSDynamicDealPipeline6.setName("workflow 6");
        LSDynamicDealPipeline6.save();

        LSDynamicDealPipeline LSDynamicDealPipeline7 = new LSDynamicDealPipeline();
        LSDynamicDealPipeline7.setName("workflow 7");
        LSDynamicDealPipeline7.save();

        listItem.add(LSDynamicDealPipeline1);
        listItem.add(LSDynamicDealPipeline2);
        listItem.add(LSDynamicDealPipeline3);
        listItem.add(LSDynamicDealPipeline4);
        listItem.add(LSDynamicDealPipeline5);
        listItem.add(LSDynamicDealPipeline6);
        listItem.add(LSDynamicDealPipeline7);

        fragmentAdapter = new FragmentAdapter(getActivity(), getFragmentManager(), listItem);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(this);
        setUiPageViewController();

    }

    private void setUiPageViewController(){
        mDotCount = fragmentAdapter.getCount();
        mDots = new LinearLayout[mDotCount];

        for(int i=0; i<mDotCount; i++){
            mDots[i] = new LinearLayout(getActivity());
            mDots[i].setBackgroundResource(R.drawable.nonselected_item);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4,0,4,0);
            indicator.addView(mDots[i],params);
            mDots[0].setBackgroundResource(R.drawable.selected_item);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i=0; i<mDotCount; i++){
            mDots[i].setBackgroundResource(R.drawable.nonselected_item);
        }
        mDots[position].setBackgroundResource(R.drawable.selected_item);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

}