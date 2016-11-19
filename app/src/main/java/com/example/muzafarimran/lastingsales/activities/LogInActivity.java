package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.Utils.PhoneNumberAndCallUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {


    public static final String TAG = "LogInActivity";
    ProgressDialog pdLoading;
    RequestQueue requestQueue;
    TextView tvNumber, tvPassword;

    String number, password;
    Button loginButton;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.isUserSignedIn()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");

        //this method will be running on UI thread
        pdLoading.setMessage("Please Wait...");

        loginButton = (Button) findViewById(R.id.loginButtonLoginScreen);
        tvNumber = (TextView) findViewById(R.id.numberLoginScreen);
        tvPassword = (TextView) findViewById(R.id.passwordLoginScreen);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                tvNumber.setError(null);
                tvPassword.setError(null);

                Boolean numberVarified = true, passwordVarified = true;

                number = tvNumber.getText().toString();
                password = tvPassword.getText().toString();

                if (number.length() < 7) {
                    numberVarified = false;
                }
                String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);

                if (intlNumber != null) {


                    if (intlNumber.length() < 15) {
                        numberVarified = false;
                    }
                } else {
                    numberVarified = false;
                }


                if (password.length() < 8) {
                    passwordVarified = false;
                }
                if (!PhoneNumberAndCallUtils.isValidPassword(password)) {

                    passwordVarified = false;

                }


                if (!numberVarified) {
                    tvNumber.setError("Invalid Number!");
                }

                if (!passwordVarified) {
                    tvPassword.setError("Invalid Password!");
                }


                if (numberVarified && passwordVarified) {

                    pdLoading.show();

                    makeLoginRequest(LogInActivity.this, number, password);

                }


            }
        });


    }




    /*
    private void makeRequest() {
        Response.Listener responseListener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (!isFinishing()) {
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdLoading.dismiss();
                if (CommunicationManager.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Error Connecting to server!!!", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(getApplicationContext(), "Error Loading Data!!!", Toast.LENGTH_SHORT).show();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.GET, link, responseListener, errorListener);
        requestQueue = VolleyRequestQueue.getQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    */


    public void makeLoginRequest(final Activity activity, final String number, final String password) {

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest sr = new StringRequest(Request.Method.POST, " http://api.lastingsales.com/api/auth/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
//                Toast.makeText(context, "Request Completed", Toast.LENGTH_SHORT).show();

                pdLoading.dismiss();

                sessionManager.loginnUser(response, Calendar.getInstance().getTimeInMillis(),number);

                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdLoading.dismiss();

                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                Toast.makeText(activity, "Error Loging In", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", number);
                params.put("password", password);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

    }


}
