package com.example.muzafarimran.lastingsales.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.muzafarimran.lastingsales.R;

public class FragmentG extends Fragment {
    private static final String TAG = "onBoarding";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isUserAndCompanySuccess = false;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        isUserAndCompanySuccess = true;
        validateAndNext();
        tvErrorMsg.setText("User registered successfully");
    }

    public void onUserAndCompanyError(String error, int responseCode) {

        if (responseCode == 222) { // Invalid email responseCode:222  move to email correction screen FragF

            ((OnBoardingActivity) getActivity()).moveToFragment(3);

        } else if (responseCode == 190) { // Email is already registered with us ResponseCode:190  move to email changing screen FragF

            ((OnBoardingActivity) getActivity()).moveToFragment(3);

        } else if (responseCode == 220) {  //Pass must be greater than 4 char ResponseCode:220 move to fragF

            ((OnBoardingActivity) getActivity()).moveToFragment(3);

        }

        Log.d(TAG, "onUserError: " + error);
        bTryAgain.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(error + " while registering");
//        Toast.makeText(getActivity(), error + " error occurred while registering user", Toast.LENGTH_SHORT).show();
    }

    public void onUserAndCompanyError(String error) {
        Log.d(TAG, "onUserError: " + error);
        bTryAgain.setVisibility(View.VISIBLE);
        tvErrorMsg.setText(error + " while registering");
//        Toast.makeText(getActivity(), error + " error occurred while registering user", Toast.LENGTH_SHORT).show();
    }

    private void validateAndNext() {
        if (isUserAndCompanySuccess) {
            ((OnBoardingActivity) getActivity()).moveToFragment(5);
        }
    }
}