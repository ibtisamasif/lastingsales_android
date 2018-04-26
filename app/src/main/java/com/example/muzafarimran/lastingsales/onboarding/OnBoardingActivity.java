package com.example.muzafarimran.lastingsales.onboarding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muzafarimran.lastingsales.R;
import com.example.muzafarimran.lastingsales.SessionManager;
import com.example.muzafarimran.lastingsales.activities.CreateCompanyActivity;
import com.example.muzafarimran.lastingsales.activities.LogInActivity;
import com.example.muzafarimran.lastingsales.activities.NavigationBottomMainActivity;
import com.example.muzafarimran.lastingsales.app.MixpanelConfig;
import com.example.muzafarimran.lastingsales.customview.CustomViewPager;
import com.example.muzafarimran.lastingsales.sync.MyURLs;
import com.example.muzafarimran.lastingsales.utils.NetworkAccess;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OnBoardingActivity extends AppCompatActivity {
    private static final String TAG = "OnBoardingActivity";
    private LinearLayout indicator;
    private int mDotCount;
    private LinearLayout[] mDots;
    private CustomViewPager viewPager;
    private TutorialsFragmentPagerAdapter tutorialsFragmentPagerAdapter;
    private static RequestQueue queue;
    private SessionManager sessionManager;
    private String companyName;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String phone;
    private String confirmpassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        indicator = (LinearLayout) findViewById(R.id.indicators);
        viewPager = findViewById(R.id.viewpager);
        FragmentManager fm = getSupportFragmentManager();
        tutorialsFragmentPagerAdapter = new TutorialsFragmentPagerAdapter(fm);
        viewPager.setAdapter(tutorialsFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        queue = Volley.newRequestQueue(OnBoardingActivity.this, new HurlStack());
        sessionManager = new SessionManager(getApplicationContext());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    viewPager.setPagingEnabled(true);
                } else if (position == 2) {
                    viewPager.setPagingEnabled(true);
                } else if (position == 3) {
                    viewPager.setPagingEnabled(false);
                } else if (position == 4) {
                    viewPager.setPagingEnabled(false);
                } else if (position == 5) {
                    viewPager.setPagingEnabled(false);
                } else if (position == 6) {
                    viewPager.setPagingEnabled(false);
                }

                for (int i = 0; i < mDotCount; i++) {
                    mDots[i].setBackgroundResource(R.drawable.nonselected_item);
                }
                mDots[position].setBackgroundResource(R.drawable.selected_item);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setUiPageViewController();
    }

    private void setUiPageViewController() {
        mDotCount = tutorialsFragmentPagerAdapter.getCount();
        mDots = new LinearLayout[mDotCount];

        for (int i = 0; i < mDotCount; i++) {
            mDots[i] = new LinearLayout(OnBoardingActivity.this);
            mDots[i].setBackgroundResource(R.drawable.nonselected_item);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);
            indicator.addView(mDots[i], params);
            mDots[0].setBackgroundResource(R.drawable.selected_item);
        }
    }

    public class TutorialsFragmentPagerAdapter extends FragmentPagerAdapter {

        final int TAB_COUNT = 7;

        TutorialsFragmentPagerAdapter(FragmentManager fm) {
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

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 1) {
            moveToFragment(0);
        } else if (viewPager.getCurrentItem() == 2) {
            moveToFragment(1);
        } else if (viewPager.getCurrentItem() == 3) {
            moveToFragment(2);
        } else if (viewPager.getCurrentItem() == 4) {
            moveToFragment(3);
        } else if (viewPager.getCurrentItem() == 5) {
            moveToFragment(4);
        } else {
            super.onBackPressed();
        }
    }

    public void moveToFragment(int i) {
        viewPager.setCurrentItem(i);
    }

    public void dataFromFragmentE(String companyName, String email) {

        this.companyName = companyName;
        this.email = email;
        moveToFragment(4);

    }

    public void dataFromFragmentF(String firstname, String lastname, String phone, String password, String confirmpassword) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.password = password;
        this.confirmpassword = confirmpassword;
        this.password = password;
        moveToFragment(5);
        registerUserRequest();
    }

    public void dataFromFragmentG() {
        registerUserRequest();
    }

    private void registerUserRequest() {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.SIGNUP_URL, response -> {
            Log.d(TAG, "onResponse() called with: response = [" + response + "]");
//                pdLoading.dismiss();
            try {
                JSONObject jObj = new JSONObject(response);
                int responseCode = jObj.getInt("responseCode");
                if (responseCode == 200) {
                    Log.d(TAG, "SignupActivityonResponse200: ");
                    JSONObject responseObject = jObj.getJSONObject("response");
//                    String user_id = responseObject.getString("id");
//                    String firstname1 = responseObject.getString("firstname");
//                    String lastname1 = responseObject.getString("lastname");
//                    String email = responseObject.getString("email");
//                    String phone1 = responseObject.getString("phone");
//                    String cell_number = responseObject.getString("cell_number");
                    String api_token = responseObject.getString("api_token");
//                    String image = responseObject.getString("image");
//                    String image_path = responseObject.getString("image_path");
//                    String completeImagePath = MyURLs.IMAGE_URL + image_path;
                    sessionManager.setLoginToken(api_token);
                    Toast.makeText(OnBoardingActivity.this, "Successfully Signup", Toast.LENGTH_SHORT).show();
                    registerCompanyRequest(companyName);
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
        }, error -> {
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
                if (fg != null) {
                    fg.onUserError("No Internet");
                }
            } else {
                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 412) { // Invalid email responseCode:222 OR Pass must be greater than 4 char ResponseCode:220 // should move to email correction screen FragE
                            JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                            int responseCode = jObj.getInt("responseCode");
                            String response = jObj.getString("response");
                            Toast.makeText(OnBoardingActivity.this, "" + response, Toast.LENGTH_LONG).show();
                            if (fg != null) {
                                fg.onUserError(response, responseCode);
                            }
                        } else if (error.networkResponse.statusCode == 409) { // Email is already registered with us ResponseCode:190 //should move to email correction screen FragE
                            JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
                            int responseCode = jObj.getInt("responseCode");
                            String response = jObj.getString("response");
                            Toast.makeText(OnBoardingActivity.this, "" + response, Toast.LENGTH_LONG).show();
                            if (fg != null) {
                                fg.onUserError(response, responseCode);
                            }
                        } else { // find error and do accordingly
                            if (fg != null) {
                                fg.onUserError("Server Error");
                            }
                            Toast.makeText(OnBoardingActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                        }
                    } else { // try again
                        if (fg != null) {
                            fg.onUserError("Poor Internet");
                        }
                        Toast.makeText(getApplicationContext(), "Poor Internet Connectivity", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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

    public void dataFromFragmentH() {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.LOGIN_URL, response -> {
            Log.d(TAG, "onResponse() makeLoginRequest called with: response = [" + response + "]");
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
//                    String phone = responseObject.getString("phone");
//                    String image = responseObject.getString("image");
//                        String created_by = responseObject.getString("created_by");
//                        String updated_by = responseObject.getString("updated_by");
                    String image_path = responseObject.getString("image_path");
                    String api_token = responseObject.getString("api_token");

                    String company_id = null;
                    String company_name = null;
                    if (responseObject.has("company_id") && !responseObject.isNull("company_id")) {
                        JSONObject companyObject = responseObject.getJSONObject("company");
                        company_id = companyObject.getString("id");
                        company_name = companyObject.getString("name");
                    }
                    String role_id = null;
                    String role_role = null;
                    if (responseObject.has("role_id") && !responseObject.isNull("role_id")) {
                        JSONObject roleObject = responseObject.getJSONObject("role");
                        role_id = roleObject.getString("id");
                        role_role = roleObject.getString("role");
                    }


                    JSONObject returnInitJson = null;
                    if (responseObject.has("config") && !responseObject.isNull("config")) {
                        String config = responseObject.getString("config");
                        Log.d(TAG, "onResponse: config: " + config);

                        JSONObject jsonobject = new JSONObject(config);

                        String init_completed = jsonobject.getString("init_completed");
                        String init_team_added = jsonobject.getString("init_team_added");
                        String init_app_downloaded = jsonobject.getString("init_app_downloaded");
                        String init_company_created = jsonobject.getString("init_company_created");
                        String init_account_type_selected = jsonobject.getString("init_account_type_selected");

                        Log.d(TAG, "init_completed: " + init_completed);
                        Log.d(TAG, "init_team_added: " + init_team_added);
                        Log.d(TAG, "init_app_downloaded: " + init_app_downloaded);
                        Log.d(TAG, "init_company_created: " + init_company_created);
                        Log.d(TAG, "init_account_type_selected: " + init_account_type_selected);

//                            sessionManager.setKeyInitCompleted(init_completed);
//                            sessionManager.setKeyInitTeamAdded(init_team_added);
//                            sessionManager.setKeyInitAppDownloaded(init_app_downloaded);
//                            sessionManager.setKeyInitCompanyCreated(init_company_created);
//                            sessionManager.setKeyInitAccountTypeSelected(init_account_type_selected);

                        //Changing Values
                        sessionManager.setKeyInitCompleted("yes");
                        sessionManager.setKeyInitAppDownloaded("yes");

                        returnInitJson = new JSONObject();
                        try {
                            returnInitJson.put(SessionManager.KEY_INIT_COMPLETED, sessionManager.getKeyInitCompleted());
                            returnInitJson.put(SessionManager.KEY_INIT_TEAM_ADDED, sessionManager.getKeyInitTeamAdded());
                            returnInitJson.put(SessionManager.KEY_INIT_APP_DOWNLOADED, sessionManager.getKeyInitAppDownloaded());
                            returnInitJson.put(SessionManager.KEY_INIT_COMPANY_CREATED, sessionManager.getKeyInitCompanyCreated());
                            returnInitJson.put(SessionManager.KEY_INIT_ACCOUNT_TYPE_SELECTED, sessionManager.getKeyInitAccountTypeSelected());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSONException: " + e);
                        }

                        Log.d(TAG, "returnInitJson: " + returnInitJson);
//                            updateFirebaseIdAndInitConfigMakeRequest(LogInActivity.this, returnInitJson);

                    }

                    String completeImagePath = MyURLs.IMAGE_URL + image_path;
                    sessionManager.loginnUser(user_id, api_token, Calendar.getInstance().getTimeInMillis(), email, firstname, lastname, completeImagePath, email, company_id, company_name, role_id, role_role);
                    sessionManager.getKeyLoginFirebaseRegId();
                    updateFirebaseIdAndInitConfigMakeRequest(returnInitJson);
//                        updateAgentFirebaseIdToServer(activity);

                    if (company_id == null) {
                        startActivity(new Intent(OnBoardingActivity.this, CreateCompanyActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(OnBoardingActivity.this, NavigationBottomMainActivity.class));
                        LogInActivity.activity.finish();
                        finish();
                    }
                    Log.d(TAG, "onResponse: " + response);
                    Toast.makeText(OnBoardingActivity.this, "" + user_id, Toast.LENGTH_SHORT).show();
                    try {

                        String projectToken = MixpanelConfig.projectToken;
                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
                        MixpanelAPI.People people = mixpanel.getPeople();
                        people.identify(user_id);
                        people.initPushHandling("44843550731");

//                            String projectToken = MixpanelConfig.projectToken;
//                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getApplicationContext(), projectToken);
//                            mixpanel.identify(user_id);
//                            mixpanel.getPeople().identify(user_id);

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
        }, error -> {
            Log.d(TAG, "onErrorResponse() called with: error = [" + error + "]");
            if (!NetworkAccess.isNetworkAvailable(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "Turn on wifi or Mobile Data", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 400) {
                            Toast.makeText(OnBoardingActivity.this, "Wrong Password.", Toast.LENGTH_SHORT).show();
                        } else if (error.networkResponse.statusCode == 404) {
                            Toast.makeText(OnBoardingActivity.this, "User Does not Exist.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OnBoardingActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Poor Internet Connectivity", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
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

    private void updateFirebaseIdAndInitConfigMakeRequest(@Nullable JSONObject returnInitJson) {

        final int MY_SOCKET_TIMEOUT_MS = 60000;
        final String BASE_URL = MyURLs.UPDATE_AGENT;
        Uri builtUri;
        if (returnInitJson != null) {
            builtUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("config", "" + returnInitJson)
                    .appendQueryParameter("device_id", "" + sessionManager.getKeyLoginFirebaseRegId())
                    .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                    .build();

        } else {
            builtUri = Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter("device_id", "" + sessionManager.getKeyLoginFirebaseRegId())
                    .appendQueryParameter("api_token", "" + sessionManager.getLoginToken())
                    .build();
        }
        final String myUrl = builtUri.toString();
        StringRequest sr = new StringRequest(Request.Method.PUT, myUrl, response -> {
            Log.d(TAG, "onResponse() updateFirebaseIdAndInitConfigMakeRequest: response = [" + response + "]");
            try {
                JSONObject jObj = new JSONObject(response);
                int responseCode = jObj.getInt("responseCode");
                if (responseCode == 200) {

                    JSONObject responseObject = jObj.getJSONObject("response");
                    Log.d(TAG, "onResponse : FirebaseLocalRegID : " + sessionManager.getKeyLoginFirebaseRegId());
                    Log.d(TAG, "onResponse : FirebaseServerRegID : " + responseObject.getString("device_id"));

//                        TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
//                        theCallLogEngine.execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d(TAG, "onErrorResponse: CouldNotUpdateInitConfigMakeRequest OR CouldNotSyncAgentFirebaseRegId");

//                RecordingManager recordingManager = new RecordingManager();
//                recordingManager.execute();
//            TheCallLogEngine theCallLogEngine = new TheCallLogEngine(getApplicationContext());
//            theCallLogEngine.execute();
        }) {
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(sr);
    }

    private void registerCompanyRequest(String companyName) {
        final int MY_SOCKET_TIMEOUT_MS = 60000;
        StringRequest sr = new StringRequest(Request.Method.POST, MyURLs.ADD_COMPANY_URL, response -> {
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
        }, error -> {
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
                if (fg != null) {
                    fg.onCompanyError("No Internet");
                }
            } else {
                try {
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 412) {
                            JSONObject jObj = new JSONObject(new String(error.networkResponse.data));
//                                int responseCode = jObj.getInt("responseCode");
                            String response = jObj.getString("response");
                            if (fg != null) {
                                fg.onCompanyError(response);
                            }
                            Toast.makeText(OnBoardingActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                        } else {
                            if (fg != null) {
                                fg.onCompanyError("Server Error");
                            }
                            Toast.makeText(OnBoardingActivity.this, "Server Error.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (fg != null) {
                            fg.onCompanyError("Poor Internet");
                        }
                        Toast.makeText(getApplicationContext(), "Poor Internet Connectivity", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
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
