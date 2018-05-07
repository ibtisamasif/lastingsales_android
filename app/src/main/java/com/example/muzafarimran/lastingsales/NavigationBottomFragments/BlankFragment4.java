package com.example.muzafarimran.lastingsales.NavigationBottomFragments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.adapters.DealFragmentPagerAdapter;
import com.example.muzafarimran.lastingsales.providers.models.LSStage;
import com.example.muzafarimran.lastingsales.providers.models.LSWorkflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlankFragment4 extends Fragment implements ViewPager.OnPageChangeListener {
    public static final String TAG = "BlankFragment4";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout indicator;
    private int mDotCount;
    private LinearLayout[] mDots;
    private ViewPager viewPager;
    private List<LSStage> listItem = new ArrayList<>();
    private DealFragmentPagerAdapter fragmentAdapter;
    private TabLayout tabLayout;

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
        tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        setData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
//        fragmentAdapter.notifyDataSetChanged(); // NPE
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private void setData() {
        LSWorkflow defaultWorkFlow = LSWorkflow.getDefaultWorkflow();
        if (defaultWorkFlow != null) {
            Collection<LSStage> lsSteps = LSStage.getAllStagesInPositionSequenceByWorkflowServerId(defaultWorkFlow.getServerId());
            if (lsSteps != null) {
                listItem.addAll(lsSteps);
                fragmentAdapter = new DealFragmentPagerAdapter(getActivity(), getFragmentManager(), listItem);
                viewPager.setAdapter(fragmentAdapter);
                viewPager.setCurrentItem(0);
                viewPager.setOnPageChangeListener(this);
                setUiPageViewController();
            }
        }
    }

    private void setUiPageViewController() {
        mDotCount = fragmentAdapter.getCount();
        mDots = new LinearLayout[mDotCount];

        for (int i = 0; i < mDotCount; i++) {
            mDots[i] = new LinearLayout(getActivity());
            mDots[i].setBackgroundResource(R.drawable.nonselected_item);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            indicator.addView(mDots[i], params);
            mDots[0].setBackgroundResource(R.drawable.selected_item);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getActivity() != null) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                LSStage lsStage = fragmentAdapter.getStage();
                if (lsStage != null) {
                    switch (lsStage.getPosition()) {
                        case "800":
//                            android.app.ActionBar bar = getActivity().getActionBar();
//                            bar.setBackgroundDrawable(new ColorDrawable(Color.GREEN));
//                            ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.md_light_blue_600));
//                            getActivity().getActionBar().setBackgroundDrawable(colorDrawable);
//                            window.setStatusBarColor(getResources().getColor(R.color.md_light_blue_800));
//                            tabLayout.setBackgroundColor(getResources().getColor(R.color.md_light_blue_600));
                            break;
                        case "999":
//                            ColorDrawable colorDrawable1 = new ColorDrawable(getResources().getColor(R.color.md_red_600));
//                            getActivity().getActionBar().setBackgroundDrawable(colorDrawable1);
//                            window.setStatusBarColor(getResources().getColor(R.color.md_red_800));
//                            tabLayout.setBackgroundColor(getResources().getColor(R.color.md_red_600));
                            break;
                        default:
//                            ColorDrawable colorDrawable2 = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
//                            getActivity().getActionBar().setBackgroundDrawable(colorDrawable2);
//                            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
//                            tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mDotCount; i++) {
            mDots[i].setBackgroundResource(R.drawable.nonselected_item);
        }
        mDots[position].setBackgroundResource(R.drawable.selected_item);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}