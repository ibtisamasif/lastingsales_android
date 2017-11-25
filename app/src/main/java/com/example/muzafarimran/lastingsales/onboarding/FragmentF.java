package com.example.muzafarimran.lastingsales.onboarding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.muzafarimran.lastingsales.R;

public class FragmentF extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button bNext;

    public FragmentF() {
        // Required empty public constructor
    }

    public static FragmentF newInstance(String param1, String param2) {
        FragmentF fragment = new FragmentF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f, container, false);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        bNext = view.findViewById(R.id.bNext);
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnBoardingActivity) getActivity()).dataFromFragmentF(etFirstName.getText().toString(),
                        etLastName.getText().toString(),
                        etPhoneNumber.getText().toString(),
                        etPassword.getText().toString(),
                        etConfirmPassword.getText().toString());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
