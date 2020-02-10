package com.example.muzafarimran.lastingsales.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.activities.LogInActivity;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;
import com.example.muzafarimran.lastingsales.utils.PhoneNumberAndCallUtils;

public class FragmentE extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText etCompanyName;
    private EditText etEmail;
    private Button bNext;
    private String email;
    private String company;

    public FragmentE() {
        // Required empty public constructor
    }

    public static FragmentE newInstance(String param1, String param2) {
        FragmentE fragment = new FragmentE();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_e, container, false);
        etCompanyName = view.findViewById(R.id.etFirstName);
        etEmail = view.findViewById(R.id.etEmail);
        bNext = view.findViewById(R.id.bNext);
        bNext.setOnClickListener(view1 -> {

            etCompanyName.setError(null);
            etEmail.setError(null);
            Boolean emailVarified = true, companyVarified = true;
            company = etCompanyName.getText().toString();
            email = etEmail.getText().toString();

            if (company.length() < 4) {
                companyVarified = false;
            }
            if (email.length() < 7 ) {
                emailVarified = false;
            }
            if(!MyDateTimeStamp.isValidEmail(email)){
                emailVarified = false;
            }
            if (!companyVarified) {
                etCompanyName.setError("Invalid Company minimum 4 characters expected!");
            }
            if (!emailVarified) {
                etEmail.setError("Invalid Email!");
            }
            if (emailVarified && companyVarified) {
                ((OnBoardingActivity) getActivity()).dataFromFragmentE(etCompanyName.getText().toString(), etEmail.getText().toString());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
