package com.example.muzafarimran.lastingsales.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;

public class FragmentG extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isUserSuccess = false;
    private boolean isCompanySuccess = false;
    private ProgressBar mProgress;

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
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setProgress(0);
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
        Toast.makeText(getActivity(), "User registered successfully", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Registering company now", Toast.LENGTH_SHORT).show();
    }

    public void onUserError(String error) {
        Toast.makeText(getActivity(), error + " error occured while registering user", Toast.LENGTH_SHORT).show();
    }

    public void onCompanySuccess() {
        mProgress.setProgress(100);
        isCompanySuccess = true;
        validateAndNext();
        Toast.makeText(getActivity(), "Company registered successfully", Toast.LENGTH_SHORT).show();
    }

    public void onCompanyError(String error) {
        Toast.makeText(getActivity(), error + " error occured while registering company", Toast.LENGTH_SHORT).show();
    }

    private void validateAndNext() {
        if (isUserSuccess && isCompanySuccess){
            ((OnBoardingActivity)getActivity()).moveToFragment(6);
        }
    }
}