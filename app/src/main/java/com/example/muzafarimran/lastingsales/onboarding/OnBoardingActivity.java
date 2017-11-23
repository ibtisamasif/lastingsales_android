package com.example.muzafarimran.lastingsales.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.muzafarimran.lastingsales.activities.LogInActivity;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ibtisam on 11/22/2017.
 */

public class OnBoardingActivity extends AppCompatActivity {
    private static final String TAG = "OnBoardingActivity";
    private ViewPager viewPager;
    private static RequestQueue queue;
    private SessionManager sessionManager;
    private String companyName;
    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fm = getSupportFragmentManager();
        TutorialsFragmentPagerAdapter tutorialsFragmentPagerAdapter = new TutorialsFragmentPagerAdapter(fm);
        viewPager.setAdapter(tutorialsFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        queue = Volley.newRequestQueue(OnBoardingActivity.this, new HurlStack());
        sessionManager = new SessionManager(getApplicationContext());
    }

    public class TutorialsFragmentPagerAdapter extends FragmentPagerAdapter {

        final int TAB_COUNT = 7;

        public TutorialsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = FragmentB.newInstance("title", "page");
                    break;
                case 1:
                    fragment = FragmentC.newInstance("title", "page");
                    break;
                case 2:
                    fragment = FragmentD.newInstance("title", "page");
                    break;
                case 3:
                    fragment = FragmentE.newInstance("title", "page");
                    break;
                case 4:
                    fragment = FragmentF.newInstance("title", "page");
                    break;
                case 5:
                    fragment = FragmentG.newInstance("title", "page");
                    break;
                case 6:
                    fragment = FragmentH.newInstance("title", "page");
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "B";
                case 1:
                    return "C";
                case 2:
                    return "D";
                case 3:
                    return "E";
                case 4:
                    return "F";
                case 5:
                    return "G";
                case 6:
                    return "H";
                default:
                    return null;
            }
        }

    }

    public void moveToFragment(int i){
        viewPager.setCurrentItem(i);
    }

    public void dataFromFragmentE(String companyName, String email) {

        this.companyName = companyName;
        this.email = email;

    }


    public void dataFromFragmentF(String firstname, String lastname, String phone, String password, String confirmpassword) {

        moveToFragment(5);

        final int MY_SOCKET_TIMEOUT_MS = 60000;
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
                        Toast.makeText(OnBoardingActivity.this, "Successfully Signup", Toast.LENGTH_SHORT).show();
                        makeCreateCompanyRequest(OnBoardingActivity.this, companyName);
//                        activity.startActivity(new Intent(activity, CreateCompanyActivity.class));
//                        activity.finish();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment != null && fragment.isVisible() && fragment instanceof FragmentG) {
                                ((FragmentG) fragment).onUserSuccess();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                FragmentG fg = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null && fragment.isVisible() && fragment instanceof FragmentG) {
                        fg = (FragmentG) fragment;
                    }
                }
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_LONG).show();
                    fg.onUserError("No Internet");
                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 412) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                                int responseCode = jObj.getInt("responseCode");
                                String response = jObj.getString("response");
                                Toast.makeText(OnBoardingActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                                fg.onUserError(response);
                            } else {
                                fg.onUserError("Server Error");
                                Toast.makeText(OnBoardingActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            fg.onUserError("Poor Internet");
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
                params.put("phone", phone);
                params.put("cell_number", phone);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void makeCreateCompanyRequest(OnBoardingActivity onBoardingActivity, String companyName) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_COMPANY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse() called with: response = [" + response + "]");
                try {
                    JSONObject jObj = new JSONObject(response);
                    int responseCode = jObj.getInt("responseCode");

                    if (responseCode == 200) {
                        sessionManager.setKeyInitCompanyCreated("yes");
                        sessionManager.setKeyInitAccountTypeSelected("individual");
                        Toast.makeText(OnBoardingActivity.this, "Successfully Created Company", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(OnBoardingActivity.this, LogInActivity.class));
//                        finish();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        for (Fragment fragment : fragmentManager.getFragments()) {
                            if (fragment != null && fragment.isVisible() && fragment instanceof FragmentG) {
                                ((FragmentG) fragment).onCompanySuccess();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                FragmentG fg = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment != null && fragment.isVisible() && fragment instanceof FragmentG) {
                        fg = (FragmentG) fragment;
                    }
                }
                Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
                if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_LONG).show();
                    fg.onCompanyError("No Internet");
                } else {
                    try {
                        if (error.networkResponse != null) {
                            if (error.networkResponse.statusCode == 412) {
                                JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                                int responseCode = jObj.getInt("responseCode");
                                String response = jObj.getString("response");
                                fg.onCompanyError(response);
                                Toast.makeText(OnBoardingActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                            } else {
                                fg.onCompanyError("Server Error");
                                Toast.makeText(OnBoardingActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            fg.onCompanyError("Poor Internet");
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
