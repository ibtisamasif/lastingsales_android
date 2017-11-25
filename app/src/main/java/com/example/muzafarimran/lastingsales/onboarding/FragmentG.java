package com.example.muzafarimran.lastingsales.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muzafarimran.lastingsales.R;

public class FragmentG extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean isUserSuccess = false;
    private boolean isCompanySuccess = false;

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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onUserSuccess() {
        isUserSuccess = true;
        validateAndNext();
    }

    public void onUserError(String error) {

    }

    public void onCompanySuccess() {
        isCompanySuccess = true;
        validateAndNext();
    }

    public void onCompanyError(String error) {

    }

    private void validateAndNext() {
        if (isUserSuccess && isCompanySuccess){
            ((OnBoardingActivity)getActivity()).moveToFragment(6);
        }
    }
}