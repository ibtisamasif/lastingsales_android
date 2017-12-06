package com.example.muzafarimran.lastingsales.onboarding;

import android.app.MediaRouteButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;

public class FragmentG extends Fragment {
    private static final String TAG = "onBoarding";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isUserSuccess = false;
    private boolean isCompanySuccess = false;
    private ProgressBar mProgress;
    private Button bTryAgain;
    private TextView tvErrorMsg;

    public FragmentG() {
        // Required empty public constructor
    }

    public static FragmentG newInstance(String param1, String param2) {
        FragmentG fragment = new FragmentG();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_g, container, false);
        mProgress = view.findViewById(R.id.progressBar);
        tvErrorMsg = view.findViewById(R.id.tvErrorMsg);
        bTryAgain = view.findViewById(R.id.bTryAgain);
        bTryAgain.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);
        bTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnBoardingActivity) getActivity()).dataFromFragmentG();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onUserSuccess() {
        mProgress.setProgress(50);
        isUserSuccess = true;
        validateAndNext();
        tvErrorMsg.setText("User registered successfully Registering company now");
    }

    public void onUserError(String error) {
        Log.d(TAG, "onCompanyError: " + error);
        bTryAgain.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(error + " while registering user");
//        Toast.makeText(getActivity(), error + " error occurred while registering user", Toast.LENGTH_SHORT).show();
    }

    public void onCompanySuccess() {
        mProgress.setProgress(100);
        isCompanySuccess = true;
        validateAndNext();
        tvErrorMsg.setText("Company registered successfully");
//        Toast.makeText(getActivity(), "Company registered successfully", Toast.LENGTH_SHORT).show();
    }

    public void onCompanyError(String error) {
        Log.d(TAG, "onCompanyError: " + error);
        bTryAgain.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(error + " while registering company");
    }

    private void validateAndNext() {
        if (isUserSuccess && isCompanySuccess){
            ((OnBoardingActivity)getActivity()).moveToFragment(6);
        }
    }
}