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
import com.example.muzafarimran.lastingsales.migration.VersionManager;
import com.example.muzafarimran.lastingsales.sync.DataSenderAsync;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.example.muzafarimran.lastingsales.utilscallprocessing.TheCallLogEngine;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    public static final String TAG = "LogInActivity";
    ProgressDialog pdLoading;
    EditText etEmail, etPassword;
    LinearLayout llSignup;
    String email, password;
    Button loginButton;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

//        Version Control
        VersionManager versionManager = new VersionManager(getApplicationContext());
        if (!versionManager.runMigrations()) {
            // if migration has failed
            Toast.makeText(getApplicationContext(), "Migration Failed", Toast.LENGTH_SHORT).show();
        }

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
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        llSignup = (LinearLayout) findViewById(R.id.llSignup);
        etEmail.getBackground().clearColorFilter();
        etPassword.getBackground().clearColorFilter();

//        hardcoding number and password for develoment speedup purposes
//        etEmail.setText("khurramrahim@gmail.com");
//        etPassword.setText("111111");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etEmail.setError(null);
                etPassword.setError(null);
                Boolean emailVarified = true, passwordVarified = true;
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();

                if (email.length() < 7) {
                    emailVarified = false;
                }
                if (password.length() < 4) {
                    passwordVarified = false;
                }
//                if (!PhoneNumberAndCallUtils.isValidPassword(password)) {
//                    passwordVarified = false;
//                }
                if (!emailVarified) {
                    etEmail.setError("Invalid Email!");
                }
                if (!passwordVarified) {
                    etPassword.setError("Invalid Password!");
                }
                if (emailVarified && passwordVarified) {
                    pdLoading.show();
                    makeLoginRequest(LogInActivity.this, email, password);
                }
            }
        });

        llSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                Toast.makeText(LogInActivity.this, "Signup", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (pdLoading != null && pdLoading.isShowing()) {
            pdLoading.dismiss();
        }
        super.onDestroy();
    }

    public void makeLoginRequest(final Activity activity, final String number, final String password) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        RequestQueue queue = Volley.newRequestQueue(activity, new HurlStack());
//        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
//                pdLoading.dismiss();
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
//                        String hash_key = responseObject.getString("hash_key");
                        String phone = responseObject.getString("phone");
                        String image = responseObject.getString("image");
//                        String created_by = responseObject.getString("created_by");
//                        String updated_by = responseObject.getString("updated_by");
                        String image_path = responseObject.getString("image_path");
                        String api_token = responseObject.getString("api_token");

                        String company_id = null;
                        String company_name = null;
                        if (responseObject.has("company_id") && !responseObject.isNull("company_id")){
                            JSONObject companyObject = responseObject.getJSONObject("company");
                            company_id = companyObject.getString("id");
                            company_name = companyObject.getString("name");
                        }
                        String role_id = null;
                        String role_role = null;
                        if (responseObject.has("role_id") && !responseObject.isNull("role_id")){
                            JSONObject roleObject = responseObject.getJSONObject("role");
                            role_id = roleObject.getString("id");
                            role_role = roleObject.getString("role");
                        }

                        String completeImagePath = MyURLs.IMAGE_URL + image_path;
                        sessionManager.loginnUser(user_id, api_token, Calendar.getInstance().getTimeInMillis(), number, firstname, lastname, completeImagePath, email, company_id, company_name, role_id, role_role);
                        sessionManager.getKeyLoginFirebaseRegId(); // Todo something fishy here
                        updateAgentFirebaseIdToServer(activity);

                        if(company_id == null){
                            activity.startActivity(new Intent(activity, CreateCompanyActivity.class));
                            activity.finish();
                        }else {
                            activity.startActivity(new Intent(activity, NavigationDrawerActivity.class));
                            activity.finish();
                        }
                        Log.d(TAG, "onResponse: " + response);
                        Toast.makeText(activity, "" + user_id, Toast.LENGTH_SHORT).show();
                        try {
                            String projectToken = MixpanelConfig.projectToken;
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                            mixpanel.identify(user_id);
                            mixpanel.getPeople().identify(user_id);

                            JSONObject props = new JSONObject();

                            props.put("$first_name", "" + firstname);
                            props.put("$last_name", "" + lastname);
                            props.put("$email", "" + email);
                            props.put("role", "" + role_role);
                            props.put("company_name", "" + company_name);
                            props.put("company_id", "" + company_id);
                            props.put("activated", "yes");

                            mixpanel.getPeople().set(props);

                            mixpanel.track("User Logged in", props);
                        } catch (JSONException e) {
                            Log.e("mixpanel", "Unable to add properties to JSONObject", e);
                        }
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
                            if (error.networkResponse.statusCode == 400) {
                                Toast.makeText(activity, "Wrong Password.", Toast.LENGTH_SHORT).show();
                            } else if (error.networkResponse.statusCode == 404) {
                                Toast.makeText(activity, "User Does not Exist.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "Server Error.", Toast.LENGTH_SHORT).show();
                            }
                        }else {
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
                params.put("email", number);
                params.put("password", password);
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
                .appendQueryParameter("device_id", "" + sessionManager.getKeyLoginFirebaseRegId())
                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                .build();
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() updateAgent: response = [" + response + "]");
                try {
                    if (pdLoading != null && pdLoading.isShowing()) {
                        pdLoading.dismiss();
                    }
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");
                    if (responseCode == 200) {
                        JSONObject responseObject = jObj.getJSONObject("response");
                        Log.d(TAG, "onResponse : FirebaseLocalRegID : " + sessionManager.getKeyLoginFirebaseRegId());
                        Log.d(TAG, "onResponse : FirebaseServerRegID : " + responseObject.getString("device_id"));

//                        RecordingManager recordingManager = new RecordingManager();
//                        recordingManager.execute();
                        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
                        theCallLogEngine.execute();
                        DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                        dataSenderAsync.run();


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
                Log.d(TAG, "onErrorResponse: CouldNotSyncAgentFirebaseRegId");

//                RecordingManager recordingManager = new RecordingManager();
//                recordingManager.execute();
                TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
                theCallLogEngine.execute();
                DataSenderAsync dataSenderAsync = DataSenderAsync.getInstance(getApplicationContext());
                dataSenderAsync.run();
            }
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }
}
