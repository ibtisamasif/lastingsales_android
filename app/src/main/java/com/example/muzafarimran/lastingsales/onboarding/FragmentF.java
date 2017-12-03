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


                etFirstName.setError(null);
                etLastName.setError(null);

                etPassword.setError(null);
                etConfirmPassword.setError(null);
                etPhoneNumber.setError(null);
                Boolean firstnameVarified = true, lastnameVarified = true, passwordVarified = true, confirmpasswordVarified = true, passwordMatchingVerified = true, mobileVarified = true;
                String firstname = etFirstName.getText().toString();
                String lastname = etLastName.getText().toString();
                String password = etPassword.getText().toString();
                String confirmpassword = etConfirmPassword.getText().toString();
                String mobile = etPhoneNumber.getText().toString();

                if (firstname.length() < 3) {
                    firstnameVarified = false;
                }
                if (lastname.length() < 3) {
                    lastnameVarified = false;
                }
                if (password.length() < 4) { //TODO password <-> confirm Password matching should be here
                    passwordVarified = false;
                }
                if (!password.equals(confirmpassword)) {
                    passwordMatchingVerified = false;
                }
                if (confirmpassword.length() < 4) {
                    confirmpasswordVarified = false;
                }
                if (mobile.length() < 11) {
                    mobileVarified = false;
                }
//                if (!PhoneNumberAndCallUtils.isValidPassword(password)) {
//                    passwordVarified = false;
//                }
                if (!firstnameVarified) {
                    etFirstName.setError("Invalid First Name!");
                }
                if (!lastnameVarified) {
                    etLastName.setError("Invalid Last Name!");
                }
                if (!passwordVarified) {
                    etPassword.setError("Invalid Password!");
                }
                if (!confirmpasswordVarified) {
                    etConfirmPassword.setError("Invalid Confirm Password!");
                }
                if (!passwordMatchingVerified) {
                    etPassword.setError("Mismatched!");
                    etConfirmPassword.setError("Mismatched!");
                }
                if (!mobileVarified) {
                    etPhoneNumber.setError("Invalid Mobile Number!");
                }
                if (firstnameVarified && lastnameVarified && passwordVarified && confirmpasswordVarified && passwordMatchingVerified && mobileVarified) {
                    ((OnBoardingActivity) getActivity()).dataFromFragmentF(
                            etFirstName.getText().toString(),
                            etLastName.getText().toString(),
                            etPhoneNumber.getText().toString(),
                            etPassword.getText().toString(),
                            etConfirmPassword.getText().toString());
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
