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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;

import org.json.JSONException;
import org.json.JSONObject;

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
            startActivity(new Intent(getApplicationContext(), NavigationDrawerActivity.class));
            finish();
        }

        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        //this method will be running on UI thread
        pdLoading.setMessage("Please Wait...");
        loginButton = (Button) findViewById(R.id.loginButtonLoginScreen);
        tvNumber = (TextView) findViewById(R.id.numberLoginScreen);
        tvPassword = (TextView) findViewById(R.id.passwordLoginScreen);
        tvNumber.getBackground().clearColorFilter();
        tvPassword.getBackground().clearColorFilter();
//        hardcoding number and password for develoment speedup purposes
//        tvNumber.setText("03361124888");
//        tvPassword.setText("LastingSales123@");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNumber.setError(null);
                tvPassword.setError(null);
                Boolean numberVarified = true, passwordVarified = true;
                number = tvNumber.getText().toString();
                password = tvPassword.getText().toString();

//                intlNumber = "ibtisamasif1@gmail.com";
//                password = "11111111";

                makeLoginRequest(LogInActivity.this, number, password);
                pdLoading.show();
//                if (number.length() < 7) {
//                    numberVarified = false;
//                }
//                String intlNumber = PhoneNumberAndCallUtils.numberToInterNationalNumber(number);
//                if (intlNumber != null) {
//                    if (intlNumber.length() < 15) {
//                        numberVarified = false;
//                    }
//                } else {
//                    numberVarified = false;
//                }
//                if (password.length() < 8) {
//                    passwordVarified = false;
//                }
//                if (!PhoneNumberAndCallUtils.isValidPassword(password)) {
//                    passwordVarified = false;
//                }
//                if (!numberVarified) {
//                    tvNumber.setError("Invalid Number!");
//                }
//                if (!passwordVarified) {
//                    tvPassword.setError("Invalid Password!");
//                }
//                if (numberVarified && passwordVarified) {
//                    pdLoading.show();
//                    intlNumber = "ibtisamasif1@gmail.com";
//                    password = "11111111";
//                    makeLoginRequest(LogInActivity.this, intlNumber, password);
//
//                }
            }
        });
    }

    public void makeLoginRequest(final Activity activity, final String number, final String password) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
//                pdLoading.dismiss();
                //TODO pass token in place of response
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
//                    Toast.makeText(getApplicationContext(), "response: "+response.toString(), Toast.LENGTH_LONG).show();

                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");

                        String user_id = responseObject.getString("id");
                        String firstname = responseObject.getString("firstname");
                        String lastname = responseObject.getString("lastname");
                        String email = responseObject.getString("email");
                        String hash_key = responseObject.getString("hash_key");
                        String phone = responseObject.getString("phone");
                        String image = responseObject.getString("image");
                        String image_path = responseObject.getString("image_path");
                        String api_token = responseObject.getString("api_token");
                        String role_id = responseObject.getString("role_id");

                        String completeImagePath = MyURLs.IMAGE_URL + image_path;
                        sessionManager.loginnUser(user_id, api_token, Calendar.getInstance().getTimeInMillis(), number, firstname, lastname, completeImagePath);
                        sessionManager.getKeyLoginFirebaseRegId();
                        updateAgentFirebaseIdToServer(activity);

                        activity.startActivity(new Intent(activity, NavigationDrawerActivity.class));
                        activity.finish();

                        Log.d(TAG, "onResponse: "+response);
                        Toast.makeText(activity, "" + user_id, Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 3) {
                        Toast.makeText(activity, "No User Found.", Toast.LENGTH_SHORT).show();
                    }else if (responseCode == 4){
                        Toast.makeText(activity, "inValid Password", Toast.LENGTH_SHORT).show();
                    }
                    else if (responseCode == 5){
                        Toast.makeText(activity, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdLoading.dismiss();
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", number);
                params.put("password", password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    public void updateAgentFirebaseIdToServer(final Activity activity) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(activity);
        final String BASE_URL = MyURLs.UPDATE_AGENT;
        Uri builtUri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendQueryParameter("device_id", ""+sessionManager.getKeyLoginFirebaseRegId())
                .appendQueryParameter("api_token", ""+sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateAgent: response = [" + response + "]");
                try {
                    pdLoading.dismiss();
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode ==  200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        Log.d(TAG, "onResponse : FirebaseLocalRegID : " +sessionManager.getKeyLoginFirebaseRegId());
                        Log.d(TAG, "onResponse : FirebaseServerRegID : " +responseObject.getString("device_id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pdLoading.dismiss();
                error.printStackTrace();
                Log.d(TAG, "onErrorResponse: CouldNotSyncAgentFirebaseRegId");
            }
        }){};
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }
}
