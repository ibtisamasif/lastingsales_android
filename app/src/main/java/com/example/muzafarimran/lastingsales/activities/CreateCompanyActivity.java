package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ibtisam on 5/19/2017.
 */

public class CreateCompanyActivity extends AppCompatActivity {
    private static final String TAG = "CreateCompanyActivity";
    private EditText etCompanyName;
    private Button bCreateCompany;
    private LinearLayout llLogin;
    private String companyName;
    private SessionManager sessionManager;
    private ProgressDialog pdLoading;
    private static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_company);
        queue = Volley.newRequestQueue(CreateCompanyActivity.this, new HurlStack());
        sessionManager = new SessionManager(getApplicationContext());
        etCompanyName = (EditText) findViewById(R.id.etFirstName);
        bCreateCompany = (Button) findViewById(R.id.bCreateCompany);
        llLogin = (LinearLayout) findViewById(R.id.llLogin);
        pdLoading = new ProgressDialog(this);
        pdLoading.setTitle("Loading data");
        //this method will be running on UI thread
        pdLoading.setMessage("Please Wait...");
//        etCompanyName.getBackground().clearColorFilter();

        // hardcoding for develoment speedup purposes
//        etCompanyName.setText("TheDesignerX21");

        bCreateCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bCreateCompany.setError(null);
                Boolean companyVerified = false;
                companyName = etCompanyName.getText().toString();
                if (companyName.length() > 3) {
                    companyVerified = true;
                }
                if (!companyVerified) {
                    etCompanyName.setError("Company Name too short!");
                }
                if (companyVerified) {
                    pdLoading.show();
                    makeCreateCompanyRequest(CreateCompanyActivity.this, companyName);
                }
            }
        });


        llLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateCompanyActivity.this, LogInActivity.class));
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

    private void makeCreateCompanyRequest(final Activity activity, final String companyName) {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
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
                        Toast.makeText(activity, "Company Created Successfully", Toast.LENGTH_SHORT).show();
//                        makeGetUserInfoRequest(CreateCompanyActivity.this);
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
                                Toast.makeText(activity, "" + response, Toast.LENGTH_SHORT).show();
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

//    private void makeGetUserInfoRequest(final Activity mContext) {
//
//        Log.d(TAG, "makeGetUserInfoRequest: getting...");
//        final int MY_SOCKET_TIMEOUT_MS = 60000;
//        final String BASE_URL = MyURLs.GET_USER_INFO;
//        Uri builtUri = Uri.parse(BASE_URL)
//                .buildUpon()
//                .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
//                .build();
//        final String myUrl = builtUri.toString();
//        Log.d(TAG, "makeGetUserInfoRequest: MYURL: " + myUrl);
//        StringRequest sr = new StringRequest(Request.Method.GET, myUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "makeGetUserInfoRequest onResponse(): "+ response);
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    int responseCode = jObj.getInt("responseCode");
//                    if (responseCode == 200) {
//                        Toast.makeText(getApplicationContext(), "Initialized User Info Successfully", Toast.LENGTH_SHORT).show();
//                        makeGetUserInfoRequest(CreateCompanyActivity.this);
////                        activity.startActivity(new Intent(activity, LogInActivity.class));
////                        activity.finish();
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Log.d(TAG, "onErrorResponse: CouldNotGetUserInfo");
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
////                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        sr.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(sr);
//    }

}
