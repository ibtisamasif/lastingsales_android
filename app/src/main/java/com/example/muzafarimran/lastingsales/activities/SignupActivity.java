package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ibtisam on 5/18/2017.
 */

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    ProgressDialog pdLoading;
    EditText etEmail, etPassword;
    String email, password;
    Button btSignup;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etConfimPassword;
    private EditText etCompany;
    private EditText etMobile;
    private String firstname;
    private String lastname;
    private String confirmpassword;
    private String company;
    private String mobile;
    private LinearLayout llLogin;
    private SessionManager sessionManager;
    private TextView tvPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        sessionManager = new SessionManager(getApplicationContext());
        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        //this method will be running on UI thread
        pdLoading.setMessage("Please Wait...");

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfimPassword = (EditText) findViewById(R.id.etConfimPassword);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etCompany = (EditText) findViewById(R.id.etCompany);
        tvPrivacyPolicy = (TextView) findViewById(R.id.tvPrivacyPolicy);
        llLogin = (LinearLayout) findViewById(R.id.llLogin);
        btSignup = (Button) findViewById(R.id.btSignup);
        etEmail.getBackground().clearColorFilter();
        etPassword.getBackground().clearColorFilter();

        // hardcoding for develoment speedup purposes
//        etFirstName.setText("ibti");
//        etLastName.setText("agent");
//        etEmail.setText("ibtiagent23@gmail.com");
//        etPassword.setText("111111");
//        etConfimPassword.setText("111111");
//        etMobile.setText("03014775234");
//        etCompany.setText("TheDesignerX23");

        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(MyURLs.PRIVACY_POLICY));
                startActivity(i);
            }
        });

        llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LogInActivity.class));
            }
        });
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFirstName.setError(null);
                etLastName.setError(null);
                etEmail.setError(null);
                etPassword.setError(null);
                etConfimPassword.setError(null);
                etCompany.setError(null);
                etMobile.setError(null);
                Boolean firstnameVarified = true, lastnameVarified = true, emailVarified = true, passwordVarified = true, confirmpasswordVarified = true, passwordMatchingVerified = true, companyVarified = true, mobileVarified = true;
                firstname = etFirstName.getText().toString();
                lastname = etLastName.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                confirmpassword = etConfimPassword.getText().toString();
                company = etCompany.getText().toString();
                mobile = etMobile.getText().toString();

                if (firstname.length() < 3) {
                    firstnameVarified = false;
                }
                if (lastname.length() < 3) {
                    lastnameVarified = false;
                }
                if (email.length() < 7) {
                    emailVarified = false;
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
                if (company.length() < 4) {
                    companyVarified = false;
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
                if (!emailVarified) {
                    etEmail.setError("Invalid Email!");
                }
                if (!passwordVarified) {
                    etPassword.setError("Invalid Password!");
                }
                if (!confirmpasswordVarified) {
                    etConfimPassword.setError("Invalid Confirm Password!");
                }
                if (!passwordMatchingVerified) {
                    etConfimPassword.setError("Mismatched!");
                }
                if (!mobileVarified) {
                    etMobile.setError("Invalid Mobile Number!");
                }
                if (!companyVarified) {
                    etCompany.setError("Invalid Company Name!");
                }
                if (firstnameVarified && lastnameVarified && emailVarified && passwordVarified && confirmpasswordVarified && passwordMatchingVerified && companyVarified && mobileVarified) {
                    pdLoading.show();
                    makeSignupRequest(SignupActivity.this, firstname, lastname, email, password, confirmpassword, company, mobile);
                }
            }
        });
    }

    private void makeSignupRequest(final Activity activity, final String firstname, final String lastname, final String number, final String password, final String confirmpassword, final String com, final String mobile) {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(activity, new HurlStack());
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.SIGNUP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
//                pdLoading.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        Log.d(TAG, "SignupActivityonResponse200: ");
                        JSONObject responseObject = jObj.getJSONObject("response");
                        String user_id = responseObject.getString("id");
                        String firstname = responseObject.getString("firstname");
                        String lastname = responseObject.getString("lastname");
                        String email = responseObject.getString("email");
                        String phone = responseObject.getString("phone");
                        String cell_number = responseObject.getString("cell_number");
                        String api_token = responseObject.getString("api_token");
                        String image = responseObject.getString("image");
                        String image_path = responseObject.getString("image_path");
                        String completeImagePath = MyURLs.IMAGE_URL + image_path;
                        sessionManager.setLoginToken(api_token);
                        Toast.makeText(activity, "Successfully Signup", Toast.LENGTH_SHORT).show();
                        makeCreateCompanyRequest(SignupActivity.this, com);
//                        activity.startActivity(new Intent(activity, CreateCompanyActivity.class));
//                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pdLoading != null && pdLoading.isShowing()) {
                    pdLoading.dismiss();
                }
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 412) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                                int responseCode = jObj.getInt("responseCode");
                                String response = jObj.getString("response");
                                Toast.makeText(activity, ""+response, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "Server Error.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Poor Internet Connectivity", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("email", email);
                params.put("password", password);
                params.put("confirm_password", confirmpassword);
                params.put("phone", mobile);
                params.put("cell_number", mobile);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void makeCreateCompanyRequest(final Activity activity, final String companyName) {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(activity, new HurlStack());
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_COMPANY_URL, new Response.Listener<String>() {
                @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                pdLoading.dismiss();
                try {
                    if (pdLoading != null && pdLoading.isShowing()) {
                        pdLoading.dismiss();
                    }
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");

                    if (responseCode == 200) {
                        sessionManager.setKeyInitCompanyCreated("yes");
                        sessionManager.setKeyInitAccountTypeSelected("individual");
                        Toast.makeText(activity, "Successfully Created Company", Toast.LENGTH_SHORT).show();
                        activity.startActivity(new Intent(activity, LogInActivity.class));
                        activity.finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pdLoading != null && pdLoading.isShowing()) {
                    pdLoading.dismiss();
                }
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 412) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                                int responseCode = jObj.getInt("responseCode");
                                String response = jObj.getString("response");
                                Toast.makeText(activity, ""+response, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "Server Error.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Poor Internet Connectivity", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", companyName);
                params.put("api_token", sessionManager.getLoginToken());
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }
}
