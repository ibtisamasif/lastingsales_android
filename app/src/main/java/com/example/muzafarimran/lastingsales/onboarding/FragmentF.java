package com.example.muzafarimran.lastingsales.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.utils.MyDateTimeStamp;

public class FragmentF extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etPhoneNumber;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etEmail;
    private EditText etCompanyName;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_f, container, false);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etCompanyName = view.findViewById(R.id.etCompanyName);
        bNext = view.findViewById(R.id.bNext);
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                etFirstName.setError(null);
                etLastName.setError(null);
                etPhoneNumber.setError(null);
                etPassword.setError(null);
                etConfirmPassword.setError(null);
                etEmail.setError(null);
                etCompanyName.setError(null);

                Boolean firstnameVarified = true, lastnameVarified = true, passwordVarified = true, confirmpasswordVarified = true, passwordMatchingVerified = true, mobileVarified = true, emailVerified = true, companyVerified = true;

                String firstname = etFirstName.getText().toString();
                String lastname = etLastName.getText().toString();
                String password = etPassword.getText().toString();
                String confirmpassword = etConfirmPassword.getText().toString();
                String mobile = etPhoneNumber.getText().toString();
                String company = etCompanyName.getText().toString();
                String email = etEmail.getText().toString();

                if (firstname.length() < 3) {
                    firstnameVarified = false;
                }
                if (lastname.length() < 3) {
                    lastnameVarified = false;
                }
                if (password.length() < 4) {
                    passwordVarified = false;
                }
                if (!password.equals(confirmpassword)) {
                    passwordMatchingVerified = false;
                }
                if (confirmpassword.length() < 4) {
                    confirmpasswordVarified = false;
                }
                if (mobile.length() < 10) {
                    mobileVarified = false;
                }
                if (company.length() < 4) {
                    companyVerified = false;
                }
                if (email.length() < 7) {
                    emailVerified = false;
                }
                if (!MyDateTimeStamp.isValidEmail(email)) {
                    emailVerified = false;
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
                    etPassword.setError("must be greater than 4!");
                }
                if (!confirmpasswordVarified) {
                    etConfirmPassword.setError("must be greater than 4!");
                }
                if (!passwordMatchingVerified) {
                    etPassword.setError("Mismatched!");
                    etConfirmPassword.setError("Mismatched!");
                }
                if (!mobileVarified) {
                    etPhoneNumber.setError("Invalid phone Number!");
                }
                if (!companyVerified) {
                    etCompanyName.setError("Invalid Company minimum 4 characters expected!");
                }
                if (!emailVerified) {
                    etEmail.setError("Invalid Email!");
                }
                if (firstnameVarified && lastnameVarified && passwordVarified && confirmpasswordVarified && passwordMatchingVerified && mobileVarified && emailVerified && companyVerified) {
                    ((OnBoardingActivity) getActivity()).dataFromFragmentF(
                            etFirstName.getText().toString(),
                            etLastName.getText().toString(),
                            etPhoneNumber.getText().toString(),
                            etPassword.getText().toString(),
                            etConfirmPassword.getText().toString(),
                            etEmail.getText().toString(),
                            etCompanyName.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "Something goes wrong. Please recheck entered data!", Toast.LENGTH_SHORT).show();
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
